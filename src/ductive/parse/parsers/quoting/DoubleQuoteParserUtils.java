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

public class DoubleQuoteParserUtils {

	// defs

	public static final String REACHED_EOF_MESSAGE_START = "Reached EOF while looking for starting quote character \"";


	// quoted --> raw

	public static UnquoteResult unquote(CharSequence input) {
		int pos = 0;

		if(input.length()==0)
			throw new DoubleQuoteUnexpectedEofException(REACHED_EOF_MESSAGE_START,0);

		char fc = input.charAt(pos++); // starting quote
		if(fc!='\"')
			throw new DoubleQuoteUnexpectedCharacterException(String.format("Unexpected character '%s' while looking for starting quote character \"",fc),0);

		OffsetTrackingAppendable b = new OffsetTrackingAppendable();
		boolean escaping = false;
		boolean inquotes = true;
		for(;pos<input.length();pos++) {
			char c=input.charAt(pos);

			if(!escaping) {
				if(c=='"') {
					inquotes=false;
					pos++;
					break;
				}

				if(c=='\\')
					escaping=true;
				else {
					b.append(c,pos);
				}
				continue;
			}

			switch(c) {
			case '\\':
				b.append('\\',pos);
				break;
			case 'b':
				b.append('\b',pos);
				break;
			case 'n':
				b.append('\n',pos);
				break;
			case 't':
				b.append('\t',pos);
				break;
			case 'f':
				b.append('\f',pos);
				break;
			case 'r':
				b.append('\r',pos);
				break;
			default:
				b.append(c,pos);
			}
			escaping=false;
		}

		if(pos>=input.length() && escaping)
			b.append('\\',pos-1);

		return new UnquoteResult(b.delegate.toString(), inquotes, pos, b.offsets.stream().mapToInt(i->i).toArray());
	}


	// raw --> quoted


	public static CharSequence quoted(CharSequence s) {
		final StringBuilder r = new StringBuilder();
		r.append('"');
		for (int i=0; i<s.length(); ++i) {
			switch (s.charAt(i)) {
			case '\b':
				r.append("\\b");
				break;
			case '\n':
				r.append("\\n");
				break;
			case '\t':
				r.append("\\t");
				break;
			case '\f':
				r.append("\\f");
				break;
			case '\r':
				r.append("\\r");
				break;
			case '\\':
			case '"':
				r.append('\\');
			default:
				r.append(s.charAt(i));
			}
		}
		r.append('\"');
		return r.toString();
	}



	@SuppressWarnings("serial")
	public static class DoubleQuoteUnexpectedEofException extends RuntimeException {
		public final int pos;

		public DoubleQuoteUnexpectedEofException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
	}

	@SuppressWarnings("serial")
	public static class DoubleQuoteUnexpectedCharacterException extends RuntimeException {
		public final int pos;

		public DoubleQuoteUnexpectedCharacterException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
	}

}
