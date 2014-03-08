package ductive.stats.providers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;

import ductive.stats.StatsProvider;
import ductive.stats.ValueConverter;
import ductive.stats.Values;

public class GuavaCacheStatsProvider implements StatsProvider {
	
	private Cache<?,?> cache;

	public GuavaCacheStatsProvider(Cache<?,?> cache	) {
		this.cache = cache;
	}

	@Override
	public Object query(String[][] paths, ValueConverter[] converters) {
		CacheStats s = cache.stats();
		return Values
				.add("average_load_penalty",s.averageLoadPenalty())
				.add("eviction_count",s.evictionCount())
				.add("hit_count",s.hitCount())
				.add("hit_rate",s.hitRate())
				.add("load_count",s.loadCount())
				.add("load_exception_count",s.loadExceptionCount())
				.add("load_success_count",s.loadSuccessCount())
				.add("miss_count",s.missCount())
				.add("miss_rate",s.missRate())
				.add("request_count",s.requestCount())
				.add("total_load_time",s.totalLoadTime())
			.map();
	}

}
