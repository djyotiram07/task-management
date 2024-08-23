package com.example;

import org.springframework.boot.SpringApplication;

public class TestCommentsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(CommentsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
