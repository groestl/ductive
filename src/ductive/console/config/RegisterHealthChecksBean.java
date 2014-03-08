package ductive.console.config;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

public class RegisterHealthChecksBean {
	
	private static final Logger log = LoggerFactory.getLogger(RegisterHealthChecksBean.class);
	
	@Autowired(required=false) private Map<String,? extends HealthCheck> healthChecks;
	@Autowired private HealthCheckRegistry healthCheckRegistry;
	
	@PostConstruct
	public void init() {
		if(MapUtils.isEmpty(healthChecks))
			return;
		
		for(Entry<String,? extends HealthCheck> e : healthChecks.entrySet()) {
			if(log.isDebugEnabled())
				log.debug(String.format("registering health check '%s'",e.getKey()));
			
			healthCheckRegistry.register(e.getKey(),e.getValue());
		}
	}

}
