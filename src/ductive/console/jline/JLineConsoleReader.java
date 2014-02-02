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
package ductive.console.jline;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Provider;

import jline.Terminal;
import jline.console.ConsoleReader;

import org.fusesource.jansi.Ansi;

public class JLineConsoleReader extends ConsoleReader {

	private static final Provider<Ansi> DEFAULT_PROMPT = new Provider<Ansi>() {
		public Ansi get() { return new Ansi().a("> ").reset(); }
	};
	
	private Provider<Ansi> prompt = DEFAULT_PROMPT;
	
	public JLineConsoleReader(InputStream in, OutputStream out, Terminal term) throws IOException {
		super(in, out, term);
	}
	
	public void setPrompt(Provider<Ansi> prompt) {
		this.prompt = prompt;
	}
	
	@Override public String getPrompt() {
		setPrompt(prompt.get().toString());
		return super.getPrompt();
	}

}
