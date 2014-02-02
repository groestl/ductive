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
import java.util.List;

import jline.console.completer.Completer;

import org.junit.Before;
import org.junit.Test;

import ductive.parse.Parser;
import ductive.parse.Parsers;
import ductive.parse.Tuple;
import ductive.parse.jline.CompletorAdapter;

public class ExpectorTests {
	
	private ArrayList<CharSequence> suggestions;
	private Parser<Tuple<String,String>> parser;
	private Completer completer;
	
	@Before
	public void before() {
		suggestions = new ArrayList<>();
		
		
		Parser<String> first = Parsers.or(Parsers.string("aa"),Parsers.string("bb")).followedBy(Parsers.WHITESPACE).token();
		Parser<String> second = Parsers.or(Parsers.string("11"),Parsers.string("22")).followedBy(Parsers.WHITESPACE).token();
		
		
		parser = Parsers.tuple(first,second);
		completer = new CompletorAdapter<>(parser);
	}
	
	@Test
	public void test() {
		String text = "hello";
		long pos = new CompletorAdapter<>(Parsers.string("hello").token()).complete(text,3,suggestions);
		assertThat(suggestions,is(list("hello")));
		assertEquals(0,pos);
	}
	
	@Test
	public void empty() {
		long pos=completer.complete("",0,suggestions);
		assertThat(suggestions,is(list("aa ","bb ")));
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
		assertThat(suggestions,is(list("11 ","22 ")));
		assertEquals(3,pos);
	}
	
	@Test
	public void aa_1() {
		long pos=completer.complete("aa 1",4,suggestions);
		assertThat(suggestions,is(list("11 ")));
		assertEquals(3,pos);
	}

	@Test
	public void aa_11() {
		long pos=completer.complete("aa 11",5,suggestions);
		assertThat(suggestions,is(list("11 ")));
		assertEquals(3,pos);
	}
	
	@Test
	public void aa_11_() {
		long pos=completer.complete("aa 11 ",6,suggestions);
		assertThat(suggestions,is(list()));
		assertEquals(0,pos);
	}

	private List<CharSequence> list(String ... args) {
		List<CharSequence> result = new ArrayList<>();
		for(String arg : args)
			result.add(arg);
		return result;
	}

}
