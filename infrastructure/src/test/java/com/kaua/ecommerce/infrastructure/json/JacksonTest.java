package com.kaua.ecommerce.infrastructure.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-integration")
@Tag("unitTest")
public class JacksonTest {

    @Test
    void testMarshall() {
        final var aFirstName = "teste";
        final var aLastName = "testes";
        final var aEmail = "teste@teste.com";
        final var aPassword = "12345678Ab";

        final var aDummyJsonTest = new DummyJsonTest(aFirstName, aLastName, aEmail, aPassword);

        final var actualJson = Json.writeValueAsString(aDummyJsonTest);

        Assertions.assertNotNull(actualJson);
    }

    @Test
    void testUnmarshall() {
        final var aFirstName = "teste";
        final var aLastName = "testes";
        final var aEmail = "teste@teste.com";
        final var aPassword = "12345678Ab";

        final var json = """
                {
                    "first_name": "%s",
                    "last_name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(aFirstName, aLastName, aEmail, aPassword);

        final var actualJson = Json.readValue(json, DummyJsonTest.class);

        Assertions.assertEquals(actualJson.firstName(), aFirstName);
        Assertions.assertEquals(actualJson.lastName(), aLastName);
        Assertions.assertEquals(actualJson.email(), aEmail);
        Assertions.assertEquals(actualJson.password(), aPassword);
    }

    @Test
    void testUnmarshallThrowsException() {
        Assertions.assertThrows(RuntimeException.class,
                () -> Json.readValue(null, DummyJsonTest.class));
    }

    public record DummyJsonTest(
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password
    ) {}
}
