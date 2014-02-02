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
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import ductive.console.shell.Terminal;

public class NonInteractiveTerminal implements Terminal {
	
	private static final String NL = "\n";
	
	private OutputStream out;

	public NonInteractiveTerminal(OutputStream out) {
		this.out = out;
	}
	
	@Override
	public OutputStream output() {
		return out;
	}
	
	@Override
	public OutputStream error() throws IOException {
//		// FIXME: just fix me
		return out;
	}
	
	@Override
	public void print(String value) throws IOException {
		IOUtils.write(value,out);
	}
	
	@Override
	public void print(Ansi value) throws IOException {
		IOUtils.write(value.toString(),out);
	}
	
	@Override
	public void println(String value) throws IOException {
		IOUtils.write(value,out);
		IOUtils.write(NL,out);
	}
	
	@Override
	public void println(Ansi value) throws IOException {
		IOUtils.write(value.toString(),out);
		IOUtils.write(NL,out);
	}
	
	@Override
	public void error(String value) throws IOException {
		IOUtils.write(new Ansi().bold().fg(Color.RED).a(value).reset().toString(),out);
	}
	
	@Override
	public void errorln(String value) throws IOException {
		IOUtils.write(new Ansi().bold().fg(Color.RED).a(value).reset().a(NL).toString(),out);
	}
	
	@Override
	public void flush() throws IOException {
		out.flush();
	}
	
}
