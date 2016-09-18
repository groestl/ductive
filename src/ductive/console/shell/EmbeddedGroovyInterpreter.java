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
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerInvocationException;

import ductive.console.groovy.GroovyInterpreter;
import groovy.lang.Binding;

public class EmbeddedGroovyInterpreter {

	private Map<String, Object> context;

	public void execute(Terminal terminal, TerminalUser user, String code) throws IOException {
		Binding binding = new Binding();

		if(context!=null)
			for(Entry<String,Object> e : context.entrySet())
				binding.setVariable(e.getKey(),e.getValue());

		CompilerConfiguration config = new CompilerConfiguration();
		binding.setProperty("out",new PrintStream(terminal.output(),true));
		//binding.setProperty("in",new BufferedReader(new InputStreamReader(terminal.input()))); FIXME:
		GroovyInterpreter interpreter = new GroovyInterpreter(binding,config);

		try {
			interpreter.interpret(code); // i don't know why dummy 'true' is required...
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

	public void setShellContext(Map<String,Object> context) {
		this.context = context;
	}

}
