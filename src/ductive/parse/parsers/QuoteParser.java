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
import ductive.parse.Parsers;

public class QuoteParser<T> extends OrParser<T> {

	private Parser<? extends T> singleQuoted;
	private Parser<? extends T> doubleQuoted;
	private Parser<? extends T> perCharacterQuoted;

	@SuppressWarnings("unchecked")
	public QuoteParser(Parser<? extends T> inner) {
		super(new Parser[] {
			Parsers.singleQuoted(inner),
			Parsers.doubleQuoted(inner),
			Parsers.perCharacterQuoted(inner)
		});
		this.singleQuoted = super.alternatives[0];
		this.doubleQuoted = super.alternatives[1];
		this.perCharacterQuoted = super.alternatives[2];
	}

	@Override
	public List<CharSequence> suggest(ParseContext ctx) {
		if(ctx.length()==0)
			return perCharacterQuoted.suggest(ctx);
		if(ctx.charAt(0) == '"')
			return doubleQuoted.suggest(ctx);
		if(ctx.charAt(0) == '\'')
			return singleQuoted.suggest(ctx);
		return perCharacterQuoted.suggest(ctx);
	}

}