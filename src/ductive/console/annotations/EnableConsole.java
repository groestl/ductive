package ductive.console.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import ductive.console.config.DefaultConsoleConfig;
import ductive.console.config.SshServerBeanRegistrar;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DefaultConsoleConfig.class,SshServerBeanRegistrar.class})
public @interface EnableConsole {

	String host() default "127.0.0.1";

	String port() default "2200";

	String hostKeyFile() default "hostkey.ser";

	String hostKeyPermissions() default "rw-------";


	// bean references

	String userAuthFactoryProvider() default ""; // no authentication

	String passwordAuthenticator() default "";

	String publicKeyAuthenticator() default "";

}
