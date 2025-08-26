package com.example.audit_listener;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.time.Duration;

public abstract class AbstractContainer {

    private static final ElasticsearchContainer container;

    static {
        container = new ElasticsearchContainer("elasticsearch:8.17.10")
                        .withExposedPorts(9200)
                        .withEnv("xpack.security.enabled", "false")
                        .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
                        .withStartupTimeout(Duration.ofSeconds(30))
                        .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                                new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(9200), new ExposedPort(9200)))
                        ));
        container.start();
        updateProperties();
    }

    private static void updateProperties() {
        System.setProperty("spring.kafka.bootstrap-servers", "localhost:9094");
        System.setProperty("audit-listener.kafka.consumer.group-id", "test_group");
        System.setProperty("audit-listener.kafka.topic-name", "test_topic");
        System.setProperty("spring.elasticsearch.uris", container.getHttpHostAddress());
        System.setProperty("audit-listener.kafka.method-topic-name", "audit.methods.test");
        System.setProperty("audit-listener.kafka.request-topic-name", "audit.requests.test");
        System.setProperty("audit-listener.kafka.error-topic-name", "audit.errors.test");
        System.setProperty("audit-listener.kafka.partition-num", "1");
        System.setProperty("audit-listener.kafka.replication_factor", "1");
        System.setProperty("audit-listener.index.method", "method_index");
        System.setProperty("audit-listener.index.request", "request_index");
    }

}
