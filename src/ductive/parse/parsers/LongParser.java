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

import org.apache.commons.lang3.CharUtils;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;
import ductive.parse.errors.UnexpectedCharacterException;
import ductive.parse.errors.UnexpectedEofException;

public class LongParser extends Parser<Long> {
	
	public static final String UNEXPECTED_EOF_MESSAGE = "Reached EOF while looking for numeric characters [0-9] at pos %s";
	public static final String UNEXPECTED_CHARACTER_MESSAGE = "Unexpected character '%s', expected numeric characters [0-9] at pos %s";

	@Override
	public Result<Long> doApply(ParseContext ctx) {
		int pos=0;
		
		if(ctx.length()==0)
			throw new UnexpectedEofException(String.format(LongParser.UNEXPECTED_EOF_MESSAGE,pos),ctx,0);

		StringBuffer buf = new StringBuffer();
		
		char c=ctx.charAt(pos);
		while(true) {
			if(!CharUtils.isAsciiNumeric(c))
				break;
			
			buf.append(c);
			
			if(++pos>=ctx.length())
				break;
			
			c=ctx.charAt(pos);
		}
		
		if(buf.length()==0)
			throw new UnexpectedCharacterException(String.format(UNEXPECTED_CHARACTER_MESSAGE,c,0),ctx,0);
		
		String str = buf.toString();
		return Result.make(Long.parseLong(str),ctx.eatInput(str.length())); 
	}

}
