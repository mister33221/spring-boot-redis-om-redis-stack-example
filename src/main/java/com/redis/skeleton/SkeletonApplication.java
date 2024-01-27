package com.redis.skeleton;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This annotation will specify the package to scan for redis-om documents (including the repositories)
@EnableRedisDocumentRepositories(basePackages = "com.redis.skeleton")
@SpringBootApplication
public class SkeletonApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkeletonApplication.class, args);
	}

}
