package com.example;

import org.springframework.boot.SpringApplication;

public class TestKanbanServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(KanbanServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
