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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sshd.common.util.Base64;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;
import org.apache.sshd.server.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.google.common.collect.Sets;

import ductive.console.annotations.EnableConsoleFiles;
import ductive.console.config.DefaultConsoleConfig.DuctiveConsole;

public class EnableConsoleFilesRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableConsoleFiles.class.getName()));

		//AnnotationAttributes files = AnnotationAttributes.class.cast(attr.get());
		AnnotationAttributes[] files = attr.getAnnotationArray("value");

		for(AnnotationAttributes f : files) {
			RootBeanDefinition beanDefinition = new RootBeanDefinition(ConsoleFileWriter.class);
			beanDefinition.getPropertyValues().add("path",f.getString("path"));
			beanDefinition.getPropertyValues().add("template",f.getString("template"));
			beanDefinition.getPropertyValues().add("permissions",f.getString("permissions"));
			beanDefinition.getPropertyValues().add("varPrefix",f.getString("varPrefix"));
			beanDefinition.getPropertyValues().add("varPostfix",f.getString("varPostfix"));
			beanDefinition.getPropertyValues().add("enabled",f.getString("enabled"));
			registry.registerBeanDefinition(BeanDefinitionReaderUtils.generateBeanName(beanDefinition,registry),beanDefinition);
		}
	}

	public static class ConsoleFileWriter {

		// deps

		@Autowired private DuctiveConsole target;

		private String path = null;
		private String template = null;
		private String permissions = "rw-r-----";
		private String varPrefix = "{{";
		private String varPostfix = "}}";
		private boolean enabled=true;

		@PostConstruct
		public void init() throws IOException {
			if(!enabled)
				return;
			checkNotNull(path);
			checkNotNull(template);

			SshServer sshServer = target.sshd();

			int port = sshServer.getPort();
			Iterable<KeyPair> keys = sshServer.getKeyPairProvider().loadKeys();


			StringBuffer knownHosts = new StringBuffer();
			for(KeyPair k : keys) {
				PublicKey pub = k.getPublic();
				Buffer buffer = new ByteArrayBuffer();
				buffer.putRawPublicKey(pub);
				knownHosts.append(String.format("[localhost]:%s ssh-rsa %s\n",sshServer.getPort(),Base64.encodeToString(buffer.getCompactData())));
			}

			Map<String,String> model = new HashMap<>();
			model.put("port",Integer.toString(port));
			model.put("known_hosts",knownHosts.toString());

			StrSubstitutor s = new StrSubstitutor(model,varPrefix,varPostfix);
			String content = s.replace(template);
			write(Paths.get(path),content,PosixFilePermissions.fromString(permissions));
		}

		private void write(Path path, String content, Set<PosixFilePermission> perms) throws IOException {
			try(SeekableByteChannel c = Files.newByteChannel(path,Sets.newHashSet(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE),PosixFilePermissions.asFileAttribute(perms))) {
				Files.setPosixFilePermissions(path,perms); // make sure permissions are set correctly (even if file was not created just now)
				c.write(ByteBuffer.wrap(content.getBytes()));
			}
		}

		public void setPath(String path) {
			this.path = path;
		}

		public void setPermissions(String permissions) {
			this.permissions = permissions;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public void setVarPrefix(String varPrefix) {
			this.varPrefix = varPrefix;
		}

		public void setVarPostfix(String varPostfix) {
			this.varPostfix = varPostfix;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}


}

