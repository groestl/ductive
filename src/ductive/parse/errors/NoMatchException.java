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
package ductive.parse.errors;

import ductive.parse.ParseContext;


public class NoMatchException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParseContext ctx;

	public long pos;

	public NoMatchException(ParseContext ctx, long pos) {
		this.ctx = ctx;
		this.pos = pos;
	}

	public NoMatchException(String message, ParseContext ctx, long pos) {
		super(message);
		this.ctx = ctx;
		this.pos = pos;
	}

	public NoMatchException(ParseContext ctx, long pos, Throwable cause) {
		super(cause);
		this.ctx = ctx;
		this.pos = pos;
	}

	public NoMatchException(String message, ParseContext ctx, long pos, Throwable cause) {
		super(message,cause);
		this.ctx = ctx;
		this.pos = pos;
	}

	@Override
	public String getMessage() {
		StringBuilder b = new StringBuilder();
		b.append("Parse exception at pos ");
		b.append(ctx.locate(pos));
		b.append(": ");
		b.append(super.getMessage());
		return b.toString();
	}

	public long originalPos() {
		return ctx.locate(pos);
	}

}

