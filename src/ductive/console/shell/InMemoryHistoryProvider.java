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

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import jline.console.history.History;
import jline.console.history.History.Entry;
import jline.console.history.MemoryHistory;

public class InMemoryHistoryProvider implements HistoryProvider {
	
	private Map<String,History> data = new HashMap<>();
	
	private Object monitor = new Object();
	
	private void saveHistory(String historyKey, History sessionHistory) {
		synchronized (monitor) {
			History history = data.get(historyKey);
			if(history==null)
				data.put(historyKey,history=new MemoryHistory());
				
			ListIterator<Entry> it = sessionHistory.entries();
			while(it.hasNext()) {
				Entry e = it.next();
				history.add(e.value());
			}
		}
	}
	
	private History forkHistory(String historyKey) {
		synchronized (monitor) {
			History history = data.get(historyKey);
			if(history==null)
				data.put(historyKey,history=new MemoryHistory());
			
			MemoryHistory sessionHistory = new MemoryHistory();
			ListIterator<Entry> it = history.entries();
			while(it.hasNext()) {
				Entry e = it.next();
				sessionHistory.add(e.value());
			}
			return sessionHistory;
		}
	}
	
	@Override public ShellHistory history(String historyKey) {
		return new SessionShellHistory(historyKey);
	}
	
	private class SessionShellHistory implements ShellHistory {
		
		private String historyKey;
		private History history;

		public SessionShellHistory(String historyKey) {
			this.historyKey = historyKey;
			history = forkHistory(historyKey);
		}
		
		@Override public History history() {
			return history;
		}

		@Override
		public void close() {
			saveHistory(historyKey,history);
		}
	}
	

}
