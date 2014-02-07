package ductive.console.commands.register;

import java.lang.reflect.Method;

import ductive.console.commands.register.DefaultArgParserRegistry.ArgParserRegistration;

public interface ArgParserRegistry {

	void register(Class<?> type, String qualifier, Object bean, Method method);

	ArgParserRegistration find(Class<?> type, String qualifier);

}
