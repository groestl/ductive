package ductive.console.commands.register;

import java.util.List;

import ductive.console.commands.register.model.CommandType;

public interface CommandRegistry {

	void register(CommandType command);

	List<CommandType> commands();

	CommandType selectCommand(String[] path);

}
