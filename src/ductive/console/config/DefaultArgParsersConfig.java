package ductive.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ductive.console.commands.lib.DefaultArgParsers;

@Configuration
public class DefaultArgParsersConfig {

	@Bean public DefaultArgParsers defaultArgParsers() {
		return new DefaultArgParsers();
	}

}
