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

import jline.console.completer.Completer;

import org.junit.Before;
import org.junit.Test;

import ductive.parse.Parser;
import ductive.parse.Parsers;
import ductive.parse.Tuple;
import ductive.parse.errors.UnexpectedCharacterException;
import ductive.parse.jline.CompletorAdapter;

public class SimpleCompleterTests {

	private ArrayList<CharSequence> suggestions;
	private Parser<String> parser;
	private Completer completer;

	@Before
	public void before() {
		suggestions = new ArrayList<>();

		parser = Parsers.string("aa").followedBy(Parsers.WHITESPACE).token();
		completer = new CompletorAdapter<>(parser);
	}

	@Test
	public void a1() {
		Parser<String> example = Parsers.string("bla").example("a","b").token();
		CompletorAdapter<String> completor = new CompletorAdapter<>(example);
		int pos = completor.complete("",0,suggestions);
		assertThat(suggestions,is(list("a","b")));
		assertEquals(0,pos);
	}

	@Test
	public void empty() {
		long pos=completer.complete("",0,suggestions);
		assertThat(suggestions,is(list("aa ")));
		assertEquals(0,pos);
	}

	@Test
	public void a() {
		long pos=completer.complete("a",1,suggestions);
		assertThat(suggestions,is(list("aa ")));
		assertEquals(0,pos);
	}

	@Test
	public void aa() {
		long pos=completer.complete("aa",2,suggestions);
		assertThat(suggestions,is(list("aa ")));
		assertEquals(0,pos);
	}

	@Test
	public void aa_() {
		long pos=completer.complete("aa ",3,suggestions);
		assertEquals(0,suggestions.size());
		assertEquals(0,pos);
	}

	@Test(expected=UnexpectedCharacterException.class)
	public void b() {
		long pos=completer.complete("b",1,suggestions);
		assertEquals(1,suggestions.size());
		assertEquals(0,pos);
	}

	private List<CharSequence> list(String ... args) {
		List<CharSequence> result = new ArrayList<>();
		for(String arg : args)
			result.add(arg);
		return result;
	}

	@Test
	public void list() {
		Parser<List<String>> p = Parsers.list(Arrays.asList(Parsers.string("--"),Parsers.string("arg"))).token();
		CompletorAdapter<List<String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("-",1,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

	@Test
	public void list2() {
		Parser<List<String>> p = Parsers.list(Arrays.asList(Parsers.string("--"),Parsers.string("arg"))).token();
		CompletorAdapter<List<String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("--",2,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

	@Test
	public void list3() {
		Parser<List<String>> p = Parsers.list(Arrays.asList(Parsers.string("--"),Parsers.string("arg"))).token();
		CompletorAdapter<List<String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("--a",3,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

	@Test
	public void list4() {
		Parser<List<String>> p = Parsers.list(Arrays.asList(Parsers.string("--"),Parsers.string("arg"))).token();
		CompletorAdapter<List<String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("--a",2,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

	@Test
	public void tuple1() {
		Parser<Tuple<String,String>> p = Parsers.tuple(Parsers.string("--"),Parsers.string("arg")).token();
		CompletorAdapter<Tuple<String, String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("-",1,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

	@Test
	public void tuple2() {
		Parser<Tuple<String,String>> p = Parsers.tuple(Parsers.string("--"),Parsers.string("arg")).token();
		CompletorAdapter<Tuple<String, String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("",0,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

	@Test
	public void tuple3() {
		Parser<Tuple<String,String>> p = Parsers.tuple(Parsers.string("--"),Parsers.string("arg")).token();
		CompletorAdapter<Tuple<String, String>> completer = new CompletorAdapter<>(p);
		long pos=completer.complete("--a",3,suggestions);
		assertThat(suggestions,is(list("--arg")));
		assertEquals(0,pos);
	}

}
