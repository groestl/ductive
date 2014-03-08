package ductive.console.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthNone;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.springframework.beans.factory.FactoryBean;

public class SshServerFactoryBean implements FactoryBean<SshServer> {

		private String host;

		private Integer port;

		private String hostKeyFile;

		private UserAuthFactoryProvider userAuthFactoryProvider; // = new ArrayList<>();

		private PasswordAuthenticator passwordAuthenticator;

		private PublickeyAuthenticator publicKeyAuthenticator;

		@PostConstruct
		public void init() {
			Validate.notNull(host, String.format("property 'host' must be set"));
			Validate.notNull(port, String.format("property 'port' must be set"));
			Validate.notNull(hostKeyFile, String.format("property 'hostKeyFile' must be set"));
		}

		@Override
		public SshServer getObject() throws Exception {
			SshServer sshd = SshServer.setUpDefaultServer();
			sshd.setHost(host);
			sshd.setPort(port);
			sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostKeyFile));

			if(userAuthFactoryProvider==null) {
    			List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
    			userAuthFactories.add(new UserAuthNone.Factory());
				sshd.setUserAuthFactories(userAuthFactories);
			} else
				sshd.setUserAuthFactories(new ArrayList<>(userAuthFactoryProvider.get()));

			if(passwordAuthenticator!=null)
				sshd.setPasswordAuthenticator(passwordAuthenticator);

			if(publicKeyAuthenticator!=null)
				sshd.setPublickeyAuthenticator(publicKeyAuthenticator);

			return sshd;
		}

		@Override
		public Class<?> getObjectType() {
			return SshServer.class;
		}

		@Override
		public boolean isSingleton() {
			return false;
		}

		// booring

		public void setHostKeyFile(String hostKeyFile) {
			this.hostKeyFile = hostKeyFile;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public void setUserAuthFactoryProvider(UserAuthFactoryProvider userAuthFactoryProvider) {
			this.userAuthFactoryProvider = userAuthFactoryProvider;
		}

		public void setPasswordAuthenticator(PasswordAuthenticator passwordAuthenticator) {
			this.passwordAuthenticator = passwordAuthenticator;
		}

		public void setPublicKeyAuthenticator(PublickeyAuthenticator publicKeyAuthenticator) {
			this.publicKeyAuthenticator = publicKeyAuthenticator;
		}

	}