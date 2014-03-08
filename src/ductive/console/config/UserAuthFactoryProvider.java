package ductive.console.config;

import java.util.List;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.UserAuth;

public interface UserAuthFactoryProvider {

	List<NamedFactory<UserAuth>> get();

}
