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
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Provider;

import jline.console.completer.Completer;
import jline.internal.NonBlockingInputStream;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.Validate;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import ductive.console.shell.InteractiveTerminal;
import ductive.console.shell.ShellHistory;
import ductive.console.shell.ShellSettings;

public class JLineInteractiveTerminal implements InteractiveTerminal {
	
	private JLineConsoleReader jline;
	private ShellSettings currentSettings;

	public JLineInteractiveTerminal(JLineConsoleReader r, ShellSettings settings) {
		Validate.notNull(settings);
		this.jline = r;
		updateSettings(settings);
		
		jline.addCompleter(new Completer() {
			@Override public int complete(String buffer, int cursor, List<CharSequence> candidates) {
				return currentSettings.completer() != null ? currentSettings.completer().complete(buffer,cursor,candidates) : cursor;
			}
		});
		jline.setPrompt(new Provider<Ansi>() {
			@Override public Ansi get() {
				return currentSettings.prompt().get();
			}
		});
	}
	
	@Override
	public InputStream input() {
		return jline.getInput();
	}

	@Override
	public String readLine() throws IOException {
		return jline.readLine();
	}

	@Override
	public int readChar() throws IOException {
		return jline.readCharacter();
	}

	@Override
	public int readChar(long timeout) throws IOException {
		if( NonBlockingInputStream.class.cast(jline.getInput()).peek(timeout) == -2 )
			return TIMEOUT;
		return readChar();
	}
	
	@Override
	public OutputStream output() {
		return new WriterOutputStream(jline.getOutput(),Charset.forName("UTF-8"),4096,true);
	}
	
	@Override
	public OutputStream error() throws IOException {
		// FIXME: just fix me
		Writer writer = jline.getOutput();
		writer.write(new Ansi().bold().fg(Color.RED).toString());
		return new WriterOutputStream(writer);
	}
	
	@Override
	public void print(String value) throws IOException {
		jline.print(value);
	}
	
	@Override
	public void print(Ansi value) throws IOException {
		jline.print(value.toString());
	}
	
	@Override
	public void println(String value) throws IOException {
		jline.println(value);
	}
	
	@Override
	public void println(Ansi value) throws IOException {
		jline.println(value.toString());
	}
	
	@Override
	public void error(String value) throws IOException {
		jline.print(new Ansi().bold().fg(Color.RED).a(value).reset().toString());
	}
	
	@Override
	public void errorln(String value) throws IOException {
		jline.println(new Ansi().bold().fg(Color.RED).a(value).reset().toString());
	}
	
	@Override
	public void flush() throws IOException {
		jline.flush();
	}
	
	@Override
	public void updateSettings(ShellSettings settings) {
		this.currentSettings = settings;
		ShellHistory history = settings.history();
		if(history!=null) {
			jline.setHistoryEnabled(true);
			jline.setHistory(history.history());
		} else {
			jline.setHistoryEnabled(false);
			jline.setHistory(null);
		}
	}
	
	@Override
	public ShellSettings getTerminalSettings() {
		return currentSettings;
	}

}
