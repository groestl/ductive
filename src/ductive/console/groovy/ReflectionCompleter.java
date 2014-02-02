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

import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.MetaMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.console.completer.Completer;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Implements the Completor interface to provide competions for
 * GroovyShell by using reflection on global variables.
 *
 * @author <a href="mailto:probabilitytrees@gmail.com">Marty Saxton</a>
 * @author dirty port to java
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ReflectionCompleter implements Completer {

    private GroovyShell shell;

    public ReflectionCompleter(GroovyShell shell) {
        this.shell = shell;
    }
    
	@Override
    public int complete(String buffer, int cursor, List candidates) {

        int identifierStart = findIdentifierStart(buffer, cursor);
        String identifierPrefix = identifierStart != -1 ? buffer.substring(identifierStart, cursor) : "";
        int lastDot = buffer.lastIndexOf('.');

        // if there are no dots, and there is a valid identifier prefix
        if (lastDot == -1 ) {
            if (identifierStart != -1) {
                List myCandidates = findMatchingVariables(identifierPrefix);
                if (myCandidates.size() > 0) {
                    candidates.addAll(myCandidates);
                    return identifierStart;
                }
            }
        }
        else {
            // there are 1 or more dots
            // if ends in a dot, or if there is a valid identifier prefix
            if (lastDot == cursor-1 || identifierStart != -1){
                // evaluate the part before the dot to get an instance
                String instanceRefExpression = buffer.substring(0, lastDot);
                try {
	                Object instance = shell.evaluate(instanceRefExpression);
	                if (instance != null) {
	                    // look for public methods/fields that match the prefix
	                    List myCandidates = getPublicFieldsAndMethods(instance, identifierPrefix);
	                    if (myCandidates.size() > 0) {
	                        candidates.addAll(myCandidates);
	                        return lastDot+1;
	                    }
	                }
                } catch(GroovyRuntimeException e) { /* noop */ }
            }
        }

        // no candidates
        return -1;
    }

    /**
     * Parse a buffer to determine the start index of the groovy identifier
     * @param buffer the buffer to parse
     * @param endingAt the end index with the buffer
     * @return the start index of the identifier, or -1 if the buffer
     * does not contain a valid identifier that ends at endingAt
     */
    int findIdentifierStart(String buffer, int endingAt) {
        // if the string is empty then there is no expression
        if (endingAt == 0)
            return -1;
        // if the last character is not valid then there is no expression
        char lastChar = buffer.charAt(endingAt-1);
        if (!Character.isJavaIdentifierPart(lastChar))
            return -1;
        // scan backwards until the beginning of the expression is found
        int startIndex = endingAt-1;
        while (startIndex > 0 && Character.isJavaIdentifierPart(buffer.charAt(startIndex-1)))
            --startIndex;
        return startIndex;
    }


    /**
     * Build a list of public fields and methods for an object
     * that match a given prefix.
     * @param instance the object
     * @param prefix the prefix that must be matched
     * @return the list of public methods and fields that begin with the prefix
     */
    List getPublicFieldsAndMethods(Object instance, String prefix) {
        SortedSet<String> rv = new TreeSet<>();
        
        for(Field f : instance.getClass().getFields())
            if (f.getName().startsWith(prefix))
                 rv.add(f.getName());
    
	    for(Method m : instance.getClass().getMethods())
	        if (m.getName().startsWith(prefix)) {
	        		if(m.getParameterTypes().length>0)
	        			rv.add(String.format("%s(",m.getName()));
	        		else
	        			rv.add(String.format("%s()",m.getName()));
	        }
	    
   		for(MetaMethod m : InvokerHelper.getMetaClass(instance).getMetaMethods())
	        if (m.getName().startsWith(prefix)) {
        		if(m.getParameterTypes().length>0)
        			rv.add(String.format("%s(",m.getName()));
        		else
        			rv.add(String.format("%s()",m.getName()));
        }

        return new ArrayList<>(rv);
    }

    /**
     * Build a list of variables defined in the shell that
     * match a given prefix.
     * @param prefix the prefix to match
     * @return the list of variables that match the prefix
     */
    List findMatchingVariables(String prefix) {
        SortedSet<String> s = new TreeSet<>();
        for (Object varName : shell.getContext().getVariables().keySet())
            if (varName.toString().startsWith(prefix))
                s.add(varName.toString());
        return new ArrayList<>(s);
    }
}
