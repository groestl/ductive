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
import java.util.Map;

import javax.inject.Provider;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import ductive.console.commands.register.annotations.Cmd;
import ductive.console.shell.EmbeddedGroovyInterpreter;
import ductive.console.shell.EmbeddedGroovyShell;
import ductive.console.shell.GroovyShellContextProvider;
import ductive.console.shell.InteractiveTerminal;
import ductive.console.shell.ShellUtils;
import ductive.console.shell.Terminal;
import ductive.console.shell.TerminalUser;


public class GroovyCommands {

	@Autowired private Provider<EmbeddedGroovyShell> shellProvider;
	@Autowired private Provider<EmbeddedGroovyInterpreter> interpreterProvider;

	@Autowired(required=false) private GroovyShellContextProvider groovyShellContextProvider;

	@Cmd(path={"gsh"},help="spawns a groovy interactive shell")
	public void groovyShell(Terminal terminal,TerminalUser user) throws IOException {
		if(InteractiveTerminal.class.isInstance(terminal)) {
			EmbeddedGroovyShell shell = shellProvider.get();
			shell.setShellContext(context(user));
			ShellUtils.nestedShell(InteractiveTerminal.class.cast(terminal),user,shell);
		} else {
			EmbeddedGroovyInterpreter interpreter = interpreterProvider.get();
			interpreter.setShellContext(context(user));
			interpreter.execute(terminal,user,IOUtils.toString(terminal.input()));
		}
	}

	private Map<String,Object> context(TerminalUser user) {
		return groovyShellContextProvider!=null ? groovyShellContextProvider.context(user) : null;
	}

	public void setShellProvider(Provider<EmbeddedGroovyShell> shellProvider) {
		this.shellProvider = shellProvider;
	}

}
