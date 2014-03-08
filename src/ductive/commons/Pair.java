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
package ductive.commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class Pair<A,B> {
	
	public final A a;
	public final B b;

	public Pair(A a,B b) {
		this.a = a;
		this.b = b;
	}
	
	@Override public int hashCode() {
		return new HashCodeBuilder()
			.append(a)
			.append(b)
			.toHashCode();
	}
	
	@Override public boolean equals(Object obj) {
		return Equals.eq(this,obj,new Equals.EqualsDefinition<Pair<A,B>>() {
			@Override public void check(Pair<A, B> l, Pair<A, B> r, EqualsBuilder b) {
				b.append(l.a,r.a)
				 .append(l.b,r.b);
			}
		});
	}
	
	public static <A,B> Pair<A,B> make(A a,B b) {
		return new Pair<A,B>(a,b);
	}
	
	@Override public String toString() { return ToString.me(this); }

}
