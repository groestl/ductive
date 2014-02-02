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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import com.google.common.base.Throwables;

import ductive.console.commands.parser.CmdParser;
import ductive.console.commands.parser.model.CommandLine;
import ductive.console.commands.register.CommandContext;
import ductive.console.commands.register.CommandInvoker;
import ductive.console.jline.NonInteractiveTerminal;
import ductive.log.LogContext;

public class CommandRunner implements Command {
	
	private CmdParser cmdParser;
	private CommandInvoker commandInvoker;
	private String commandLine;

	private OutputStream out;
	private OutputStream err;
	private ExitCallback callback;
	private InputStream in;
	
	private boolean closed;

	private AtomicInteger exitCode = new AtomicInteger(0);
	private ExecutorService executor;
	
	
	
	public CommandRunner(CmdParser cmdParser, CommandInvoker commandInvoker, String commandLine) {
		this.commandInvoker = commandInvoker;
		executor = Executors.newSingleThreadExecutor();
		this.cmdParser = cmdParser;
		this.commandLine = commandLine;
	}

	@Override
	public void start(final Environment env) throws IOException {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try(LogContext ctx = new LogContext("remote-exec")) {
					ctx.put("user",env.getEnv().get(Environment.ENV_USER));
					
					NonInteractiveTerminal terminal = new NonInteractiveTerminal(out);
					CommandContext commandCtx = new CommandContext(terminal);
					try {
						CommandLine line = cmdParser.parse(commandLine);
						commandInvoker.execute(commandCtx,line);
					} catch(Exception x) {
						try(PrintStream p = new PrintStream(terminal.error())) {
							x.printStackTrace(p);
						}
						exitCode.set(1);
					}
					
					destroy();
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}
		});
	}
	
	@Override
	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	@Override
	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void destroy() {
		if (!closed) {
			closed = true;
			flush(out, err);
			close(in, out, err);
			callback.onExit(exitCode.get());
		}
	}

	private static void flush(OutputStream... streams) {
		for (OutputStream s : streams) {
			try {
				s.flush();
			} catch (IOException e) { /* noop */ }
		}
	}

	private static void close(Closeable... closeables) {
		for (Closeable c : closeables) {
			try {
				c.close();
			} catch (IOException e) { /* noop */ 	}
		}
	}
}
