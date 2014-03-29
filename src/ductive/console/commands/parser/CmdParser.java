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
package ductive.console.commands.parser;

import java.util.List;

import jline.console.completer.Completer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ductive.console.commands.parser.model.CommandLine;
import ductive.parse.Parser;
import ductive.parse.errors.NoMatchException;
import ductive.parse.jline.CompletorAdapter;

public class CmdParser implements Completer {
	
	private static final Logger log = LoggerFactory.getLogger(CmdParser.class);

	private Parser<CommandLine> parser;
	private CompletorAdapter<?> completor;

	private boolean verbose;

	public CmdParser(Parser<CommandLine> parser, boolean verbose) {
		this.parser = parser;
		this.verbose = verbose;
		this.completor = new CompletorAdapter<>(parser);
	}

	// api

	public CommandLine parse(String input) {
		return parser.parseFully(input);
	}

	@Override
	public int complete(String input, int cursor, List<CharSequence> candidates) {
		try {
			return completor.complete(input,cursor,candidates);
		} catch(NoMatchException e) {
			if(verbose && log.isTraceEnabled())
				log.trace(String.format("completion error: %s"),e);
			return cursor;
		}
	}

}
