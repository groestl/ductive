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
package ductive.console.sshd;

import java.io.OutputStream;
import java.util.Map;

import jline.TerminalSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.PtyMode;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.SignalListener;

import ductive.console.jline.NL2CRNLOutputStream;

public class SshTerminal extends TerminalSupport implements SignalListener {
	
	private static final Log log = LogFactory.getLog(SshTerminal.class);

	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 25;
	
	private Environment env;
	private int height;
	private int width;

	public SshTerminal(Environment environment) throws Exception {
		super(true);
		setAnsiSupported(true);
		setEchoEnabled(false);
		
		this.env = environment;
		
		if(log.isTraceEnabled())
			log.trace(String.format("%s",environment.getEnv()));
		
		this.env.addSignalListener(this);
		
		updateSize();
	}
	
	@Override
	public void signal(Signal signal) {
		if(log.isTraceEnabled())
			log.trace(String.format("signal received: %s",signal));
		
		if(signal==Signal.WINCH)
			updateSize();
	}

	@Override
	public OutputStream wrapOutIfNeeded(OutputStream out) {
		Integer n = env.getPtyModes().get(PtyMode.ONLCR);
		if(n==null || n!=0)
			return new NL2CRNLOutputStream(out);
		return out;
	}
	
	@Override
	public boolean hasWeirdWrap() {
		return true;
	}
	
	@Override public int getWidth() { return width; }
	
	@Override public int getHeight() { return height; }
	
	private void updateSize() {
		Map<String,String> e = env.getEnv();
		
		String w = e.get(Environment.ENV_COLUMNS);
		width = w!=null ? Integer.parseInt(w) : DEFAULT_WIDTH;
		
		String h = e.get(Environment.ENV_LINES);
		height = h!=null ? Integer.parseInt(h) : DEFAULT_HEIGHT;
	}

}