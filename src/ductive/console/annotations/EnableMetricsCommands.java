package ductive.console.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import ductive.console.config.DefaultArgParsersConfig;
import ductive.console.config.DefaultMetricsCommandsConfig;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DefaultArgParsersConfig.class,DefaultMetricsCommandsConfig.class})
public @interface EnableMetricsCommands {

}
