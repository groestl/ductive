package ductive.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ductive.console.commands.lib.DefaultArgParsers;

@Configuration
public class DefaultArgumentParserConfig {

	@Bean public DefaultArgParsers defaultArgParsers() {
		return new DefaultArgParsers();
	}

}
