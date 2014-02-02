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

import java.util.Arrays;
import java.util.List;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;
import ductive.parse.errors.UnexpectedCharacterException;
import ductive.parse.errors.UnexpectedEofException;


public class StringParser extends Parser<String> {

	public static final String UNEXPECTED_EOF_MESSAGE = "Unexpected EOF while looking for string '%s'";
	public static final String UNEXPECTED_CHARACTER_MESSAGE = "Unexpected character '%s', expected '%s'";

	private String recognize;
	private boolean caseSensitive;

	public StringParser(String recognize) {
		this(recognize,true);
	}
	
	public StringParser(String recognize, boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		if(!caseSensitive)
			this.recognize = recognize;
		else {
			StringBuffer b = new StringBuffer();
			for(int i=0;i<recognize.length();++i)
				b.append(Character.toLowerCase(recognize.charAt(i)));
			this.recognize = b.toString();
		}
	}

	@Override
	public List<CharSequence> suggest(ParseContext ctx) {
		return Arrays.<CharSequence>asList(recognize);
	}

	@Override
	public Result<String> doApply(ParseContext ctx) {
		for(int i=0;i<recognize.length();++i) {
			if(i>=ctx.length())
				throw new UnexpectedEofException(String.format(UNEXPECTED_EOF_MESSAGE,recognize),ctx,i);

			
			char expected = recognize.charAt(i);
			char actual = ctx.charAt(i);
			if(caseSensitive ? (expected!=actual) : (expected!=Character.toLowerCase(actual)))
				throw new UnexpectedCharacterException(String.format(UNEXPECTED_CHARACTER_MESSAGE,actual,expected),ctx,i);
		}

		return Result.make(recognize,ctx.eatInput(recognize.length()));
	}

}
