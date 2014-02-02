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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.springframework.beans.factory.annotation.Autowired;

import ductive.commons.Names;
import ductive.console.commands.parser.CmdParser;
import ductive.console.commands.parser.CmdParserBuilder;
import ductive.console.commands.parser.model.CommandLine;
import ductive.console.commands.register.CommandContext;
import ductive.console.commands.register.CommandInvoker;
import ductive.console.commands.register.CommandRegistry;
import ductive.parse.errors.NoMatchException;

public class EmbeddedAppShell implements Shell {

	private static final Ansi DEFAULT_STANDARD_PROMPT = new Ansi().reset().bold().fg(Color.MAGENTA).a("app> ").reset();

	private static final String HISTORY_KEY = Names.from(EmbeddedAppShell.class,"history");

	private Ansi standardPrompt = DEFAULT_STANDARD_PROMPT;

	private CommandRegistry commandRegistry;

	@Autowired private CommandInvoker commandInvoker;
	@Autowired private CmdParserBuilder cmdParserBuilder;
	@Autowired private HistoryProvider historyProvider;
	
	private CmdParser cmdParser;
	
	@PostConstruct
	public void init() {
		cmdParser = new CmdParser(cmdParserBuilder.buildParser(commandRegistry.commands()));
	}

	@Override
	public void execute(InteractiveTerminal terminal) throws IOException {
		try( ShellHistory history = historyProvider.history(HISTORY_KEY) ) {
			ShellSettings settings = new StaticShellSettings(standardPrompt,cmdParser,history);
			terminal.updateSettings(settings);
			
			while (true) {
				Integer result = execute(terminal,terminal.readLine());
				if(result != null)
					break;
			}
		}
	}

	private Integer execute(InteractiveTerminal terminal, String command) throws IOException {
		if (command == null)
			return 0;

		if (StringUtils.isBlank(command))
			return null;

		try {
			try {
				CommandLine line = cmdParser.parse(command);
				//terminal.println(line.toString());
				commandInvoker.execute(new CommandContext(terminal),line);
			} catch(NoMatchException e) {
				terminal.errorln(e.getMessage());
			}
		} catch (Throwable e) {
			// Unroll invoker exceptions
			if (e instanceof InvokerInvocationException) {
				e = e.getCause();
			}

			PrintStream ps = new PrintStream(terminal.error());
			e.printStackTrace(ps);
			ps.flush();
		}
		return null;
	}

	public void setCommandRegistry(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

}
