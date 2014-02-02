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

public class EOLParser extends Parser<String> {
	
	public static final Parser<String> INSTANCE = new EOLParser();
	
	public static final String UNEXPECTED_EOF_MESSAGE = "Reached EOF while looking for EOL";
	public static final String UNEXPECTED_CHARACTER_MESSAGE = "Unexpected character '%s' while looking for EOL";

	private static final char EOL = '\n';

	@Override
	public Result<String> doApply(ParseContext ctx) {
		if(ctx.length()==0)
			throw new UnexpectedEofException(UNEXPECTED_EOF_MESSAGE,ctx,0);
		
		char c = ctx.charAt(0);
		if(c!=EOL)
			throw new UnexpectedCharacterException(String.format(UNEXPECTED_CHARACTER_MESSAGE,c),ctx,0);
		
		return Result.make(Character.toString(EOL),ctx.eatInput(1));
	}

}
