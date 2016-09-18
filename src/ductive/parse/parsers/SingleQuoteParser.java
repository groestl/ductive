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

import java.util.ArrayList;
import java.util.List;

import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;
import ductive.parse.errors.ExtraContentException;
import ductive.parse.errors.NoFinishingQuoteException;
import ductive.parse.errors.UnexpectedCharacterException;
import ductive.parse.errors.UnexpectedEofException;
import ductive.parse.locators.LookupPosMapping;
import ductive.parse.parsers.quoting.SingleQuoteParserUtils;
import ductive.parse.parsers.quoting.SingleQuoteParserUtils.SingleQuoteUnexpectedCharacterException;
import ductive.parse.parsers.quoting.SingleQuoteParserUtils.SingleQuoteUnexpectedEofException;
import ductive.parse.parsers.quoting.UnquoteResult;

public class SingleQuoteParser<T> extends Parser<T> {

	public static final String REACHED_EOF_MESSAGE_START = "Reached EOF while looking for starting quote character '";

	public static final String REACHED_EOF_MESSAGE_END = "Reached EOF while looking for finishing quote character '";

	public static final String EXTRA_CONTENT_MESSAGE = "Extra content '%s' at the end of single quoted literal '%s'";


	private Parser<T> inner;

	public SingleQuoteParser(Parser<T> inner) {
		this.inner = inner;
	}

	@Override
	public Result<T> doApply(ParseContext ctx) {
		final UnquoteResult pr;
		try {
			 pr = SingleQuoteParserUtils.unquote(ctx);
		} catch(SingleQuoteUnexpectedEofException e) {
			throw new UnexpectedEofException(e.getMessage(),ctx,e.pos,e);
		} catch(SingleQuoteUnexpectedCharacterException e) {
			throw new UnexpectedCharacterException(e.getMessage(),ctx,e.pos,e);
		}

		if(pr.pos>=ctx.length() && pr.inquotes)
			throw new NoFinishingQuoteException(REACHED_EOF_MESSAGE_END,ctx,pr.pos);

		ParseContext innerCtx = ctx.fork(pr.unquoted,new LookupPosMapping(pr.offsets));
		Result<T> r = inner.apply(innerCtx);
		if(r.ctx.length()>0)
			throw new ExtraContentException(String.format(EXTRA_CONTENT_MESSAGE,r.ctx,innerCtx),r.ctx,innerCtx,ctx,innerCtx.length()-r.ctx.length()+1);

		return Result.make(r.value,ctx.eatInput(pr.pos));
	}

	@Override public List<CharSequence> suggest(ParseContext ctx) {
		List<CharSequence> result = new ArrayList<>();
		List<CharSequence> suggested = inner.suggest(ctx);

		for(CharSequence s : suggested)
			result.add(SingleQuoteParserUtils.quoted(s));

		return result;
	}

}
