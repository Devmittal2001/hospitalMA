package com.medical.testing.MedicalTestingMicro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.medical.testing.MedicalTestingMicro.repo")
@EntityScan(basePackages = "com.medical.testing.MedicalTestingMicro.models")
@EnableTransactionManagement
public class MedicalTestingMicroApplication {
	public static void main(String[] args) {
		SpringApplication.run(MedicalTestingMicroApplication.class, args);
	}
}