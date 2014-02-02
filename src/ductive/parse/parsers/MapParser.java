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


import com.google.common.base.Function;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;


public class MapParser<T,R> extends Parser<R> {

	private final Parser<T> parser;
	private final Function<? super T, ? extends R> fn;

	public MapParser(Parser<T> parser, Function<? super T, ? extends R> fn) {
		this.parser = parser;
		this.fn = fn;
	}

	@Override
	public Result<R> doApply(final ParseContext ctx) {
		Result<T> r = parser.apply(ctx);
		return Result.make((R)fn.apply(r.value),r.ctx);
	}
	
	@Override public List<CharSequence> suggest(ParseContext ctx) {
		return parser.suggest(ctx);
	}
	
	@Override 
	public String toString() {
		return fn.toString();
	}

}
