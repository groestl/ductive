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
import java.util.Collection;


public class Utils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Parser<T>[] toArray(Iterable<? extends Parser<? extends T>> parsers) {
		if (parsers instanceof Collection<?>)
			return toArray((Collection) parsers);
		return toArrayWithIteration(parsers);
	}

	public static <T> Parser<T>[] toArrayWithIteration(Iterable<? extends Parser<? extends T>> parsers) {
		ArrayList<Parser<? extends T>> list = new ArrayList<Parser<? extends T>>();
		for (Parser<? extends T> parser : parsers)
			list.add(parser);
		return toArray(list);
	}

	@SuppressWarnings("unchecked")
	public static <T> Parser<T>[] toArray(Collection<? extends Parser<? extends T>> parsers) {
		return parsers.toArray(new Parser[parsers.size()]);
	}

}
