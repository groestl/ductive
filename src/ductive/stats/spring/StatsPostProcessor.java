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
package ductive.stats.spring;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import ductive.stats.StatsProviderRegistry;
import ductive.stats.annotations.Stats;

public class StatsPostProcessor implements BeanPostProcessor {

	// deps
	
	private String[] prefix;

	@Autowired private StatsProviderRegistry statsProviderRegistry;

	// impl

	@Override
	public Object postProcessBeforeInitialization( Object bean,String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean,String beanName) throws BeansException {
		ReflectionUtils.doWithMethods(bean.getClass(),new MethodCallback(){
			@Override public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				Stats cmd = AnnotationUtils.findAnnotation(method,Stats.class);
				if( cmd==null )
					return;


				String[] path = ArrayUtils.addAll(prefix,cmd.value());
				statsProviderRegistry.register(path,bean,method);
			}
		});
		return bean;
	}

	public void setStatsProviderRegistry(StatsProviderRegistry statsProviderRegistry) {
		this.statsProviderRegistry = statsProviderRegistry;
	}
	
	public void setPrefix(String... prefix) {
		this.prefix = prefix;
	}

}
