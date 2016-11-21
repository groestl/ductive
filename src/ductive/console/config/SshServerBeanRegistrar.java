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

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableConsole.class.getName()));

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

}
