package com.example.audit_listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories
public class AuditListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditListenerApplication.class, args);
	}

}
