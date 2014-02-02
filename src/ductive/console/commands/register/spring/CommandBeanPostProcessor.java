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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import ductive.console.commands.register.CommandRegistry;
import ductive.console.commands.register.annotations.Arg;
import ductive.console.commands.register.annotations.Cmd;
import ductive.console.commands.register.annotations.Opt;
import ductive.console.commands.register.model.ArgumentType;
import ductive.console.commands.register.model.CommandType;
import ductive.console.commands.register.model.MethodCommandTarget;
import ductive.console.commands.register.model.OptionType;
import ductive.console.commands.register.model.ParameterType;
import ductive.console.shell.Terminal;

public class CommandBeanPostProcessor implements BeanPostProcessor {

	// defs

	private ParameterType[] EMPTY_PARAMS = new ParameterType[0];

	// deps

	private CommandRegistry commandRegistry;

	// impl

	@Override
	public Object postProcessBeforeInitialization( Object bean,String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean,String beanName) throws BeansException {
		ReflectionUtils.doWithMethods(bean.getClass(),new MethodCallback(){
			@Override public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				Cmd cmd = AnnotationUtils.findAnnotation(method,Cmd.class);
				if( cmd==null )
					return;


				List<ParameterType> params = new ArrayList<>();

				Class<?>[] types = method.getParameterTypes();
				Annotation[][] annotations = method.getParameterAnnotations();

				for(int i=0;i<types.length;++i)
					processArgument(params,method,types[i],annotations[i]);

				commandRegistry.register(new CommandType(
						cmd.path(),
						params.toArray(EMPTY_PARAMS),
						new MethodCommandTarget(bean,method),
						cmd.help()
				));
			}

			private void processArgument(List<ParameterType> params,	Method method, Class<?> type, Annotation[] annotations) {
				if( Terminal.class.isAssignableFrom(type) ) { // will be injected
					return;
				}

				for(int i=0;i<annotations.length;++i) {
					Annotation a = annotations[i];
					if( Arg.class.isInstance(a) ) {
						Arg arg = Arg.class.cast(a);
						params.add(new ArgumentType(arg.value(),arg.optional(),type,arg.parserQualifier(),arg.help()));
						return;
					}
					
					if( Opt.class.isInstance(a) ) {
						Opt opt = Opt.class.cast(a);
						params.add(new OptionType(opt.value(),opt.inverse(),opt.help()));
						return;
					}
				}

				throw new RuntimeException(String.format("method '%s': illegal command argument '%s'",method,type));
			}
		});
		return bean;
	}

	public void setCommandRegistry(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

}
