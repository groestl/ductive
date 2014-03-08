package ductive.console.shell;

import java.util.Map;

public interface GroovyShellContextProvider {

	Map<String,Object> context(TerminalUser terminalUser);

}
