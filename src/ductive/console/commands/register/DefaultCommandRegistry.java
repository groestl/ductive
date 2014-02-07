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
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Sets;

import ductive.commons.ToString;
import ductive.console.commands.register.model.CommandType;

public class DefaultCommandRegistry implements CommandRegistry {

	private  final CommandNode tree = new CommandNode();

	private Object monitor = new Object();

	public static class CommandNode { // either commands or subnodes
		public final Map<String,CommandType> commands;
		public final Map<String,CommandNode> subnodes;

		public CommandNode() {
			commands = new TreeMap<>();
			subnodes = new TreeMap<>();
		}

		@Override public String toString() { return ToString.me(this); }
	}

	@Override
	public void register(CommandType command) {
		synchronized (monitor) {
			register(command.path,0,tree,command);
		}
	}

	// write

	private void register(String[] path, int step, CommandNode node, CommandType command) {
		Validate.isTrue(path.length>0);
		Validate.isTrue(step<path.length);

		String name = path[step];

		if(step==path.length-1) {
			if(node.subnodes.containsKey(name)) {
				CommandNode n = node.subnodes.get(name);
				HashSet<String> conflicting = Sets.newHashSet(n.commands.keySet());
				conflicting.addAll(n.subnodes.keySet());
				throw new RuntimeException(String.format("error registering command %s: conflicts with subcommand paths %s",toString(command.path),toString(ArrayUtils.subarray(path,0,step+1),conflicting)));
			}

			if(node.commands.containsKey(name))
				throw new RuntimeException(String.format("error registering command %s: already registered",Arrays.toString(command.path)));

			node.commands.put(name,command);
		} else {
			if(node.commands.get(name)!=null)
				throw new RuntimeException(String.format("error registering command %s: conflicts with registered command %s",toString(command.path),toString(ArrayUtils.subarray(path,0,step+1))));

			CommandNode subnode = node.subnodes.get(name);
			if(subnode==null)
				node.subnodes.put(name,subnode=new CommandNode());
			register(path,++step,subnode,command);
		}
	}

	// read

	@Override
	public List<CommandType> commands() {
		synchronized (monitor) {
			List<CommandType> result = new ArrayList<>();
			add(result,tree);
			return result;
		}
	}

	private void add(List<CommandType> result, CommandNode node) {
		for(CommandNode n : node.subnodes.values())
			add(result,n);
		result.addAll(node.commands.values());
	}

	public CommandNode tree() {
		return copy(tree);
	}

	private CommandNode copy(CommandNode tree) {
		synchronized (monitor) {
			CommandNode n = new CommandNode();
			n.commands.putAll(tree.commands);
			Map<String,CommandNode> copy = new HashMap<>();
			for(Entry<String,CommandNode> e : tree.subnodes.entrySet())
				copy.put(e.getKey(),copy(e.getValue()));
			n.subnodes.putAll(copy);
			return n;
		}
	}

	private static String toString(String[] path) {
		return new StringBuilder()
			.append("{")
			.append(StringUtils.join(path,","))
			.append("}")
			.toString();
	}

	private static String toString(String[] path,Iterable<String> conflicting) {
		StringBuilder b = new StringBuilder();
		b.append("{");
		if(path.length>0) {
			b.append(StringUtils.join(path,","));
			b.append(",");
		}
		return b.append("[").append(StringUtils.join(conflicting,',')).append("]").append("}").toString();
	}

	@Override
	public CommandType selectCommand(String[] path) {
		return selectCommand(path,0,tree);
	}

	private CommandType selectCommand(String[] path, int i, CommandNode node) {
		if(node==null)
			return null;

		if(i>=path.length-1)
			return node.commands.get(path[i]);

		return selectCommand(path,i+1,node.subnodes.get(path[i]));
	}

	public List<CommandType> matching(String[] path) {
		return matching(path,0,tree);
	}

	private List<CommandType> matching(String[] path, int i, CommandNode node) {
		if(node==null)
			return null;

		if(i>=path.length-1) {
			List<CommandType> result = new ArrayList<>();
			String match = path[i];
			for(Entry<String,CommandType> e : node.commands.entrySet())
				if(e.getKey().startsWith(match))
					result.add(e.getValue());

			for(Entry<String,CommandNode> e : node.subnodes.entrySet())
				if(e.getKey().startsWith(match))
					add(result,e.getValue());

			return result;
		}

		return matching(path,i+1,node.subnodes.get(path[i]));
	}

}
