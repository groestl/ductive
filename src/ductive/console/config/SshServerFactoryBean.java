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
package ductive.console.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.UserAuthNoneFactory;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.springframework.beans.factory.FactoryBean;

import com.google.common.collect.Sets;

public class SshServerFactoryBean implements FactoryBean<SshServer> {

	private String host;

	private Integer port;

	private String hostKeyFile;

	private String hostKeyPermissions;

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

		DuctiveSimpleGeneratorHostKeyProvider kp = new DuctiveSimpleGeneratorHostKeyProvider(Paths.get(hostKeyFile));
		kp.setAlgorithm("rsa");
		sshd.setKeyPairProvider(kp);

		if(userAuthFactoryProvider==null) {
			List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
			userAuthFactories.add(new UserAuthNoneFactory());
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

	public void setHostKeyPermissions(String hostKeyPermissions) {
		this.hostKeyPermissions = hostKeyPermissions;
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

	public class DuctiveSimpleGeneratorHostKeyProvider extends SimpleGeneratorHostKeyProvider {
		public DuctiveSimpleGeneratorHostKeyProvider(Path path) {
			super(path);
		}
		@Override
		protected KeyPair readKeyPair(Path keyPath, OpenOption... options) throws IOException, GeneralSecurityException {
			try {
				Files.setPosixFilePermissions(keyPath,perms()); // make sure permissions are set correctly (even if file was not created just now)
			} catch(IOException e) { /* noop */ }
			return super.readKeyPair(keyPath, options);
		}
		@Override
		protected void writeKeyPair(KeyPair kp, Path keyPath, OpenOption... options) throws IOException, GeneralSecurityException {
			if ((!Files.exists(keyPath)) || isOverwriteAllowed()) {
				try(SeekableByteChannel c = Files.newByteChannel(keyPath,Sets.newHashSet(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE),PosixFilePermissions.asFileAttribute(perms()))) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					doWriteKeyPair(keyPath.toString(), kp, os);
					c.write(ByteBuffer.wrap(os.toByteArray()));
				} catch (Throwable e) {
					log.warn("writeKeyPair({}) failed ({}) to write key {}: {}", keyPath, e.getClass().getSimpleName(), e.getMessage());
					if (log.isDebugEnabled()) {
						log.debug("writeKeyPair(" + keyPath + ") write failure details", e);
					}
				}
			} else {
				log.error("Overwriting key ({}) is disabled: using throwaway {}: {}", keyPath, KeyUtils.getKeyType(kp), KeyUtils.getFingerPrint((kp == null) ? null : kp.getPublic()));
			}
		}
	}

	public Set<PosixFilePermission> perms() {
		return PosixFilePermissions.fromString(hostKeyPermissions);
	}

}