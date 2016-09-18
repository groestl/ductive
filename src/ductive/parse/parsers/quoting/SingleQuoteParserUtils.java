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

public class SingleQuoteParserUtils {

	// defs

	public static final String REACHED_EOF_MESSAGE_START = "Reached EOF while looking for starting quote character '";


	// quoted --> raw

	public static UnquoteResult unquote(CharSequence input) {
		int pos = 0;

		if(input.length()==0)
			throw new SingleQuoteUnexpectedEofException(REACHED_EOF_MESSAGE_START,0);

		char fc = input.charAt(pos++);
		if(fc!='\'')
			throw new SingleQuoteUnexpectedCharacterException(String.format("Unexpected character '%s' while looking for starting quote character \'",fc),0);

		OffsetTrackingAppendable b = new OffsetTrackingAppendable();

		boolean inquotes = true;
		while(pos<input.length()) {
			char c=input.charAt(pos++);

			if(c=='\'') {
				if(pos<input.length() && input.charAt(pos)=='\'') {
					++pos;
					b.append(c,pos-1);
					continue;
				} else {
					inquotes=false;
					break;
				}
			}

			b.append(c,pos-1);
		}

		return new UnquoteResult(b.delegate.toString(), inquotes, pos, b.offsets.stream().mapToInt(i->i).toArray());
	}


	// raw --> quoted


	public static CharSequence quoted(CharSequence s) {
		final StringBuilder r = new StringBuilder();
		r.append('\'');
		for (int i=0; i<s.length(); ++i) {
			char c = s.charAt(i);
			if(c=='\'')
				r.append('\'');
			r.append(c);
		}
		r.append('\'');
		return r.toString();
	}



	@SuppressWarnings("serial")
	public static class SingleQuoteUnexpectedEofException extends RuntimeException {
		public final int pos;

		public SingleQuoteUnexpectedEofException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
	}

	@SuppressWarnings("serial")
	public static class SingleQuoteUnexpectedCharacterException extends RuntimeException {
		public final int pos;

		public SingleQuoteUnexpectedCharacterException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
	}

}
