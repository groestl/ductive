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

import ductive.parse.CompleteResult;
import ductive.parse.ParseContext;
import ductive.parse.Parser;

public class WhitespaceParser extends CharTestParser<String> {

	public static final Parser<String> INSTANCE = new WhitespaceParser();

	@Override
	protected boolean charTest(char character) {
		return Character.isWhitespace(character);
	}

//	@Override
//	public Examples examples() {
//		return new WhitespaceExpector();
//	}

	@Override
	public List<CharSequence> suggest(ParseContext ctx) {
		return Arrays.asList((CharSequence)" ");
	}

	@Override
	protected String makeResult(CharSequence matchingString) {
		return matchingString.toString();
	}

	@Override
	protected String characterClassDescription() {
		return "WHITESPACE";
	}

//	public static class WhitespaceExpector implements Examples {
//
//	}

	@Override
	public CompleteResult complete(CharSequence input, int cursor) {
		return CompleteResult.make(Arrays.asList(" "),0);
	}

}
