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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import ductive.commons.ToString;

public class CompleteResult {

	public static final CompleteResult EMPTY = new CompleteResult(new ArrayList<CharSequence>(),0);

	public final List<? extends CharSequence> suggestions;
	public final int suggestionPos;

	public CompleteResult(List<? extends CharSequence> suggestions, int suggestionPos) {
		this.suggestions = suggestions;
		this.suggestionPos = suggestionPos;
		Validate.notNull(suggestions);
		//Validate.notNull(suggestionPos);
	}

	public static CompleteResult make(List<? extends CharSequence> suggestions, int suggestionPos) {
		return new CompleteResult(suggestions,suggestionPos);
	}

	@Override public String toString() { return ToString.me(this); }

}
