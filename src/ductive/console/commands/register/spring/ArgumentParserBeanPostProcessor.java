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
package ductive.console.commands.register.spring;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import ductive.console.commands.register.ArgParserRegistry;
import ductive.console.commands.register.annotations.ArgParser;
import ductive.parse.Parser;


public class ArgumentParserBeanPostProcessor implements BeanPostProcessor {

	// deps

	private ArgParserRegistry argParserRegistry;

	// impl

	@Override
	public Object postProcessBeforeInitialization( Object bean,String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean,String beanName) throws BeansException {
		ReflectionUtils.doWithMethods(bean.getClass(),new MethodCallback(){
			@Override public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ArgParser argParser = AnnotationUtils.findAnnotation(method,ArgParser.class);
				if( argParser==null )
					return;

				String qualifier = StringUtils.defaultIfEmpty(argParser.value(),null);

				if( !void.class.equals(argParser.bind()) ) {
					argParserRegistry.register(argParser.bind(),qualifier,bean,method);
					return;
				}

				Type genericReturnType = method.getGenericReturnType();
				Validate.isTrue(ParameterizedType.class.isInstance(genericReturnType),String.format("method %s: return type of parser generator method must be a subclass of %s<?>",method,Parser.class.getCanonicalName()));

				ParameterizedType pt = ParameterizedType.class.cast(genericReturnType);
				Validate.isTrue(Parser.class.isAssignableFrom((Class<?>)pt.getRawType()),String.format("method %s: return type of parser generator method must be a subclass of %s<?>",method,Parser.class.getCanonicalName()));

				Type[] genericParams = pt.getActualTypeArguments();
				Validate.isTrue(genericParams.length==1);

				argParserRegistry.register((Class<?>)genericParams[0],qualifier,bean,method);
			}

		});
		return bean;
	}

	public void setArgParserRegistry(ArgParserRegistry argParserRegistry) {
		this.argParserRegistry = argParserRegistry;
	}

}
