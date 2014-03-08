package ductive.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ductive.config.DefaultStats;
import ductive.stats.StatsProviderRegistry;
import ductive.stats.spring.StatsPostProcessor;

@Configuration
public class DefaultStatsConfig {

	@Bean public StatsProviderRegistry statsProviderRegistry() {
		return new StatsProviderRegistry();
	}

	@Bean public StatsPostProcessor statsPostProcessor() {
		return new StatsPostProcessor();
	}

	@Bean public DefaultStats defaultStats() {
		return new DefaultStats();
	}

}
