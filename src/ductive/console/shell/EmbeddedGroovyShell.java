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

import groovy.lang.Binding;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.springframework.beans.factory.annotation.Autowired;

import ductive.commons.Names;
import ductive.console.groovy.GroovyInterpreter;
import ductive.console.groovy.ReflectionCompleter;

public class EmbeddedGroovyShell implements Shell {

	private static final Ansi DEFAULT_STANDARD_PROMPT = new Ansi().reset().fgBright(Color.BLUE).a("gsh> ").reset();
	private static final Ansi DEFAULT_PENDING_PROMPT = new Ansi().reset().fgBright(Color.BLUE).a("...> ").reset();
	private static final Ansi DEFAULT_RESULT_MARKER = new Ansi().reset().fg(Color.GREEN).a("==>").fgBright(Color.GREEN).a(" %s").reset();

	private static final String HISTORY_KEY = Names.from(EmbeddedGroovyShell.class,"history");

	private Ansi standardPrompt = DEFAULT_STANDARD_PROMPT;
	private Ansi pendingPrompt = DEFAULT_PENDING_PROMPT;
	private Ansi resultMarker = DEFAULT_RESULT_MARKER;

	@Autowired private HistoryProvider historyProvider;

	@Override
	public void execute(InteractiveTerminal terminal) throws IOException {
		Binding binding = new Binding();
		binding.setVariable("foo", new Integer(2));

		CompilerConfiguration config = new CompilerConfiguration();
		binding.setProperty("out",new PrintStream(terminal.output(),true));
		// binding.setProperty("in",new BufferedReader(new InputStreamReader(terminal.input()))); FIXME:
		GroovyInterpreter interpreter = new GroovyInterpreter(binding,config);

		try(ShellHistory history = historyProvider.history(HISTORY_KEY)) {

			final AtomicBoolean pending = new AtomicBoolean(false);
			ShellSettings settings = new StaticShellSettings(new Provider<Ansi>() {
				@Override public Ansi get() { return pending.get() ? pendingPrompt : standardPrompt; }
			},new ReflectionCompleter(interpreter),history);
			terminal.updateSettings(settings);

			while (true) {
				String code = new CodeReader(terminal,pending).read();
				if (code == null) {
					terminal.println("");
					break;
				}

				if (StringUtils.isBlank(code))
					continue;

				try {
					Object result = interpreter.interpret("true\n" + code); // i don't know why dummy 'true' is required...
					terminal.println(String.format(resultMarker.toString(),result));
	//			} catch (CompilationFailedException e) {
	//				//terminal.error(e.toString());
	//				filterAndPrintStackTrace(e);
				} catch (Throwable e) {
					// Unroll invoker exceptions
					if (e instanceof InvokerInvocationException) {
						e = e.getCause();
					}

					PrintStream ps = new PrintStream(terminal.error());
					e.printStackTrace(ps);
					ps.flush();
				}
			}

		}
	}

	private class CodeReader {

		private InteractiveTerminal terminal;
		private AtomicBoolean isPending;

		StringBuilder accepted = new StringBuilder();
		String pending = null;
		Exception error = null;

		public CodeReader(InteractiveTerminal terminal, AtomicBoolean isPending) {
			this.terminal = terminal;
			this.isPending = isPending;
		}

		private String read() throws IOException {

			while (true) {
				pending = terminal.readLine();
				if (pending == null)
					return null;

				// TODO: command handling
				// String command = pending.trim();

				if (StringUtils.isBlank(pending)) {
					accept();
					continue;
				}

				final String code = current();

				if (parse(code)) {
					accept();
					isPending.set(false);
					return accepted.toString();
				}

				if (error == null) {
					accept();
				} else {
					report();
					return "";
				}
				//
			}
		}

		private void report() throws IOException {
			terminal.errorln("Discarding invalid text: " + error.toString()); // TODO:
			isPending.set(false);
		}

		private void accept() {
			accepted.append(pending).append('\n');
			if(!StringUtils.isBlank(accepted.toString()))
				isPending.set(true);
		}

		private String current() {
			return accepted.toString() + pending + '\n';
		}

		private boolean parse(String code) {
			return parse(code, 1);
		}

		private boolean parse(final String code, final int tolerance) {
			SourceUnit parser = null;
			try {
				parser = SourceUnit.create("groovysh-script", code, tolerance);
				parser.parse();
				return true;
			} catch (CompilationFailedException e) {
				if (parser.getErrorCollector().getErrorCount() > 1 || !parser.failedWithUnexpectedEOF()) {
					error = e;
				}
			} catch (Exception e) {
				error = e;
			}
			return false;
		}

	}

	public void setHistoryProvider(HistoryProvider historyProvider) {
		this.historyProvider = historyProvider;
	}

}
