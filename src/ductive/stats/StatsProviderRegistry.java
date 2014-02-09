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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.springframework.util.ReflectionUtils;


import ductive.commons.ToString;

public class StatsProviderRegistry {
	
	private Map<String,Object> root = new HashMap<>();


	public void register(String[] path, Object bean, Method method) {
		Validate.isTrue(path.length>=1);
		validatePath(path);
		registerInternal(root,0,path,bean,method);
	}

	private void validatePath(String[] path) {
		for(String name: path) {
			Validate.notBlank(name,String.format("path must not contain blank path segments"));
			Validate.isTrue(!name.contains("."),String.format("path segments must not contain dots"));
		}
	}

	private void registerInternal(Map<String,Object> map, int i, String[] path, Object bean, Method method) {
		String name = path[i];
		
		if(i<path.length-1) {
			Object subval = map.get(name);
			Validate.isTrue(subval==null||Map.class.isInstance(subval),String.format("cannot register subtree: name '%s' already registered to a provider",name));
			
			Map<String,Object> submap = subval!=null ? StatsUtils.mapCast(subval) : new HashMap<String,Object>();
			registerInternal(submap,++i,path,bean,method);
			map.put(name,submap);
			return;
		}
		
		Validate.isTrue(!map.containsKey(name),String.format("cannot register provider: name '%s' already registered",name));
		
		map.put(name,new ProviderEntry(path,bean,method));
	}
	
	
	public static class ProviderEntry implements StatsProvider {

		public final String[] path;
		public final Object bean;
		public final Method method;

		public ProviderEntry(String[] path, Object bean, Method method) {
			this.path = path;
			this.bean = bean;
			this.method = method;
		}
		
		@Override
		public Object query(String[][] paths, ValueConverter[] converters) {
			return StatsProviderRegistry.query(bean,method,paths,converters);
		}
		
		@Override public String toString() { return ToString.me(this); }
		
	}
	
	private static Object query(Object bean, Method method, String[][] paths, ValueConverter[] converters) {
		return ReflectionUtils.invokeMethod(method,bean);
	}
	
	public static class StatsEntry {
		
		public final String[] path;
		public final Object value;
		
		public StatsEntry(String[] path,Double value) {
			this.path = path;
			this.value = value;
		}
		
	}
	
	public Object query(String[][] paths, ValueConverter[] converters) {
		return StatsUtils.query(root,paths,converters);
	}
	
}
