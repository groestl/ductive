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
import java.util.SortedSet;
import java.util.TreeSet;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;

public class PrecedesParser<T> extends Parser<T> {

	private Parser<?> predecessor;
	private Parser<T> follower;

	public PrecedesParser(Parser<?> predecessor, Parser<T> follower) {
		this.predecessor = predecessor;
		this.follower = follower;
	}

	@Override
	public Result<T> doApply(ParseContext ctx) {
		return follower.apply(predecessor.apply(ctx).ctx);
	}

//	@Override
//	public Examples examples() {
//		return new PrecedesExpector(predecessor.examples(),follower.examples());
//	}

	@Override
	public List<CharSequence> suggest(ParseContext ctx) {
		SortedSet<CharSequence> result = new TreeSet<>();
		List<CharSequence> as = predecessor.suggest(ctx);
		List<CharSequence> bs = follower.suggest(ctx);
		for(CharSequence a : as)
			for(CharSequence b : bs)
				result.add(StringHelper.concat(a,b));
		return new ArrayList<>(result);
	}

//	private static class PrecedesExpector implements Examples {
//
//		private Examples predecessor;
//		private Examples follower;
//
//		public PrecedesExpector(Examples predecessor, Examples follower) {
//			this.predecessor = predecessor;
//			this.follower = follower;
//		}
//
//
//
//	}

}
