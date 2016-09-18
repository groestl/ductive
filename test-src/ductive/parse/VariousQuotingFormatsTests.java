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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import ductive.parse.parsers.quoting.DoubleQuoteParserUtils;
import ductive.parse.parsers.quoting.PerCharacterQuoteParserUtils;

@RunWith(Parameterized.class)
public class VariousQuotingFormatsTests {

	@Parameters(name="check quoted parsing of {0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{
				"hallo there",
			},
			{
				"hello\tthere",
			},
		});
	}

	@Parameter(0)
	public String unquoted;


	@Test
	public void flexibleQuoting() {
		Parser<String> inner = Parsers.ANY;

		Parser<String> quotedParser = Parsers.or(
			Parsers.doubleQuoted(inner),
			Parsers.singleQuoted(inner),
			Parsers.perCharacterQuoted(inner)
		);

		CharSequence[] quoted = {
			DoubleQuoteParserUtils.quoted(this.unquoted),
			PerCharacterQuoteParserUtils.quoted(this.unquoted),
		};

		for(CharSequence b : quoted) {
			String result = quotedParser.parse(b);

			assertEquals(this.unquoted,result);
		}
	}

}
