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
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;

import ductive.parse.CompleteResult;
import ductive.parse.ParseContext;
import ductive.parse.Parser;
import ductive.parse.Result;
import ductive.parse.errors.NoMatchException;

public class OrParser<T> extends Parser<T> {

	private Parser<? extends T>[] alternatives;

	public OrParser(Parser<? extends T>[] alternatives) {
		Validate.isTrue(alternatives.length>0);
		this.alternatives = alternatives;
	}

	@Override
	public Result<T> doApply(ParseContext ctx) {
		Long maxPos=null;
		NoMatchException ex=null;

		List<SuggestException> suggestErrors = new ArrayList<>();
		for(Parser<? extends T> a : alternatives) {
			try {
				return a.apply(ctx).cast();
			} catch(NoMatchException e) {
				long pos = e.originalPos();
				if(maxPos == null || pos>maxPos) {
					ex=e;
					maxPos=pos;
				}
			} catch(SuggestException e) {
				suggestErrors.add(e);
			}
		}

		if(suggestErrors.size()>0)
			throw aggregateSuggests(suggestErrors);

		Validate.notNull(ex);
		throw ex;
	}

	private RuntimeException aggregateSuggests(List<SuggestException> suggestErrors) {
		Set<CharSequence> suggestions = new TreeSet<>();
		Integer suggestionPos = null;
		for(SuggestException e : suggestErrors) {
			if( suggestionPos==null || e.result.suggestionPos>suggestionPos ) {
				suggestions = new TreeSet<>();
				suggestionPos = e.result.suggestionPos; // FIXME:
			}
			suggestions.addAll(e.result.suggestions);
		}
		return new SuggestException(new CompleteResult(new ArrayList<>(suggestions),suggestionPos!=null?suggestionPos:0),null); // FIXME: exception caus
	}

	@Override
	public List<CharSequence> suggest(ParseContext ctx) {
		List<CharSequence> result = new ArrayList<>();
		for(Parser<? extends T> p : alternatives)
			result.addAll(p.suggest(ctx));
		return result;
	}

//	@Override
//	public Examples examples() {
//		List<Examples> expectors = new ArrayList<>();
//		for(Parser<? extends T> a : alternatives)
//			expectors.add(a.examples());
//		return new OrExamples(expectors);
//	}
////
//	private static class OrExamples implements Examples {
//
//		private List<Examples> expectors;
//
//		public OrExamples(List<Examples> expectors) {
//			this.expectors = expectors;
//		}
//
//
//
//	}

}
