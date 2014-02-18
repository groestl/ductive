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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Throwables;

import ductive.console.commands.parser.model.Argument;
import ductive.console.commands.parser.model.CommandLine;
import ductive.console.commands.parser.model.Option;
import ductive.console.commands.parser.model.Parameter;
import ductive.console.commands.register.model.ArgumentType;
import ductive.console.commands.register.model.CallbackCommandTarget;
import ductive.console.commands.register.model.CommandTarget;
import ductive.console.commands.register.model.CommandType;
import ductive.console.commands.register.model.MethodCommandTarget;
import ductive.console.commands.register.model.OptionType;
import ductive.console.commands.register.model.ParameterType;
import ductive.console.shell.Terminal;

public class CommandInvoker {

	@Autowired private CommandRegistry commandRegistry;
	@Autowired private ConversionService conversionService;


	public Object execute(final CommandContext ctx, final CommandLine line) {
		final CommandType command = commandRegistry.selectCommand(line.path);
		if(command==null)
			throw new RuntimeException(String.format("command %s not found",Arrays.toString(line.path)));

		return command.target.visit(new CommandTarget.Visitor<Object>() {
			@Override public Object accept(CallbackCommandTarget t) { return execute(ctx,t,command.params,line.params); }
			@Override public Object accept(MethodCommandTarget t) { return execute(ctx,t,command.params,line.params); }
		});

	}


	protected Object execute(CommandContext ctx, CallbackCommandTarget target, ParameterType[] paramTypes, List<Parameter> params) {
		target.execute();
		return null;
	}


	protected Object execute(CommandContext ctx, MethodCommandTarget target, ParameterType[] paramTypes, List<Parameter> params) {
		try {
			Preprocessed pp = preprocess(params);

			Class<?>[] parameters = target.method.getParameterTypes();
			Object[] args = new Object[parameters.length];
			int aidx=0;
			for(int i=0;i<parameters.length;++i) {
				Class<?> param = parameters[i];

				if(Terminal.class.isAssignableFrom(param)) {
					if(!param.isInstance(ctx.terminal))
						throw new RuntimeException(String.format("method '%s' does not support terminal '%s'",target.method,ctx.terminal));

					args[i] = ctx.terminal;
					continue;
				}

				ParameterType t = paramTypes[aidx++];
				args[i] = handleParam(pp,t);
			}

			Validate.isTrue(aidx==paramTypes.length);

			return ReflectionUtils.invokeMethod(target.method,target.bean,args);
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}
	}


	private Object handleParam(final Preprocessed pp, ParameterType t) {
		return t.visit(new ParameterType.Visitor<Object>() {
			@Override public Object accept(ArgumentType arg) { return handleArgument(pp,arg); }
			@Override public Object accept(OptionType opt) { return handleOption(pp,opt); }
		});
	}

	protected Object handleArgument(Preprocessed pp, ArgumentType t) {
		List<Object> v_ = pp.argumentValues.get(t.name);

		if( v_==null && !t.optional)
			throw new RuntimeException(String.format("argument '%s' is not optional",t.name));

		if( v_==null && t.optional)
			return null;

		if(!t.type.isArray()) {
			if( v_.size()>1 )
				throw new RuntimeException(String.format("too many occurrences of argument '%s': is not an array",t.name));

			return conversionService.convert(v_.get(0),t.type);
		}

		return conversionService.convert(v_.toArray(ArrayUtils.EMPTY_OBJECT_ARRAY),t.type);
	}

	protected Object handleOption(Preprocessed pp, OptionType opt) {
		return pp.options.contains(opt.name) ^ opt.inverse;
	}

	private Preprocessed preprocess(List<Parameter> params) {
		final Map<String,List<Object>> argumentValues = new HashMap<>();
		final Set<String> options = new HashSet<String>();

		for(Parameter a : params) {
			a.visit(new Parameter.Visitor<Void>() {
				@Override public Void accept(Argument a) {
					List<Object> v_ = argumentValues.get(a.name);
					if(v_==null)
						argumentValues.put(a.name,v_=new ArrayList<>());
					v_.add(a.value);
					return null;
				}
				@Override public Void accept(Option option) {
					options.add(option.name);
					return null;
				}
			});
		}

		return new Preprocessed(argumentValues,options);
	}

	private static class Preprocessed {
		Map<String,List<Object>> argumentValues;
		Set<String> options;

		public Preprocessed(Map<String, List<Object>> argumentValues, Set<String> options) {
			this.argumentValues = argumentValues;
			this.options = options;
		}
	}


	public void setCommandRegistry(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
