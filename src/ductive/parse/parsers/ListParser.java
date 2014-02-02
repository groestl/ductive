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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;

public class ListParser<A> extends Parser<List<A>> {

	private final Parser<? extends A>[] parsers;

	public ListParser(Parser<? extends A>[] parsers) {
		Validate.isTrue(parsers.length>0);
		this.parsers = parsers;
	}

	@Override
	public Result<List<A>> doApply(ParseContext ctx) {
		List<A> result = new ArrayList<>();

		for(Parser<? extends A> p : parsers) {
			Result<? extends A> r = p.apply(ctx);
			result.add(r.value);
			ctx = r.ctx;
		}

		return Result.make(result,ctx);
	}

	@Override
	public List<CharSequence> suggest(ParseContext ctx) {

		List<List<CharSequence>> suggestions = new ArrayList<>();

		for(Parser<? extends A> p : parsers)
			suggestions.add(p.suggest(ctx));

		List<CharSequence> result = new ArrayList<>();
		for(List<CharSequence> l : suggestions) {
			List<CharSequence> next = new ArrayList<>();
			if(result.size()==0)
				next = l;
			else
				for(CharSequence a : result)
					for(CharSequence b : l)
						next.add(new StringBuilder().append(a).append(b).toString());

			result = next;
		}

		return result;
	}

}
