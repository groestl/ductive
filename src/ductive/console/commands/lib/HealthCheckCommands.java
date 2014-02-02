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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;

import ductive.commons.PrefixOutputStream;
import ductive.console.commands.register.annotations.Arg;
import ductive.console.commands.register.annotations.Cmd;
import ductive.console.commands.register.annotations.Opt;
import ductive.console.shell.Terminal;

public class HealthCheckCommands {
	
	private static final Logger log = LoggerFactory.getLogger(HealthCheckCommands.class);
	
	@Autowired private HealthCheckRegistry healthChecks;
	
	@Cmd(path={"app","health","list"},help="run health checks")
	public void list(Terminal term) throws IOException {
		for(String name : healthChecks.getNames())
			term.println(new Ansi().fgBright(Color.CYAN).a(name).reset());
	}

	@Cmd(path={"app","health","check"},help="run health checks")
	public int runHealthcheck(Terminal term, @Arg(value="pattern",optional=true) String pattern, @Opt(value="details") boolean details) throws IOException {
		final Map<String,Result> results;
		
		if( StringUtils.isBlank(pattern) ) {
			if(log.isInfoEnabled())
				log.info(String.format("running %s health checks...",healthChecks.getNames().size()));
			
			results = healthChecks.runHealthChecks();
		} else
			results = selectiveHealthChecks(pattern);
		
		display(term,results,details);
		
		return isHealthy(results) ? 0 : 1;
	}

	private boolean isHealthy(Map<String,Result> results) {
		for(Entry<String,Result> e : results.entrySet())
			if(!e.getValue().isHealthy())
				return false;
		return true;
	}

	private Map<String,Result> selectiveHealthChecks(String pattern) {
		Set<String> names = new LinkedHashSet<>();
		for(String name : healthChecks.getNames())
			if(name.matches(pattern))
				names.add(name);
				
		log.info(String.format("running %s health checks...",names.size()));

		Map<String,Result> results = new LinkedHashMap<>();
		for(String name : names)
			results.put(name,healthChecks.runHealthCheck(name));
		
		return results;
	}

	private void display(Terminal term, Map<String, Result> results, boolean details) throws IOException {
		for (Entry<String, HealthCheck.Result> entry : results.entrySet()) {
			if(entry.getValue().isHealthy()) {
				term.println(new Ansi().bold().fgBright(Color.GREEN).a("OK      ").reset().fgBright(Color.CYAN).a(entry.getKey()).reset());
				continue;
			}

			Ansi a = new Ansi().bold().fg(Color.RED).a("FAILED  ").reset().fgBright(Color.CYAN).a(entry.getKey()).reset().a(" ").fgBright(Color.WHITE).a(" '").bold().a(entry.getValue().getMessage()).a("'").reset();
			
			if(details) {
				final Throwable e = entry.getValue().getError();
				if (e != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					e.printStackTrace(new PrintStream(new PrefixOutputStream(baos,new Ansi().fg(Color.BLUE).a("        # ").fg(Color.BLUE).toString())));
					a = a.a("\n").fg(Color.DEFAULT).a(baos.toString("UTF-8")).reset();
				}
				term.print(a.reset());
			} else
				term.println(a.reset());
		}
	}

}
