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
package ductive.parse;

import org.apache.commons.lang3.Validate;

import ductive.parse.locators.OffsetMapping;
import ductive.parse.locators.PosMapping;
import ductive.parse.locators.SourceLocator;

public class ParseContext implements CharSequence {

	private final CharSequence input;
	private final SourceLocator locator;
	private final boolean completing;

	public ParseContext(CharSequence input, SourceLocator locator, boolean completing) {
		this.locator = locator;
		this.input = input;
		this.completing = completing;
	}

	public CharSequence input() {
		return input;
	}

	public ParseContext eatInput(int n) {
		Validate.isTrue(n<=input.length());
		return new ParseContext(input.subSequence(n,input.length()),locator.map(new OffsetMapping(n)),completing);
	}

	public ParseContext fork(String newInput, PosMapping mapping) {
		return new ParseContext(newInput,locator.map(mapping),completing);
	}


	// completion

	public boolean isCompleting() {
		return completing;
	}

	public boolean isEOF(long pos) {
		return locator.isEof(pos);
	}
	
	public boolean isOriginalEof(long pos) {
		return locator.isOriginalEof(pos);
	}


	// CharSequence

	@Override
	public ParseContext subSequence(int start, int end) {
		return new ParseContext(input.subSequence(start,end),locator.map(new OffsetMapping(start)),completing);
	}

	@Override
	public char charAt(int index) {
		return input.charAt(index);
	}

	@Override
	public int length() {
		return input.length();
	}

	@Override
	public String toString() {
		return input.toString();
	}

	public long locate(long pos) {
		return locator.originalPos(pos);
	}

}
