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
package ductive.parse.parsers;

import java.util.List;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;


public class IsCompletingSwitchParser<T> extends Parser<T> {

	private Parser<? extends T> defaultParser;
	private Parser<? extends T> completingParser;

	public IsCompletingSwitchParser(Parser<? extends T> defaultParser, Parser<? extends T> completingParser) {
		this.defaultParser = defaultParser;
		this.completingParser = completingParser;
	}

	@Override public Result<T> doApply(ParseContext ctx) {
		if(!ctx.isCompleting())
			return defaultParser.apply(ctx).cast();
		return completingParser.apply(ctx).cast();
	}

	@Override public List<CharSequence> suggest(ParseContext ctx) {
		return completingParser.suggest(ctx);
	}

}
