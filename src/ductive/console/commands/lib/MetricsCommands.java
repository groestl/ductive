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
import java.util.SortedMap;

import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

import ductive.console.commands.register.annotations.Cmd;
import ductive.console.shell.Terminal;

public class MetricsCommands {
	
	private static final Logger log = LoggerFactory.getLogger(MetricsCommands.class);
	
	@Autowired private MetricRegistry metricRegistry;
	
	@Cmd(path={"stats"},help="prints stats")
	public void setLevel(Terminal terminal) throws IOException {
		terminal.println("-counters");
		{
			SortedMap<String,Counter> counters = metricRegistry.getCounters();
			for(Entry<String,Counter> e : counters.entrySet()) {
				terminal.println(new Ansi().a(e.getKey()).a(" ").a(e.getValue().getCount()).reset());
			}
		}

		terminal.println("-gauges");
		{
			SortedMap<String,Gauge> gauges = metricRegistry.getGauges();
			for(Entry<String,Gauge> e : gauges.entrySet()) {
				terminal.println(new Ansi().a(e.getKey()).a(" ").a(e.getValue().getValue()).reset());
			}
		}

		terminal.println("-gauges");
		{
			SortedMap<String,Meter> meters = metricRegistry.getMeters();
			for(Entry<String,Meter> e : meters.entrySet()) {
				terminal.println(new Ansi().a(e.getKey()).a(" ").a(e.getValue().getCount()).reset());
			}
		}

		terminal.println("-timers");
		{
			SortedMap<String,Timer> timers = metricRegistry.getTimers();
			for(Entry<String,Timer> e : timers.entrySet()) {
				terminal.println(new Ansi().a(e.getKey()).a(" ").a(formatTimer(e.getValue().getSnapshot())).reset());
			}
		}

		terminal.println("-histograms");
		{
			SortedMap<String,Histogram> histograms = metricRegistry.getHistograms();
			for(Entry<String,Histogram> e : histograms.entrySet()) {
				terminal.println(new Ansi().a(e.getKey()).a(" ").a(e.getValue()).reset());
			}
		}

	}

	private String formatTimer(Snapshot s) {
		return String.format("%s",s.getMin());
	}


}
