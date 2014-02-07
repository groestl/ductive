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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.springframework.beans.factory.annotation.Autowired;

import ductive.console.commands.register.CommandRegistry;
import ductive.console.commands.register.annotations.Cmd;
import ductive.console.commands.register.model.ArgumentType;
import ductive.console.commands.register.model.CommandType;
import ductive.console.commands.register.model.OptionType;
import ductive.console.commands.register.model.ParameterType;
import ductive.console.shell.Terminal;

public class HelpCommands {

	@Autowired private CommandRegistry commandRegistry;

	@Cmd(path={"help"},help="list available commands")
	public void help(Terminal terminal) throws IOException {
		for(CommandType cmd : commandRegistry.commands()) {
			terminal.println(new Ansi()
				.bold().a(formatPath(cmd.path)).reset()
				.a(formatParams(cmd.params))
				.fgBright(Color.CYAN).a("  ").a(cmd.help).reset()
			);
		}
	}

	private Ansi formatParams(ParameterType[] params) {
		final List<ArgumentType> arguments = new ArrayList<>();
		final List<OptionType> options = new ArrayList<>();
		for(ParameterType p : params)
			p.visit(new ParameterType.Visitor<Void>() {
				@Override public Void accept(ArgumentType arg) { arguments.add(arg); return null;	}
				@Override public Void accept(OptionType opt) { options.add(opt); return null; }
			});

		Ansi a = new Ansi();
		for(OptionType opt : options)
			a = a.fg(Color.YELLOW).a(" [--").a(opt.name).a("]");

		for(ArgumentType arg : arguments) {
			if(arg.type.isArray())
				a = a.fgBright(Color.YELLOW).a(" [--").a(arg.name).a("=]*");
			else if(arg.optional)
				a = a.fgBright(Color.YELLOW).a(" [--").a(arg.name).a("=]");
			else
				a = a.fgBright(Color.YELLOW).a(" --").a(arg.name).a("=");
		}

		return a.reset();
	}

	private String formatPath(String[] path) {
		return StringUtils.join(path,' ');
	}

	public void setCommandRegistry(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

}
