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
package ductive.console.shell;


import java.io.IOException;
import java.io.PrintStream;

import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import ductive.commons.Names;
import ductive.console.commands.parser.CmdParser;
import ductive.console.commands.parser.model.CommandLine;
import ductive.console.commands.register.CommandContext;
import ductive.console.commands.register.CommandInvoker;
import ductive.console.config.StandardPromptProvider;
import ductive.parse.errors.NoMatchException;
import jline.console.UserInterruptException;

public class EmbeddedAppShell implements Shell {

	private static final Ansi DEFAULT_STANDARD_PROMPT = new Ansi().reset().bold().fg(Color.MAGENTA).a("app> ").reset();

	private static final String HISTORY_KEY = Names.from(EmbeddedAppShell.class,"history");

	private CommandInvoker commandInvoker;
	private HistoryProvider historyProvider;
	private Provider<CmdParser> cmdParserProvider;
	private StandardPromptProvider standardPromptProvider = new StandardPromptProvider() {
		@Override public Ansi get() {
			return DEFAULT_STANDARD_PROMPT;
		}
	};

	@Override
	public void execute(InteractiveTerminal terminal, TerminalUser user) throws IOException {
		CmdParser cmdParser = cmdParserProvider.get();
		try( ShellHistory history = historyProvider.history(HISTORY_KEY) ) {
			ShellSettings settings = new StaticShellSettings(standardPromptProvider,cmdParser,history);
			terminal.updateSettings(settings);

			while (true) {
				Integer result = execute(cmdParser,terminal,history,user);
				if(result != null)
					break;
			}
		}
	}

	private Integer execute(CmdParser cmdParser, InteractiveTerminal terminal, ShellHistory history, TerminalUser user) throws IOException {
		final CommandLine line;
		try {
			String command = terminal.readLine();

			if (command == null)
				return 0;

			if (StringUtils.isBlank(command))
				return null;

			line = cmdParser.parse(command);
		} catch(NoMatchException e) {
			terminal.errorln(e.getMessage());
			return null;
		}

		try {
			commandInvoker.execute(new CommandContext(terminal,user),line);
		} catch(UserInterruptException e) {
		} catch (Throwable e) {
			// Unroll invoker exceptions
			if (e.getClass().getCanonicalName().equals("org.codehaus.groovy.runtime.InvokerInvocationException")) {
				e = e.getCause();
			}

			PrintStream ps = new PrintStream(terminal.error());
			e.printStackTrace(ps);
			ps.flush();
		}
		return null;
	}

	public void setCmdParserProvider(Provider<CmdParser> cmdParserProvider) {
		this.cmdParserProvider = cmdParserProvider;
	}

	public void setCommandInvoker(CommandInvoker commandInvoker) {
		this.commandInvoker = commandInvoker;
	}

	public void setHistoryProvider(HistoryProvider historyProvider) {
		this.historyProvider = historyProvider;
	}

	public void setStandardPromptProvider(StandardPromptProvider standardPromptProvider) {
		this.standardPromptProvider = standardPromptProvider;
	}

}
