/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.consul.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Marcin Biegan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ConsulLifecycleCustomizedTests.MyTestConfig.class,
		webEnvironment = RANDOM_PORT,
		properties = "spring.application.name=testCustomLifecycle")
public class ConsulLifecycleCustomizedTests {

	@Autowired
	private ConsulDiscoveryClient discoveryClient;
	@Autowired
	private ConsulLifecycle lifecycle1;
	@Autowired
	private CustomConsulLifecycle lifecycle2;

	@Test
	public void getInstancesForServiceWorks() {
		List<ServiceInstance> instances = discoveryClient.getInstances("consul");
		assertNotNull("instances was null", instances);
		assertFalse("instances was empty", instances.isEmpty());
	}

	@Test
	public void usesCustomConsulLifecycle() {
		assertEquals("serviceId is not customized", "foo", lifecycle1.getServiceId());
		assertEquals("serviceId is not customized", "foo", lifecycle2.getServiceId());
	}

	@Configuration
	@EnableDiscoveryClient
	@EnableAutoConfiguration
	public static class MyTestConfig {
		@Bean
		public ConsulLifecycle customizedLifecycle(ConsulServiceRegistry serviceRegistry,
				ConsulDiscoveryProperties properties, HeartbeatProperties ttlConfig) {
			return new CustomConsulLifecycle(serviceRegistry, properties, ttlConfig);
		}
	}

	public static class CustomConsulLifecycle extends ConsulLifecycle {
		@Autowired
		public CustomConsulLifecycle(ConsulServiceRegistry serviceRegistry,
				ConsulDiscoveryProperties properties, HeartbeatProperties ttlConfig) {
			super(serviceRegistry, properties, ttlConfig);
		}

		@Override
		public String getServiceId() {
			return "foo";
		}
	}
}
