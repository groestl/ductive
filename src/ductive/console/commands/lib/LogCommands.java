/*
 	Copyright (c) 2014 code.fm

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
 */
package ductive.console.commands.lib;


import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

import ductive.commons.SleepUtils;
import ductive.console.commands.register.annotations.Arg;
import ductive.console.commands.register.annotations.ArgParser;
import ductive.console.commands.register.annotations.Cmd;
import ductive.console.shell.InteractiveTerminal;
import ductive.console.shell.Terminal;
import ductive.parse.Parser;
import ductive.parse.Parsers;

public class LogCommands {

	private static final Logger log = LoggerFactory.getLogger(LogCommands.class);

	private static final String DEFAULT_LOG_PATTERN = "%d %5p [%c] %x%X %m%n";

	private static final int CR = 13;
	private static final int CTRL_C = 3;
	private static final int CTRL_D = 4;

	private String logPattern = DEFAULT_LOG_PATTERN;

	@Cmd(path={"log","set-level"},help="sets the threshold of a logger")
	public void setLevel(Terminal terminal, @Arg(value="logger",optional=true) String loggerName, @Arg(value="level",optional=true) Level level_) throws IOException {
		final org.apache.logging.log4j.Logger logger;
		if(StringUtils.isEmpty(loggerName))
			logger = org.apache.logging.log4j.LogManager.getRootLogger();
		else
			logger = org.apache.logging.log4j.LogManager.getLogger(loggerName);

		final org.apache.logging.log4j.Level level = level_!=null ? level_ : org.apache.logging.log4j.Level.TRACE;

		org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger)logger;
		coreLogger.setLevel(level);

		terminal.println(new Ansi().a("logger ").bold().a(logger.getName()).boldOff().a(" set to level ").bold().a(level).boldOff());
	}


	@Cmd(path={"log","attach"},help="checks health of app")
	public void logAttach(Terminal terminal, @Arg(value="logger",optional=true) String loggerName, @Arg(value="level",optional=true) Level level_) throws IOException {

		final org.apache.logging.log4j.Logger logger;
		if(StringUtils.isEmpty(loggerName))
			logger = org.apache.logging.log4j.LogManager.getRootLogger();
		else
			logger = org.apache.logging.log4j.LogManager.getLogger(loggerName);
		org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger)logger;

		Configuration config = coreLogger.getContext().getConfiguration();

		final Level level = level_!=null ? level_ : Level.TRACE;
		coreLogger.setLevel(level);

		PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern(logPattern).build();
		RemoteTerminalAppender appender = new RemoteTerminalAppender(terminal,UUID.randomUUID().toString(),null,layout,true);
		appender.start();

		coreLogger.addAppender(appender);

		try {
			if(log.isInfoEnabled())
				log.info(String.format("terminal attached to logger '%s'",StringUtils.defaultIfBlank(logger.getName(),"ROOT")));

			coreLogger.addAppender(appender);

			if(InteractiveTerminal.class.isInstance(terminal)) {
				terminal.println(new Ansi().fgBright(Color.WHITE).bold().a(String.format("---- catching %s on logger %s. press 'q', ^D or ^C to detach ----",level,StringUtils.defaultIfBlank(logger.getName(),"ROOT"))).reset());
				terminal.flush();

				InteractiveTerminal term = InteractiveTerminal.class.cast(terminal);
				while(true) {
					int c = term.readChar(10);

					if( c==CR ) {
						terminal.println("");
						terminal.flush();
					} else if(c==CTRL_C || c==CTRL_D || c=='q' || c=='Q' || appender.hasIoErrors() )
						break;
				}
			} else {
				terminal.println(new Ansi().fgBright(Color.WHITE).bold().a(String.format("---- catching %s on logger %s ----",level,StringUtils.defaultIfBlank(logger.getName(),"ROOT"))).reset());
				terminal.flush();
				while(!appender.hasIoErrors())
					SleepUtils.sleep(100);
			}
		} finally {
			coreLogger.removeAppender(appender);
			appender.stop();
			if(log.isInfoEnabled())
				log.info(String.format("terminal detached from logger '%s'",StringUtils.defaultIfBlank(logger.getName(),"ROOT")));
		}
	}

	private static class RemoteTerminalAppender extends org.apache.logging.log4j.core.appender.AbstractAppender {

		private Terminal terminal;
		private AtomicBoolean hasIoErrors = new AtomicBoolean(false);

		public RemoteTerminalAppender(Terminal terminal, String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
			super(name, filter, layout, ignoreExceptions);
			this.terminal = terminal;
		}

		@Override
		public void append(org.apache.logging.log4j.core.LogEvent ev) {
			if(ev.getLoggerName().startsWith("org.apache.sshd") && ev.getLevel().isLessSpecificThan(Level.INFO))
				return;

			String msg = new String(getLayout().toByteArray(ev));
			try {
				Ansi a = formatAnsi(new Ansi(),ev.getLevel()).a(msg);
				terminal.print(a.reset().toString());
				terminal.flush();
			} catch (IOException e) {
				hasIoErrors.set(true);
			}
		};

		public boolean hasIoErrors() {
			return hasIoErrors.get();
		}

		private static Ansi formatAnsi(Ansi a, Level level) {
			if(Level.TRACE.compareTo(level)<=0)
				return a.fg(Color.CYAN);
			if(Level.DEBUG.compareTo(level)<=0)
				return a.fgBright(Color.CYAN);
			if(Level.INFO.compareTo(level)<=0)
				return a.fgBright(Color.WHITE);
			if(Level.WARN.compareTo(level)<=0)
				return a.fgBright(Color.YELLOW);
			if(Level.ERROR.compareTo(level)<=0)
				return a.fg(Color.RED);
			return a.fgBright(Color.RED).bold();
		}

	}

	@ArgParser
	public Parser<Level> loglevel() {
		return Parsers.or(
				Parsers.istring("trace"),
				Parsers.istring("debug"),
				Parsers.istring("info"),
				Parsers.istring("warn"),
				Parsers.istring("error")
		).map(new Function<String,Level>() {
			@Override public Level apply(@Nullable String level) {
				return Level.toLevel(level);
			}
		});
	}

}
