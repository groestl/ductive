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
import ductive.parse.locators.LookupPosMapping;
import ductive.parse.parsers.quoting.PerCharacterQuoteParserUtils;
import ductive.parse.parsers.quoting.UnquoteResult;

public class PerCharacterQuoteParser<T> extends Parser<T> {

	public static final String EXTRA_CONTENT_MESSAGE = "Extra content '%s' at the end of single quoted literal '%s'";


	private Parser<T> inner;

	public PerCharacterQuoteParser(Parser<T> inner) {
		this.inner = inner;
	}

	@Override
	public Result<T> doApply(ParseContext ctx) {
		final UnquoteResult ur = PerCharacterQuoteParserUtils.unquote(ctx);

		ParseContext innerCtx = ctx.fork(ur.unquoted,new LookupPosMapping(ur.offsets));
		Result<T> r = inner.apply(innerCtx);
		if(r.ctx.length()>0)
			throw new ExtraContentException(String.format(EXTRA_CONTENT_MESSAGE,r.ctx,innerCtx),r.ctx,innerCtx,ctx,innerCtx.length()-r.ctx.length()+1);

		return Result.make(r.value,ctx.eatInput(ur.pos));
	}

	// FIXME:
	@Override
	public List<CharSequence> suggest(ParseContext ctx) {
		List<CharSequence> result = new ArrayList<>();
		List<CharSequence> suggested = inner.suggest(ctx);

		for(CharSequence s : suggested)
			result.add(PerCharacterQuoteParserUtils.quoted(s));

		return result;
	}

}
