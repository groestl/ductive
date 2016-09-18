package ductive.console.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import ductive.console.annotations.EnableDefaultCommands;
import ductive.console.commands.lib.GroovyCommands;
import ductive.console.commands.lib.HelpCommands;
import ductive.console.commands.lib.LogCommands;
import ductive.console.commands.lib.StatsCommands;
import ductive.console.shell.EmbeddedGroovyInterpreter;
import ductive.console.shell.EmbeddedGroovyShell;

public class DefaultCommandsRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDefaultCommands.class.getName()));

		conditionallyRegister(attr,registry,"groovyShell","groovyShellCommands",GroovyCommands.class);
		conditionallyRegister(attr,registry,"help","helpShellCommands",HelpCommands.class);
		conditionallyRegister(attr,registry,"log","logShellCommands",LogCommands.class);
		conditionallyRegister(attr,registry,"stats","statsShellCommands",StatsCommands.class);

		{
			RootBeanDefinition beanDefinition = new RootBeanDefinition(EmbeddedGroovyShell.class);
			beanDefinition.setScope("prototype");
			beanDefinition.getPropertyValues().add("historyProvider",new RuntimeBeanReference(attr.getString("historyProviderBean")));
			registry.registerBeanDefinition("groovyShell",beanDefinition);
		}

		{
			RootBeanDefinition beanDefinition = new RootBeanDefinition(EmbeddedGroovyInterpreter.class);
			beanDefinition.setScope("prototype");
			registry.registerBeanDefinition("groovyInterpreter",beanDefinition);
		}
	}

	private void conditionallyRegister(AnnotationAttributes attr, BeanDefinitionRegistry registry, String attribute, String beanName, Class<?> clazz) {
		if(!attr.getBoolean(attribute))
			return;

		RootBeanDefinition beanDefinition = new RootBeanDefinition(clazz);
		beanDefinition.setSource(null);
		registry.registerBeanDefinition(beanName, beanDefinition);
	}

}
