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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDKToCommonsHandler extends Handler {

	// state

	private final ConcurrentMap<String,Log> logs = new ConcurrentHashMap<>();

	// impl

	@Override
	public void publish(LogRecord record) {
		String name = record.getLoggerName();

		Log log = logs.get(name);
		if( log==null )
			logs.put(name,log=LogFactory.getLog(name));

		String message = record.getMessage();
		Throwable ex = record.getThrown();
		Level level = record.getLevel();

		if( Level.SEVERE==level )
			log.error(message,ex);
		else if( Level.WARNING==level )
			log.warn(message,ex);
		else if( Level.INFO==level )
			log.info(message,ex);
		else if( Level.CONFIG==level )
			log.debug(message,ex);
		else
			log.trace(message,ex);
	}

	@Override public void flush() {}
	@Override public void close() throws SecurityException {}

	// static stuff

	public static final JDKToCommonsHandler DEFAULT = new JDKToCommonsHandler();

	static {
		DEFAULT.setLevel(Level.ALL);
	}

	public static JDKToCommonsHandler rerouteJDKToCommons(Level level) {
		Logger root = Logger.getLogger("");

		{
			Handler[] handlers = root.getHandlers();
			if( !ArrayUtils.isEmpty(handlers) )
				for(Handler h:handlers)
					root.removeHandler(h);
		}

		root.addHandler(DEFAULT);
		root.setLevel(level);

		return DEFAULT;
	}

}
