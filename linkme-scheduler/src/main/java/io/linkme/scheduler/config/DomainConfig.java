package io.linkme.scheduler.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("io.linkme.scheduler.domain")
@EnableJpaRepositories("io.linkme.scheduler.repos")
@EnableTransactionManagement
public class DomainConfig {
}
