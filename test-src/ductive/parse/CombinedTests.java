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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Function;


public class CombinedTests {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void test() {
		String text = StringUtils.join(Arrays.asList("line1","'line2'","\"line3\""),"\n");
		
		Parser<?> endOfLine = Parsers.or(Parsers.EOL,Parsers.EOF);
		Parser<String> stuff = Parsers.string("line").followedBy(Parsers.number());
		Parser<String> stuffRepr = Parsers.or(Parsers.doubleQuoted(stuff),Parsers.singleQuoted(stuff),stuff);
		Parser<String> line = stuffRepr.followedBy(endOfLine);
		Parser<List<String>> lines = line.many1();
		
		List<String> result = lines.parseFully(text);
		
		assertEquals(3,result.size());
		assertThat(result,is(Arrays.asList("line","line","line")));
	}
	
	
	@Test
	public void test1() {
		Parser<String> hello = Parsers.string("hello").map(new Function<String,String>() {
			@Override
			public String apply(String input) {
				return input;
			}
		});
		
		{
			Parser<String> a = Parsers.string("\"").precedes(hello).followedBy(Parsers.string("\"")).followedBy(Parsers.WHITESPACE);
			String result = a.parseFully("\"hello\"  ");
			assertEquals("hello",result);
		}
		
		{
			Parser<String> b = Parsers.NON_WHITESPACE.followedBy(Parsers.WHITESPACE);
			String result = b.parseFully("\"hello\"  ");
			assertEquals("\"hello\"",result);
		}
	}
	
}
