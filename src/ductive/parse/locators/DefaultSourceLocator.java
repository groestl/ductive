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
package ductive.parse.locators;

import java.util.ArrayList;
import java.util.List;

public class DefaultSourceLocator implements SourceLocator {

	private final CharSequence originalSource;
	
	private final List<PosMapping> posMappings;

	public DefaultSourceLocator(CharSequence originalSource) {
		this.originalSource = originalSource;
		this.posMappings = new ArrayList<>();
	}
	
	public DefaultSourceLocator(DefaultSourceLocator other, PosMapping mapping) {
		this.originalSource = other.originalSource;
		this.posMappings = new ArrayList<>(other.posMappings);
		this.posMappings.add(0,mapping);
	}
	
	@Override
	public SourceLocator map(PosMapping f) {
		return new DefaultSourceLocator(this,f);
	}
	
	@Override
	public boolean isEof(long pos) {
		return originalPos(pos)>=originalSource.length();
	}
	
	@Override
	public long originalPos(long pos) {
		for(PosMapping m : posMappings)
			pos = m.map(pos);
		return pos;
	}
	
	@Override
	public boolean isOriginalEof(long originalPos) {
		return originalPos>=originalSource.length();
	}

}
