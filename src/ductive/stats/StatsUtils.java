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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public abstract class StatsUtils {

	public static String[][] parseQueries(String ... paths) {
		if(paths==null)
			return null;
		String[][] result = null;
		for(String path : paths)
			result = ArrayUtils.add(result,StringUtils.split(path,'.'));
		return result;
	}

	public static Object query(Object value, String[][] paths, ValueConverter[] converters) {
		value = expand(value,paths,converters);

		if(value==null)
			return null;

		if( Number.class.isInstance(value) || String.class.isInstance(value) )
			if(paths==null)
				return value;
			else
				return null;

		if(!Map.class.isInstance(value))
			throw new RuntimeException(String.format("value '%s' not supported",value));

		Map<String,Object> m = mapCast(value);

		Map<String,Object> result = new HashMap<>();
		if(paths==null) {

			for(Entry<String,Object> e : m.entrySet())
				result.put(e.getKey(),query(e.getValue(),null,converters));

		} else {

			Map<String,String[][]> subqueries = subqueries(paths);

			for(Entry<String,String[][]> e : subqueries.entrySet()) {
				Object subvalue = m.get(e.getKey());
				if(subvalue==null)
					continue;

				result.put(e.getKey(),query(subvalue,e.getValue(),converters));
			}
		}

		return result;
	}


	private static Object expand(Object value, String[][] query, ValueConverter[] converters) {
		while(StatsProvider.class.isInstance(value))
			value = StatsProvider.class.cast(value).query(query,converters);

		if(value==null || Number.class.isInstance(value) || String.class.isInstance(value) || Map.class.isInstance(value))
			return value;

		return convert(value,converters);
	}

	private static Object convert(Object value, ValueConverter[] converters) {
		if(converters!=null)
			for(ValueConverter c : converters)
				if(c.supports(value))
					return c.convert(value);
		throw new RuntimeException(String.format("cannot convert value '%s' of type %s using converters %s",value,value.getClass().getCanonicalName(),Arrays.toString(converters)));
	}

	@SuppressWarnings("unchecked")
	public static Map<String,Object>mapCast(Object value) {
		return (Map<String,Object>)value;
	}

	private static Map<String,String[][]> subqueries(String[][] paths) {
		if(ArrayUtils.isEmpty(paths))
			return null;

		Map<String,String[][]> subqueries = new HashMap<>();
		for(String[] path : paths) {
			if(ArrayUtils.isEmpty(path))
				continue;

			String name = path[0];

			String[][] subquery = subqueries.get(name);
			if(subquery!=null || !subqueries.containsKey(name)) {
				String[] subarray = ArrayUtils.subarray(path,1,path.length);
				if(ArrayUtils.isEmpty(subarray))
					subqueries.put(name,null);
				else
					subqueries.put(name,ArrayUtils.add(subquery,subarray));
			}
		}
		return subqueries;
	}


	@SuppressWarnings("unchecked")
	public static Map<String,Object> makeFlat(Object tree) {
		Validate.isTrue(Map.class.isInstance(tree));
		Map<String,Object> result = new TreeMap<>();
		postprocess((Map<String,Object>)tree,null,result);
		return result;
	}

	private static void postprocess(Map<String, Object> tree, String[] path, Map<String,Object> result) {
		for(Entry<String,Object> e : tree.entrySet()) {
			Object val = e.getValue();
			if(Map.class.isInstance(val)) {
				postprocess(mapCast(e.getValue()),ArrayUtils.add(path,e.getKey()),result);
			} else {
				result.put(StringUtils.join(ArrayUtils.add(path,e.getKey()),'.'),val);
			}
		}
	}

}
