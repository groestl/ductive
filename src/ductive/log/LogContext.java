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
package ductive.log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.MDC;

public class LogContext implements AutoCloseable {
	
	private static final String CONTEXT_KEY = "_ductive";

	private String name;
	
	private HashMap<String,Object> values = new LinkedHashMap<>();

	public LogContext(String name) {
		this.name = name;
		contextHolder().push(this);
	}
	
	@Override
	public void close() throws Exception {
		contextHolder().pop(this);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(name);
		b.append("={");
		Iterator<Entry<String, Object>> it = values.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Object> e = it.next();
			b.append(e.getKey());
			b.append("=");
			b.append(e.getValue());
			if(it.hasNext())
				b.append(",");
		}
		b.append("}");
		return b.toString();
	}


	public void put(String name, Object value) {
		values.put(name,value);
	}
	
	private ContextHolder contextHolder() {
		ContextHolder contextHolder = ContextHolder.class.cast(MDC.get(CONTEXT_KEY));
		if(contextHolder==null)
			MDC.put(CONTEXT_KEY,contextHolder=new ContextHolder());
		return contextHolder;
	}
	
	private static class ContextHolder {
		
		private Deque<LogContext> contexts = new ArrayDeque<>();

		public void push(LogContext ctx) {
			contexts.push(ctx);
		}

		public void pop(LogContext ctx) {
			LogContext pop = contexts.pop();
			Validate.isTrue(pop==ctx);
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("[");
			Iterator<LogContext> it = contexts.iterator();
			while(it.hasNext()) {
				LogContext c = it.next();
				b.append(c.toString());
				if(it.hasNext())
					b.append(",");
			}
			b.append("]");
			return b.toString();
		}
		
	}

}
