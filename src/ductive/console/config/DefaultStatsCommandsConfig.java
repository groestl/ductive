package ductive.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ductive.console.commands.lib.StatsCommands;

@Configuration
@Import(DefaultArgumentParserConfig.class)
public class DefaultStatsCommandsConfig {

	@Bean public StatsCommands statsCommands() {
		return new StatsCommands();
	}

}
