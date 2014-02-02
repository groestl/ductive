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

import javax.annotation.PostConstruct;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ductive.console.commands.parser.CmdParser;
import ductive.console.commands.parser.CmdParserBuilder;
import ductive.console.commands.parser.model.CommandLine;
import ductive.console.commands.register.CommandInvoker;
import ductive.console.commands.register.CommandRegistry;
import ductive.parse.Parser;

public class DefaultCommandFactory implements CommandFactory {

	@Autowired private CmdParserBuilder cmdParserBuilder;
	@Autowired private CommandRegistry commandRegistry;
	@Autowired private CommandInvoker commandInvoker;
	
	private CmdParser cmdParser;

	@PostConstruct
	public void init() {
		Parser<CommandLine> parser = cmdParserBuilder.buildParser(commandRegistry.commands());
		cmdParser = new CmdParser(parser);
	}

	
	@Override
	public Command createCommand(String command) {
		return new CommandRunner(cmdParser,commandInvoker,command);
	}

}
