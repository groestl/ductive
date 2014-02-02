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
package ductive.console.commands.parser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Function;

import ductive.console.commands.parser.model.Argument;
import ductive.console.commands.parser.model.CommandLine;
import ductive.console.commands.parser.model.Option;
import ductive.console.commands.parser.model.Parameter;
import ductive.console.commands.register.ArgParserRegistry;
import ductive.console.commands.register.ArgParserRegistry.ArgParserRegistration;
import ductive.console.commands.register.model.ArgumentType;
import ductive.console.commands.register.model.CommandType;
import ductive.console.commands.register.model.OptionType;
import ductive.console.commands.register.model.ParameterType;
import ductive.parse.Parser;
import ductive.parse.Parsers;
import ductive.parse.Tuple;

public class CmdParserBuilder {
	
	private static final List<Parameter> EMPTY_PARAM_LIST = new ArrayList<Parameter>();
	
	@Autowired private ArgParserRegistry argParserRegistry;
	@Autowired private ArgParserFactory argParserFactory;
	
	
	public Parser<CommandLine> buildParser(List<CommandType> commands) {
		Parser<CommandLine>[] p = null;
		for(CommandType c : commands)
			p = ArrayUtils.add(p,parser(c));

		if(ArrayUtils.isEmpty(p))
			return null;

		return Parsers.or(p);
	}

	private Parser<CommandLine> parser(CommandType c) {
		try {
			if(c.params.length==0)
				return path(c.path).map(new Function<List<String>,CommandLine>() {
					@Override public CommandLine apply(List<String> t) {
						return new CommandLine(t.toArray(ArrayUtils.EMPTY_STRING_ARRAY),EMPTY_PARAM_LIST);
					}
				});
			else
				return Parsers.tuple(path(c.path),args(c.params)).map(new Function<Tuple<List<String>,List<Parameter>>,CommandLine>() {
					@Override public CommandLine apply(Tuple<List<String>,List<Parameter>> t) {
						return new CommandLine(t.a.toArray(ArrayUtils.EMPTY_STRING_ARRAY),t.b);
					}
				});
		} catch(RuntimeException e) {
			throw new RuntimeException(String.format("unable to build parser for command %s",c),e);
		}
	}

	private Parser<List<Parameter>> args(ParameterType[] args) {
		final ArrayList<Parser<? extends Parameter>> p = new ArrayList<Parser<? extends Parameter>>();
		for(ParameterType arg : args)
			p.add(arg.visit(new ParameterType.Visitor<Parser<? extends Parameter>>() {
				@Override public Parser<? extends Parameter> accept(ArgumentType arg) { return arg(arg);	}
				@Override public Parser<? extends Parameter> accept(OptionType opt) { return opt(opt); }
			}));

		return Parsers.or(p).many();
	}

	private Parser<Option> opt(OptionType opt) {
		return token(Parsers.string("--").precedes(Parsers.string(opt.name))).map(new Function<String,Option>() {
			@Override public Option apply(@Nullable String name) {
				return new Option(name);
			}
		});
	}

	private Parser<Argument> arg(ArgumentType arg) {
		return Parsers.tuple(
			token(Parsers.string("--").precedes(Parsers.string(arg.name))),
			token(argValueParser(arg))
			).map(new Function<Tuple<String,?>,Argument>() {
				@Override public Argument apply(Tuple<String,?> t) {
					return new Argument(t.a,t.b);
				}
			});
	}

	
	private Parser<?> argValueParser(ArgumentType arg) {
		ArgParserRegistration registration = argParserRegistry.find(arg.type,StringUtils.defaultIfEmpty(arg.parserQualifier,null));
		if(registration!=null)
			return argParserFactory.create(registration);
		if(String.class.isAssignableFrom(arg.type))
			return Parsers.NON_WHITESPACE;
		throw new UnsupportedOperationException(String.format("cannot build argument parser for argument %s",arg));
	}

	private static Parser<List<String>> path(String... path) {
		List<Parser<String>> parts = new ArrayList<>();
		for(String p : path)
			parts.add(token(p));
		return Parsers.list(parts);
	}

	private static Parser<String> token(String recognize) {
		return token(Parsers.string(recognize));
	}

	private static <T> Parser<T> token(Parser<T> p) {
		Parser<Object> tokenTerminator = Parsers.isCompletingSwitch(
				Parsers.or(Parsers.WHITESPACE,Parsers.EOF),
				Parsers.WHITESPACE
		);

		return p.followedBy(tokenTerminator).token();
		/*Parsers.or(
			//Parsers.singleQuoted(p).followedBy(tokenTerminator).token(),
			//Parsers.doubleQuoted(p).followedBy(tokenTerminator).token(),

		);*/

	}

	
	public void setArgParserRegistry(ArgParserRegistry argParserRegistry) {
		this.argParserRegistry = argParserRegistry;
	}

	

}
