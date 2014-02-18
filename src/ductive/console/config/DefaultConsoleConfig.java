package ductive.console.config;

import java.io.IOException;
import javax.inject.Provider;

import org.apache.sshd.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import ductive.console.commands.parser.ArgParserFactory;
import ductive.console.commands.parser.CmdParser;
import ductive.console.commands.parser.CmdParserBuilder;
import ductive.console.commands.register.ArgParserRegistry;
import ductive.console.commands.register.CommandInvoker;
import ductive.console.commands.register.CommandRegistry;
import ductive.console.commands.register.DefaultArgParserRegistry;
import ductive.console.commands.register.DefaultCommandRegistry;
import ductive.console.commands.register.spring.ArgumentParserBeanPostProcessor;
import ductive.console.commands.register.spring.CommandBeanPostProcessor;
import ductive.console.shell.DefaultCommandFactory;
import ductive.console.shell.DefaultShellFactory;
import ductive.console.shell.EmbeddedAppShell;
import ductive.console.shell.HistoryProvider;

@Configuration
public class DefaultConsoleConfig {

	@Bean public ArgParserRegistry argParserRegistry() { return new DefaultArgParserRegistry(); }
	@Bean public CommandRegistry commandRegistry() { return new DefaultCommandRegistry(); }
	@Bean public ConversionService conversionService() { return new DefaultConversionService(); }

//	@Bean
//	public SshServer defaultSshServerProvider() {
//		SshServer sshd = SshServer.setUpDefaultServer();
//		sshd.setHost("127.0.0.1");
//		sshd.setPort(2200);
//		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
//
//		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
//		userAuthFactories.add(new UserAuthNone.Factory());
//		sshd.setUserAuthFactories(userAuthFactories);
//		return sshd;
//	}

	@Bean
	@Autowired
	public DuctiveConsole shell(
			SshServer sshServer,
			final CommandRegistry commandRegistry,
			ArgParserRegistry argParserRegistry,
			ConversionService conversionService,
			HistoryProvider historyProvider
		) throws IOException {


		final CommandInvoker commandInvoker = new CommandInvoker();
		commandInvoker.setCommandRegistry(commandRegistry);
		commandInvoker.setConversionService(conversionService);


		ArgParserFactory argParserFactory = new ArgParserFactory();

		final CmdParserBuilder cmdParserBuilder = new CmdParserBuilder();
		cmdParserBuilder.setArgParserFactory(argParserFactory);
		cmdParserBuilder.setArgParserRegistry(argParserRegistry);

		final Provider<CmdParser> cmdParserProvider = new Provider<CmdParser>() {
			@Override public CmdParser get() {
				return new CmdParser(cmdParserBuilder.buildParser(commandRegistry.commands()));
			}
		};

		final EmbeddedAppShell appShell = new EmbeddedAppShell();
		appShell.setCmdParserProvider(cmdParserProvider);
		appShell.setCommandInvoker(commandInvoker);
		appShell.setHistoryProvider(historyProvider);


		DefaultShellFactory shellFactory = new DefaultShellFactory();
		shellFactory.setShell(appShell);
		sshServer.setShellFactory(shellFactory);

		DefaultCommandFactory commandFactory = new DefaultCommandFactory();
		commandFactory.setCmdParserProvider(cmdParserProvider);
		commandFactory.setCommandInvoker(commandInvoker);
		sshServer.setCommandFactory(commandFactory);


		sshServer.start();
		return new DuctiveConsole(sshServer);
	}

	@Bean
	@Autowired
	public CommandBeanPostProcessor commandBeanPostProcessor(CommandRegistry commandRegistry) {
		CommandBeanPostProcessor commandPP = new CommandBeanPostProcessor();
		commandPP.setCommandRegistry(commandRegistry);
		return commandPP;
	}

	@Bean
	@Autowired
	public ArgumentParserBeanPostProcessor argumentParserBeanPostProcessor(ArgParserRegistry argParserRegistry) {
		ArgumentParserBeanPostProcessor pp = new ArgumentParserBeanPostProcessor();
		pp.setArgParserRegistry(argParserRegistry);
		return pp;
	}

	public static class DuctiveConsole {

		private SshServer sshd;

		public DuctiveConsole(SshServer sshd) {
			this.sshd = sshd;
		}

		public SshServer sshd() {
			return sshd;
		}

	}

}
