package ductive.console.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import ductive.console.commands.lib.GroovyCommands;
import ductive.console.commands.lib.HelpCommands;
import ductive.console.commands.lib.LogCommands;
import ductive.console.shell.EmbeddedGroovyShell;
import ductive.console.shell.HistoryProvider;

@Configuration
@Import(DefaultArgumentParserConfig.class)
public class DefaultCommandsConfig {

	@Bean
	public GroovyCommands groovyCommands() {
		return new GroovyCommands();
	}

	@Bean
	public HelpCommands helpCommands() {
		return new HelpCommands();
	}

	@Bean
	public LogCommands logCommands() {
		return new LogCommands();
	}

	@Bean
	@Autowired
	@Scope("prototype")
	public EmbeddedGroovyShell groovyShell(HistoryProvider historyProvider) {
		EmbeddedGroovyShell groovyShell = new EmbeddedGroovyShell();
		groovyShell.setHistoryProvider(historyProvider);
		return groovyShell;
	}

}
