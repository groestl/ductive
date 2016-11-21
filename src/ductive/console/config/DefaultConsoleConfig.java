/*
 	Copyright (c) 2014 code.fm

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
 */
package ductive.console.config;

import java.io.IOException;

import javax.inject.Provider;

import org.apache.sshd.server.SshServer;
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
			HistoryProvider historyProvider,
			StandardPromptProvider standardPromptProvider
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
				return new CmdParser(cmdParserBuilder.buildParser(commandRegistry.commands()),false);
			}
		};

		final EmbeddedAppShell appShell = new EmbeddedAppShell();
		appShell.setCmdParserProvider(cmdParserProvider);
		appShell.setCommandInvoker(commandInvoker);
		appShell.setHistoryProvider(historyProvider);
		appShell.setStandardPromptProvider(standardPromptProvider);


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
