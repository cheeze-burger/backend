package com.cheezeburger.cheezegame;

import com.cheezeburger.cheezegame.global.config.properties.AppProperties;
import com.cheezeburger.cheezegame.global.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableConfigurationProperties({
		CorsProperties.class,
		AppProperties.class
})
@EnableJpaAuditing
@SpringBootApplication
public class CheezeGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheezeGameApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
