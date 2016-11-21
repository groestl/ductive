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
