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
package ductive.parse.parsers.quoting;

public class PerCharacterQuoteParserUtils {

	// quoted --> raw

	public static UnquoteResult unquote(CharSequence input) {
		int pos = 0;

		OffsetTrackingAppendable b = new OffsetTrackingAppendable();
		boolean escaping = false;
		for(;pos<input.length();pos++) {
			char c=input.charAt(pos);

			if(!escaping) {

				if(Character.isWhitespace(c)) {
					break;
				}

				if(c=='\\')
					escaping=true;
				else {
					b.append(c,pos);
				}
				continue;
			}

			if(c!='\n') {
				b.append(c,pos);
			}
			escaping=false;
		}

		if(pos>=input.length()) {
			if(escaping)
				b.append('/',pos);
		}

		return new UnquoteResult(b.delegate.toString(), false, pos, b.offsets.stream().mapToInt(i->i).toArray());
	}


	// raw --> quoted

	public static CharSequence quoted(CharSequence s) {
		final StringBuilder r = new StringBuilder();
		for (int i=0; i<s.length(); ++i) {
			char c = s.charAt(i);
			if(Character.isWhitespace(c) || c=='\\')
				r.append('\\');
			r.append(c);
		}
		return r.toString();
	}

}
