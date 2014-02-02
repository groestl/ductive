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

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;
import ductive.parse.errors.UnexpectedCharacterException;
import ductive.parse.errors.UnexpectedEofException;

public abstract class CharTestParser<T> extends Parser<T> {

	@Override
	public Result<T> doApply(ParseContext ctx) {
		if(ctx.length()==0)
			throw new UnexpectedEofException(String.format("unexpected EOF while looking for character class %s",characterClassDescription()),ctx,0);

		int pos = 0;

		while(pos<ctx.length() && charTest(ctx.charAt(pos)))
			pos++;

		if(pos==0)
			throw new UnexpectedCharacterException(String.format("unexpected character '%s' while looking for character class %s",ctx.charAt(0),characterClassDescription()),ctx,0);

		T val = makeResult(ctx.subSequence(0,pos));
		ParseContext newCtx = ctx.eatInput(pos);
		return Result.make(val,newCtx);
	}

	protected abstract String characterClassDescription();

	protected abstract boolean charTest(char charAt);

	protected abstract T makeResult(CharSequence matchingString);

}
