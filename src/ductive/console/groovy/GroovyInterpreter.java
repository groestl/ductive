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
package ductive.console.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.Script;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.MethodClosure;

public class GroovyInterpreter extends GroovyShell {
	
	private static final Log log = LogFactory.getLog(GroovyInterpreter.class);
	
	private volatile int counter = 0;
	
	public GroovyInterpreter(Binding ctx, CompilerConfiguration config) {
		super(ctx,config);
	}

	public Object interpret(String code) {
		Script script = parse(code,generateScriptName());
		
		Binding ctx = getContext();

		MetaClass clazz = ((GroovyObject)script).getMetaClass();
		
		script.setBinding(ctx);
		Object result = script.evaluate(code);
		
		for(MetaMethod m :  clazz.getMethods()) {
			String name = m.getName();
			
			if(!m.getDeclaringClass().getTheClass().equals(clazz.getTheClass()))
				continue;
			
			if(ArrayUtils.contains(ArrayUtils.toArray("main","run"),name) ||
					name.startsWith("super$") ||
					name.startsWith("class$") ||
					name.startsWith("$") ||
					name.startsWith("__$"))
				continue;

			if(log.isTraceEnabled())
				log.trace(String.format("defined function '%s'",m.getName()));

			ctx.setVariable(m.getName(),new MethodClosure(clazz.invokeConstructor(new Object[]{}), m.getName()));
		}
		
		return result;
	}
	
	protected synchronized String generateScriptName() {
		return "Script" + (++counter) + ".groovy";
	}

}
