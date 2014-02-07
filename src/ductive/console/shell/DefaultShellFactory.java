package ductive.console.shell;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;

public class DefaultShellFactory implements Factory<Command> {

	private Shell shell;

	@Override public Command create() {
		return new EmbeddedShellRunner(shell);
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

}