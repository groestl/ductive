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

import java.util.List;

import ductive.parse.locators.DefaultSourceLocator;
import ductive.parse.parsers.AnyParser;
import ductive.parse.parsers.DoubleQuoteParser;
import ductive.parse.parsers.EOFParser;
import ductive.parse.parsers.EOLParser;
import ductive.parse.parsers.IsCompletingSwitchParser;
import ductive.parse.parsers.ListParser;
import ductive.parse.parsers.LongParser;
import ductive.parse.parsers.NeverParser;
import ductive.parse.parsers.NonWhitespaceParser;
import ductive.parse.parsers.OrParser;
import ductive.parse.parsers.PerCharacterQuoteParser;
import ductive.parse.parsers.SingleQuoteParser;
import ductive.parse.parsers.StringParser;
import ductive.parse.parsers.SuggestException;
import ductive.parse.parsers.WhitespaceParser;


public class Parsers {

	public static final Parser<Void> EOF = EOFParser.INSTANCE;

	public static final Parser<String> WHITESPACE = WhitespaceParser.INSTANCE;

	public static final Parser<String> NON_WHITESPACE = NonWhitespaceParser.INSTANCE;

	public static final Parser<String> ANY = AnyParser.INSTANCE;

	public static final Parser<String> EOL = EOLParser.INSTANCE;

	private static <T> Parser<T> never() {
		return new NeverParser<>();
	}

	public static Parser<String> string(String recognize) {
		return new StringParser(recognize);
	}
	
	public static Parser<String> istring(String recognize) {
		return new StringParser(recognize,false);
	}

	public static Parser<String> doubleQuoted() {
		return doubleQuoted(ANY);
	}

	public static <T> Parser<T> doubleQuoted(Parser<T> inner) {
		return new DoubleQuoteParser<>(inner);
	}

	public static Parser<String> singleQuoted() {
		return singleQuoted(ANY);
	}

	public static <T> Parser<T> singleQuoted(Parser<T> inner) {
		return new SingleQuoteParser<>(inner);
	}

	public static Parser<String> perCharacterQuoted() {
		return new PerCharacterQuoteParser<>(ANY);
	}

	public static <T> Parser<T> perCharacterQuoted(Parser<T> inner) {
		return new PerCharacterQuoteParser<>(inner);
	}

	@SafeVarargs
	public static <T> Parser<T> or(Parser<? extends T>... alternatives) {
		if (alternatives.length == 0) return never();
		if (alternatives.length == 1) return alternatives[0].cast();
		return new OrParser<T>(alternatives);
	}

	public static <T> Parser<T> or(Iterable<? extends Parser<? extends T>> parsers) {
		return new OrParser<T>(Utils.toArray(parsers));
	}

	@SuppressWarnings("unused")
	private static Parser<Object> alt(Parser<?> ... alternatives) {
		return or(alternatives);
	}

	public static <T> T parse(CharSequence source, Parser<T> parser) {
		ParseContext ctx = new ParseContext(source, new DefaultSourceLocator(source),false);
		return parser.doApply(ctx).value;
	}

	public static Parser<Long> number() {
		return new LongParser();
	}

	public static <T> CompleteResult complete(CharSequence input, int cursor, Parser<T> parser) {
		CharSequence source = input.subSequence(0,cursor);
		ParseContext ctx = new ParseContext(source, new DefaultSourceLocator(source),true);
		try {
			parser.doApply(ctx);
			return CompleteResult.EMPTY;
		} catch(SuggestException e) {
			return new CompleteResult(e.result.suggestions,e.result.suggestionPos);
		}
	}

	public static <A,B> Parser<Tuple<A,B>> tuple(Parser<A> a, Parser<B> b) {
		return new TupleParser<A,B>(a,b);
	}

	public static <T> Parser<List<T>> list(Iterable<? extends Parser<? extends T>> parsers) {
		return new ListParser<T>(Utils.toArray(parsers));
	}

	public static <T> Parser<T> isCompletingSwitch(Parser<? extends T> defaultParser, Parser<? extends T> completingParser) {
		return new IsCompletingSwitchParser<>(defaultParser,completingParser);
	}


}
