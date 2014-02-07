package ductive.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

import ductive.config.DefaultHealthChecks;
import ductive.console.commands.lib.MetricsCommands;

@Configuration
@Import(DefaultArgumentParserConfig.class)
public class DefaultMetricsCommandsConfig {

	@Bean public HealthCheckRegistry healthCheckRegistry() {
		return	new HealthCheckRegistry();
	}

	@Bean public DefaultHealthChecks defaultHealthChecks() {
		return new DefaultHealthChecks();
	}

	@Bean public MetricsCommands metricsCommands() {
		return new MetricsCommands();
	}

	@Bean public MetricRegistry metricRegistry() {
		return new MetricRegistry();
	}

}
