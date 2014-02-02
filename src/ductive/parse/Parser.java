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

import com.google.common.base.Function;

import ductive.parse.parsers.AtLeastParser;
import ductive.parse.parsers.FollowedByParser;
import ductive.parse.parsers.MapParser;
import ductive.parse.parsers.PrecedesParser;
import ductive.parse.parsers.StringExamples;
import ductive.parse.parsers.TokenParser;

public abstract class Parser<T> {
	
	private static final List<CharSequence> EMPTY_LIST = new ArrayList<>();

	public <R> Parser<R> precedes(Parser<R> follower) {
		return new PrecedesParser<R>(this,follower);
	}

	public Parser<T> followedBy(Parser<?> follower) {
		return new FollowedByParser<T>(this,follower);
	}

	public <R extends T> Parser<T> or(Parser<R> alternative) {
		return Parsers.or(this,alternative);
	}


	// * +

	public Parser<List<T>> many() {
		return atLeast(0);
	}

	public Parser<List<T>> many1() {
		return atLeast(1);
	}

	public Parser<List<T>> atLeast(int atLeast) {
		return new AtLeastParser<>(this,atLeast);
	}


	// mapping

	public <R> Parser<R> map(Function<? super T,? extends R> fn) {
		return new MapParser<T,R>(this,fn);
	}


	// parsing
	
	public Result<T> apply(ParseContext ctx) {
		Result<T> result = doApply(ctx);
		
		if(ctx.isCompleting())
			checkComplete(ctx,result);
		
		return result;
	}

	public abstract Result<T> doApply(ParseContext ctx);
	

	protected void checkComplete(ParseContext ctx, Result<T> result) {
	}


	public final T parse(CharSequence source) {
		return Parsers.parse(source,this);
	}

	public final T parseFully(CharSequence source) {
		return this.followedBy(Parsers.EOF).parse(source);
	}


	// completion

	public Parser<T> token() {
		return new TokenParser<T>(this);
	}

	public List<CharSequence> suggest(ParseContext ctx) {
		return EMPTY_LIST;
	}

//	public Examples examples() {
//		return new EmptyExamples();
//	}

	public Parser<T> example(String ... examples) {
		return example(new StringExamples(examples));
	}
	public Parser<T> example(Examples examples) {
		return new ExamplesWrapperParser<T>(this,examples);
	}

	//	public int complete(ParseContext ctx, List<CharSequence> suggestions) {
	//		throw new NotImplementedException(this.getClass());
	//	}

	public CompleteResult complete(CharSequence input, int cursor) {
		return Parsers.complete(input,cursor,this);
	}

	//	public int complete(ParseContext ctx, List<CharSequence> suggestions) {
	//		if(ctx.length()==0) {
	//			suggestions.addAll(this.suggest(ctx));
	//			return 0;
	//		}
	//
	//		try {
	//			apply(ctx);
	//		} catch(NoMatchException e) {
	//			if(e.pos==0)
	//				return 0;
	//		}
	//
	//		suggestions.addAll(this.suggest(ctx));
	//		return 0;
	//	}

	//	public static <T> Parser<T> token() {
	//		return new CompletionTokenParser();
	//	}



	// helpers

	@SuppressWarnings("unchecked")
	public final <R> Parser<R> cast() {
		return (Parser<R>)this;
	}



}
