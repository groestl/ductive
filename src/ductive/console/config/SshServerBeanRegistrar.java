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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import ductive.console.annotations.EnableConsole;

public class SshServerBeanRegistrar implements ImportBeanDefinitionRegistrar {

	private static final String SSH_SERVER_FACTORY_BEAN_NAME = "sshServer";
	private static final String STANDARD_PROMPT_PROVIDER_BEAN_NAME = "standardPromptProvider";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableConsole.class.getName()));

		{
			RootBeanDefinition beanDefinition = new RootBeanDefinition(SshServerFactoryBean.class);
			beanDefinition.setSource(null);
			beanDefinition.getPropertyValues().add("host",attr.getString("host"));
			beanDefinition.getPropertyValues().add("port",attr.getString("port"));
			beanDefinition.getPropertyValues().add("hostKeyFile",attr.getString("hostKeyFile"));
			beanDefinition.getPropertyValues().add("hostKeyPermissions",attr.getString("hostKeyPermissions"));

			String userAuthFactoryBeanName = attr.getString("userAuthFactoryProvider");
			if(!StringUtils.isBlank(userAuthFactoryBeanName))
				beanDefinition.getPropertyValues().add("userAuthFactoryProvider",new RuntimeBeanReference(userAuthFactoryBeanName));

			String passwordAuthenticatorBeanName = attr.getString("passwordAuthenticator");
			if(!StringUtils.isBlank(passwordAuthenticatorBeanName))
				beanDefinition.getPropertyValues().add("passwordAuthenticator",new RuntimeBeanReference(passwordAuthenticatorBeanName));

			String publicKeyAuthenticatorBeanName = attr.getString("publicKeyAuthenticator");
			if(!StringUtils.isBlank(publicKeyAuthenticatorBeanName))
				beanDefinition.getPropertyValues().add("publicKeyAuthenticator",new RuntimeBeanReference(publicKeyAuthenticatorBeanName));

			registry.registerBeanDefinition(SSH_SERVER_FACTORY_BEAN_NAME, beanDefinition);
		}

		{
			RootBeanDefinition beanDefinition = new RootBeanDefinition(DefaultStandardPromptProvider.class);
			beanDefinition.setSource(null);
			beanDefinition.getPropertyValues().add("prompt",attr.getString("standardPrompt"));
			beanDefinition.getPropertyValues().add("color",attr.getString("standardPromptColor"));

			registry.registerBeanDefinition(STANDARD_PROMPT_PROVIDER_BEAN_NAME,beanDefinition);
		}
	}

}
