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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import ductive.parse.parsers.quoting.DoubleQuoteParserUtils;
import ductive.parse.parsers.quoting.SingleQuoteParserUtils;
import ductive.parse.parsers.quoting.UnquoteResult;

@RunWith(Parameterized.class)
public class SingleQuoteParserTests {

	// 012345678
	// 'dat''um'
	// dat'um
	// 012345
	// 123567
	//
	// 01234567890
	// '''hello'''
	// 'hello'
	// 0123456
	// 2345679
	//
	// 0123456
	// 'apple'
	// apple
	// 12345

	@Parameters(name="check double quote parsing of {0} should be {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{
				"'dat''um'",
				"dat'um",
				new int[]{1,2,3,5,6,7}
			},
			{
				"'''hello'''",
				"'hello'",
				new int[]{2,3,4,5,6,7,9}
			},
			{
				"'apple'",
				"apple",
				new int[]{1,2,3,4,5}
			},
		});
	}

	@Parameter(0)
	public String quoted;

	@Parameter(1)
	public String unquoted;

	@Parameter(2)
	public int[] offsets;

	@Test
	public void parseQuoted() {
		UnquoteResult r = SingleQuoteParserUtils.unquote(quoted);
		assertEquals(this.unquoted,r.unquoted);
		assertArrayEquals(this.offsets,r.offsets);

		CharSequence quotedAgain = SingleQuoteParserUtils.quoted(r.unquoted);
		assertEquals(this.quoted,quotedAgain);
	}

}
