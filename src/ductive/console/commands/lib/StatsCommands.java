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
import java.util.Map.Entry;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.springframework.beans.factory.annotation.Autowired;

import ductive.console.commands.register.annotations.Arg;
import ductive.console.commands.register.annotations.Cmd;
import ductive.console.shell.Terminal;
import ductive.stats.StatsProviderRegistry;
import ductive.stats.StatsUtils;
import ductive.stats.ValueConverter;

public class StatsCommands {
	
	@Autowired(required=false) StatsProviderRegistry statsProviderRegistry;
	@Autowired(required=false) ValueConverter[] valueConverters;
	
	@Cmd(path={"stats"},help="lists statististical application data")
	public void stats(Terminal terminal, @Arg(value="path",optional=true) String[] paths) throws IOException {
		if(statsProviderRegistry==null) {
			terminal.errorln(String.format("no instance of class %s registered.",StatsProviderRegistry.class.getCanonicalName()));
			return;
		}

		
		Object tree = statsProviderRegistry.query(StatsUtils.parseQueries(paths),valueConverters);
		for(Entry<String,Object> e : StatsUtils.makeFlat(tree).entrySet()) {
			terminal.println(new Ansi().fgBright(Color.CYAN).a(e.getKey()).a(" ").fg(Color.WHITE).bold().a(e.getValue()).reset());
		}
	}

}
