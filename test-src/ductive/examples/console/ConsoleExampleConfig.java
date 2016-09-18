package ductive.examples.console;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import ductive.console.annotations.ConsoleFile;
import ductive.console.annotations.ConsoleFileTemplates;
import ductive.console.annotations.EnableConsole;
import ductive.console.annotations.EnableConsoleFiles;
import ductive.console.annotations.EnableDefaultCommands;
import ductive.console.annotations.EnableStats;
import ductive.console.shell.HistoryProvider;
import ductive.console.shell.InMemoryHistoryProvider;

@PropertySource("classpath:ductive/examples/console/console.properties")
@EnableConsole(host="${example_console.host}",port="${example_console.port}",hostKeyFile="${example_console.host_key_file}")
@EnableConsoleFiles({
	@ConsoleFile(path="${example_console.shell_path}",template=ConsoleFileTemplates.shell,permissions="rwxr-x---"),
	@ConsoleFile(path="${example_console.portfile_path}",template=ConsoleFileTemplates.port),
})
@EnableDefaultCommands
@EnableStats
public class ConsoleExampleConfig {

	@Bean public HistoryProvider historyProvider() { return new InMemoryHistoryProvider(); }
	@Bean public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() { return new PropertySourcesPlaceholderConfigurer(); }
}
