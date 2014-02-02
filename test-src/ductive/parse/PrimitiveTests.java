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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ductive.parse.Parser;
import ductive.parse.Parsers;
import ductive.parse.errors.ExtraContentException;
import ductive.parse.errors.NoFinishingQuoteException;
import ductive.parse.errors.NoMatchException;
import ductive.parse.errors.UnexpectedCharacterException;
import ductive.parse.errors.UnexpectedEofException;
import ductive.parse.parsers.DoubleQuoteParser;
import ductive.parse.parsers.EOLParser;
import ductive.parse.parsers.LongParser;
import ductive.parse.parsers.SingleQuoteParser;
import ductive.parse.parsers.StringParser;


public class PrimitiveTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void stringSimple() {
		Parser<String> abc = Parsers.string("abc");
		String result = abc.parseFully("abc");
		assertEquals("abc",result);
	}

	@Test
	public void stringUnexpectedEof() {
		thrown.expect(UnexpectedEofException.class);
		thrown.expectMessage(String.format(StringParser.UNEXPECTED_EOF_MESSAGE,"abc"));
		Parser<String> abc = Parsers.string("abc");
		abc.parseFully("");
		throw new IllegalStateException();
	}

	@Test
	public void stringUnexpectedCharacter() {
		thrown.expect(UnexpectedCharacterException.class);
		thrown.expectMessage(String.format(StringParser.UNEXPECTED_CHARACTER_MESSAGE,"d","c",2));
		Parser<String> abc = Parsers.string("abc");
		abc.parseFully("abd");
		throw new IllegalStateException();
	}

	@Test
	public void or1() {
		Parser<String> p = Parsers.string("a").or(Parsers.string("b"));
		String a = p.parseFully("a");
		String b = p.parseFully("b");
		assertEquals("a",a);
		assertEquals("b",b);
	}

	@Test(expected=NoMatchException.class)
	public void or2() {
		Parser<String> p = Parsers.string("a").or(Parsers.string("b"));
		p.parseFully("c");
	}

	@Test(expected=NoMatchException.class)
	public void or3() {
		Parser<String> p = Parsers.string("a").or(Parsers.string("b"));
		p.parseFully("");
	}

	@Test
	public void doubleQuoted() {
		String literal = "a\"b\nc\\d";
		Parser<String> p2 = Parsers.doubleQuoted(Parsers.string(literal));
		String result = p2.parseFully("\"a\\\"b\\\nc\\\\d\"");
		assertEquals(literal, result);
	}

	@Test
	public void openDoubleQuoted() {
		thrown.expect(NoFinishingQuoteException.class);
		thrown.expectMessage(DoubleQuoteParser.REACHED_EOF_MESSAGE_END);
		Parser<String> p2 = Parsers.doubleQuoted();
		p2.parseFully("\"abc");
		throw new IllegalStateException();
	}

	@Test
	public void doubleQuotedInner() {
		Parser<String> quoted = Parsers.doubleQuoted(Parsers.string("abc"));
		String result = quoted.parseFully("\"abc\"");
		assertEquals("abc",result);
	}

	@Test
	public void doubleQuotedExtraContent() {
		thrown.expect(ExtraContentException.class);
		thrown.expectMessage(String.format(DoubleQuoteParser.EXTRA_CONTENT_MESSAGE,"d","abcd"));
		Parser<String> quoted = Parsers.doubleQuoted(Parsers.string("abc"));
		quoted.parseFully("\"abcd\"");
		throw new IllegalStateException();
	}

	@Test
	public void doubleQuote() {
		Parser<String> p2 = Parsers.doubleQuoted();
		String result = p2.parseFully("\"abc\"");
		assertEquals("abc",result);
	}

	@Test
	public void emptyDoubleQuote() {
		thrown.expect(UnexpectedEofException.class);
		thrown.expectMessage(String.format(DoubleQuoteParser.REACHED_EOF_MESSAGE_START));
		Parser<String> p2 = Parsers.doubleQuoted();
		p2.parseFully("");
		throw new IllegalStateException();
	}

	@Test
	public void singleQuote() {
		Parser<String> p2 = Parsers.singleQuoted();
		String result = p2.parseFully("'abc'");
		assertEquals("abc",result);
	}

	@Test
	public void emptySingleQuote() {
		thrown.expect(UnexpectedEofException.class);
		thrown.expectMessage(String.format(SingleQuoteParser.REACHED_EOF_MESSAGE_START));
		Parser<String> p2 = Parsers.singleQuoted();
		p2.parseFully("");
		throw new IllegalStateException();
	}

	@Test
	public void openSingleQuote() {
		thrown.expect(NoFinishingQuoteException.class);
		thrown.expectMessage(String.format(SingleQuoteParser.REACHED_EOF_MESSAGE_END));
		Parser<String> p2 = Parsers.singleQuoted();
		p2.parseFully("'abc");
		throw new IllegalStateException();
	}


	@Test
	public void singleQuotedExtraContent() {
		thrown.expect(ExtraContentException.class);
		thrown.expectMessage(String.format(SingleQuoteParser.EXTRA_CONTENT_MESSAGE,"d","abcd"));
		Parser<String> quoted = Parsers.singleQuoted(Parsers.string("abc"));
		quoted.parseFully("'abcd'");
		throw new IllegalStateException();
	}

	@Test
	public void escapedSingleQuote() {
		Parser<String> p2 = Parsers.singleQuoted();
		String result = p2.parseFully("'ab''c'");
		assertEquals("ab'c",result);
	}

	@Test
	public void quoting() {
		Parser<String> abc = Parsers.string("abc");
		Parsers.singleQuoted(abc);
	}

	@Test
	public void number()  {
		Parser<Long> number = Parsers.number();
		long result = number.parseFully("123");
		assertEquals(123l,result);
	}

	@Test
	public void numberEof()  {
		thrown.expect(UnexpectedEofException.class);
		thrown.expectMessage(String.format(LongParser.UNEXPECTED_EOF_MESSAGE,0));
		Parser<Long> number = Parsers.number();
		number.parseFully("");
		throw new IllegalStateException();
	}

	@Test
	public void numberWrongChar()  {
		thrown.expect(UnexpectedCharacterException.class);
		thrown.expectMessage(String.format(LongParser.UNEXPECTED_CHARACTER_MESSAGE,'a',0));
		Parser<Long> number = Parsers.number();
		number.parseFully("a");
		throw new IllegalStateException();
	}

	@Test
	public void eol()  {
		Parser<String> eol = Parsers.EOL;
		String r = eol.parseFully("\n");
		assertEquals("\n",r);
	}

	@Test
	public void eolReachedEof()  {
		thrown.expect(UnexpectedEofException.class);
		thrown.expectMessage(String.format(EOLParser.UNEXPECTED_EOF_MESSAGE,0));
		Parser<String> eol = Parsers.EOL;
		eol.parseFully("");
		throw new IllegalStateException();
	}

	@Test
	public void eolWrongParse()  {
		thrown.expect(UnexpectedCharacterException.class);
		thrown.expectMessage(String.format(EOLParser.UNEXPECTED_CHARACTER_MESSAGE,'a',0));
		Parser<String> eol = Parsers.EOL;
		eol.parseFully("abc");
		throw new IllegalStateException();
	}

	@Test
	public void atLeast() {
		Parser<String> p1 = Parsers.string("a");
		Parser<List<String>> p2 = p1.many();
		List<String> result = p2.parseFully("aaa");
		assertThat(result,is(Arrays.asList("a","a","a")));
	}

	@Test
	public void atLeastWrong() {
		Parser<String> p1 = Parsers.string("a");
		Parser<List<String>> p2 = p1.many();
		List<String> result = p2.parse("b");
		assertThat(result,is((List<String>)new ArrayList<String>()));
	}

	@Test
	public void atLeastEmpty() {
		Parser<String> p1 = Parsers.string("a");
		Parser<List<String>> p2 = p1.many();
		List<String> result = p2.parseFully("");
		assertThat(result,is((List<String>)new ArrayList<String>()));
	}

	@Test
	public void atLeastOne() {
		Parser<String> p1 = Parsers.string("a");
		Parser<List<String>> p2 = p1.many1();
		List<String> result = p2.parse("abc");
		assertThat(result,is(Arrays.asList("a")));
	}

	@Test
	public void atLeastOneMany() {
		Parser<String> p1 = Parsers.string("a");
		Parser<List<String>> p2 = p1.many1();
		assertThat(p2.parse("aaabc"),is(Arrays.asList("a","a","a")));
	}

	@Test
	public void perCharacterQuote() {
		Parser<String> p1 = Parsers.string("abc");
		Parser<String> parser = Parsers.perCharacterQuoted(p1);
		assertEquals("abc",parser.parseFully("abc"));
	}

	@Test
	public void perCharacterQuote2() {
		Parser<String> p1 = Parsers.string("abc def");
		Parser<String> parser = Parsers.perCharacterQuoted(p1);
		assertEquals("abc def",parser.parseFully("abc\\ def"));
	}

	@Test
	public void perCharacterQuote3() {
		Parser<String> p1 = Parsers.string("abcdef");
		Parser<String> parser = Parsers.perCharacterQuoted(p1);
		assertEquals("abcdef",parser.parseFully("abc\\\ndef"));
	}

	@Test
	public void listParser() {
		Parser<String> p = Parsers.string("a");
		Parser<List<String>> parser = Parsers.list(Arrays.asList(p,p,p));

		List<String> result = parser.parse("aaa");
		assertThat(result,is(Arrays.asList("a","a","a")));
	}

	@Test
	public void listParser2() {
		Parser<String> p = Parsers.string("a");
		Parser<List<String>> parser = Parsers.list(Arrays.asList(p,p,p));

		List<String> result = parser.parse("aaaa");
		assertThat(result,is(Arrays.asList("a","a","a")));
	}

	@Test(expected=UnexpectedEofException.class)
	public void listParser3() {
		Parser<String> p = Parsers.string("a");
		Parser<List<String>> parser = Parsers.list(Arrays.asList(p,p,p));

		List<String> result = parser.parse("aa");
		assertThat(result,is(Arrays.asList("a","a","a")));
	}

}
