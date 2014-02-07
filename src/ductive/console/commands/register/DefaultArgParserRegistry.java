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
package ductive.console.commands.register;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ductive.commons.Equals;
import ductive.commons.Equals.EqualsDefinition;

public class DefaultArgParserRegistry implements ArgParserRegistry {

	// state

	private ConcurrentMap<RegistryKey,ArgParserRegistration> data = new ConcurrentHashMap<>();

	// api

	@Override
	public void register(Class<?> type, String qualifier, Object bean, Method method) {
		Validate.isTrue(null==data.putIfAbsent(new RegistryKey(type,qualifier),new ArgParserRegistration(bean,method)),String.format("unable to register ArgParser(type=%s,qualifier=%s): already registered",type,qualifier));
	}

	@Override
	public ArgParserRegistration find(Class<?> type_, String qualifier) {
		Class<?> type = ClassUtils.primitiveToWrapper(type_);
		return data.get(new RegistryKey(type,qualifier));
	}

	// structs

	public static class ArgParserRegistration {

		public final Object bean;
		public final Method method;

		public ArgParserRegistration(Object bean, Method method) {
			this.bean = bean;
			this.method = method;
		}

	}

	// pimpl

	private static class RegistryKey {

		final Class<?> type;
		final String qualifier;

		public RegistryKey(Class<?> type, String qualifier) {
			this.type = type;
			this.qualifier = qualifier;
		}

		@Override public boolean equals(Object other) {
			return Equals.eq(this,other,new EqualsDefinition<RegistryKey>() {
				@Override public void check(RegistryKey l, RegistryKey r, EqualsBuilder b) {
					b.append(l.type,r.type);
					b.append(l.qualifier,r.qualifier);
				}
			});
		}

		@Override public int hashCode() {
			return new HashCodeBuilder()
				.append(type)
				.append(qualifier)
				.toHashCode();
		}

	}

}
