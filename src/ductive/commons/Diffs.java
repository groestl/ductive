package ductive.commons;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;


public class Diffs {
	
	public static DiffBuilder diff() {
		return new DiffBuilder();
	}
	
	public static class DiffBuilder {
		
		final private Map<Object,Object> l;
		final private Map<Object,Object> r;
		
		public DiffBuilder() {
			l = new LinkedHashMap<>();
			r = new LinkedHashMap<>();
		}

		public DiffBuilder(DiffBuilder o) {
			l = new LinkedHashMap<>(o.l);
			r = new LinkedHashMap<>(o.r);
		}

		public DiffBuilder append(Object key, Object a, Object b) {
			DiffBuilder builder = new DiffBuilder(this);
			builder.l.put(key,a);
			builder.r.put(key,b);
			return builder;
		}
		
		public MapDifference<Object,Object> diff() {
			return Maps.difference(l,r);
		}
		
	}


}
