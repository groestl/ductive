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
import ductive.parse.errors.NoMatchException;

public class AtLeastParser<T> extends Parser<List<T>> {
	
	private static final String EXPECTED_AT_LEAST_MESSAGE = "Expected at least %s occurences of %s";
	private Parser<T> delegate;
	private int atLeast;

	public AtLeastParser(Parser<T> delegate, int atLeast) {
		Validate.notNull(delegate);
		Validate.isTrue(atLeast>=0);
		this.delegate = delegate;
		this.atLeast = atLeast;
	}

	@Override
	public Result<List<T>> doApply(final ParseContext ctx) {
		List<T> result = new ArrayList<>();
		
		ParseContext curCtx = ctx;
		NoMatchException first = null;
		while(true) {
			try {
				Result<T> r = this.delegate.apply(curCtx);
				result.add(r.value);
				curCtx = r.ctx;
			} catch(NoMatchException e) {
				first = e;
				break;
			}
		}

		if(result.size()<atLeast)
			if(first!=null)
				throw first;
			else
				throw new NoMatchException(String.format(EXPECTED_AT_LEAST_MESSAGE,atLeast,this.delegate),curCtx,0);
		
		return Result.make(result,curCtx);
	}
	
}
