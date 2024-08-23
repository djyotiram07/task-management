package com.example.controller;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeAll
    static void start() {
        postgreSQLContainer.start();

        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgreSQLContainer.getJdbcUrl(),
                        postgreSQLContainer.getUsername(),
                        postgreSQLContainer.getPassword())
                .load();
        flyway.migrate();
    }

    @AfterAll
    static void stop() {
        postgreSQLContainer.stop();
    }

    @Test
    void shouldCreateUser() {
        String requestBody = """
                {
                    "id": 1,
                    "username": "jane_doe",
                    "email": "jane.doe@example.com",
                    "password": "myp@ssw",
                    "createdAt": "2024-07-24T11:20:45",
                    "updatedAt": "2024-07-24T11:20:45"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/tm-user")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .header("Location", Matchers.notNullValue());
    }
}
