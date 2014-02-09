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
package ductive.stats;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Provider;

public abstract class Values {
	
	@SuppressWarnings("rawtypes")
	private static Provider TREEMAP_PROVIDER = new Provider() { @Override public Map get() { return new TreeMap();	} };

	@SuppressWarnings("unchecked")
	public static MapBuilder add(String key, Object value) {
		return new MapBuilder(key,value,(Provider<Map<String,Object>>)TREEMAP_PROVIDER);
	}
	
	public static class MapBuilder {
		
		private final Provider<Map<String,Object>> provider;
		private final Map<String,Object> m;

		public MapBuilder(String key, Object value, Provider<Map<String,Object>> provider) {
			this.provider = provider;
			m = provider.get();
			m.put(key,value);
		}
		
		public MapBuilder(Map<String,Object> map, String key, Object value, Provider<Map<String,Object>> provider) {
			this.provider = provider;
			Map<String,Object> copy = provider.get();
			copy.putAll(map);
			copy.put(key,value);
			m = copy;
		}

		public MapBuilder add(String key, Object value) {
			return new MapBuilder(this.m,key,value,provider);
		}

		public Map<String,Object> map() {
			return Collections.unmodifiableMap(m);
		}
		
	}

}
