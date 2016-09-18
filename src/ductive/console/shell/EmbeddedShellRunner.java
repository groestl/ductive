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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ductive.console.jline.JLineConsoleReader;
import ductive.console.jline.JLineInteractiveTerminal;
import ductive.console.sshd.SshTerminal;
import ductive.log.LogContext;

public class EmbeddedShellRunner implements Command {

	private static final Logger log = LoggerFactory.getLogger(EmbeddedShellRunner.class);

	private final Executor executor;

	private OutputStream err;
	private InputStream in;
	private OutputStream out;

	private boolean closed;

	private ExitCallback callback;

	private Shell shell;

	public EmbeddedShellRunner(Shell shell) {
		executor = Executors.newSingleThreadExecutor();
		this.shell = shell;
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
	public void start(final Environment env) throws IOException {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try(LogContext ctx = LogContext.create("remote-shell")) {
					String user = env.getEnv().get(Environment.ENV_USER);
					ctx.put("user",user);

					JLineConsoleReader r = new JLineConsoleReader(in,out,new SshTerminal(env));
					try {
						r.setExpandEvents(false);
						JLineInteractiveTerminal terminal = new JLineInteractiveTerminal(r,DefaultTerminalSettings.INSTANCE);
		
						shell.execute(terminal,new TerminalUser(user));
					} finally {
						r.shutdown();
					}
					

					destroy();
				} catch (Exception e) {
					if(log.isWarnEnabled())
						log.warn(String.format("exception in remote console thread: %s",e));

					if(log.isDebugEnabled())
						log.debug(String.format("exception in remote console thread:"),e);

					destroy();
				}
			}
		});
	}

	@Override
	public void destroy() {
		if (!closed) {
			closed = true;
			flush(out, err);
			close(in, out, err);
			callback.onExit(0);
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