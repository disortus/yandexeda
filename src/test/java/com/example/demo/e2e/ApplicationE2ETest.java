package com.example.demo.e2e;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entity.User;
import com.example.demo.enums.ApiErrorCode;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ApplicationE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void fullApplicationFlowShouldWorkThroughRealHttpServer() throws Exception {
        HttpResponse<String> openApiResponse = sendGet("/v3/api-docs", null);
        assertThat(openApiResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonNode openApi = objectMapper.readTree(openApiResponse.body());
        assertThat(openApi.at("/info/title").asText()).isEqualTo("Demo Spring Boot API");
        assertThat(openApi.at("/components/securitySchemes/bearerAuth/type").asText()).isEqualTo("http");
        assertThat(openApi.path("paths").has("/status")).isTrue();
        assertThat(openApi.path("paths").has("/auth/register")).isTrue();
        assertThat(openApi.path("paths").has("/auth/login")).isTrue();
        assertThat(openApi.path("paths").has("/users")).isTrue();
        assertThat(openApi.path("paths").has("/users/me")).isTrue();

        HttpResponse<String> statusResponse = sendGet("/status", null);
        assertThat(statusResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(objectMapper.readTree(statusResponse.body()).get("status").asText()).isEqualTo("UP");

        String customerPhone = "7000000005";
        HttpResponse<String> registerCustomerResponse = sendPost("/auth/register", null, """
                {
                  "username": "Customer E2E",
                  "phone": "%s",
                  "password": "Qwerty123!"
                }
                """.formatted(customerPhone));
        assertThat(registerCustomerResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        JsonNode registeredCustomer = objectMapper.readTree(registerCustomerResponse.body());
        String customerToken = registeredCustomer.get("accessToken").asText();
        assertThat(customerToken).isNotBlank();
        assertThat(registeredCustomer.at("/user/phone").asText()).isEqualTo(customerPhone);
        assertThat(registeredCustomer.at("/user/role").asText()).isEqualTo(UserRole.CUSTOMER.name());

        HttpResponse<String> meResponse = sendGet("/users/me", customerToken);
        assertThat(meResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(objectMapper.readTree(meResponse.body()).get("phone").asText()).isEqualTo(customerPhone);

        HttpResponse<String> anonymousUsersResponse = sendGet("/users", null);
        assertThat(anonymousUsersResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(objectMapper.readTree(anonymousUsersResponse.body()).get("code").asText())
                .isEqualTo(ApiErrorCode.AUTHENTICATION_FAILED.name());

        HttpResponse<String> customerUsersResponse = sendGet("/users", customerToken);
        assertThat(customerUsersResponse.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(objectMapper.readTree(customerUsersResponse.body()).get("code").asText())
                .isEqualTo(ApiErrorCode.ACCESS_DENIED.name());

        userRepository.save(new User(
                "Admin E2E",
                "7000000006",
                passwordEncoder.encode("Qwerty123!"),
                UserRole.ADMIN,
                LocalDateTime.now()));

        HttpResponse<String> loginAdminResponse = sendPost("/auth/login", null, """
                {
                  "phone": "7000000006",
                  "password": "Qwerty123!"
                }
                """);
        assertThat(loginAdminResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        String adminToken = objectMapper.readTree(loginAdminResponse.body()).get("accessToken").asText();

        HttpResponse<String> createCourierResponse = sendPost("/users", adminToken, """
                {
                  "username": "Courier E2E",
                  "phone": "7000000007",
                  "password": "Qwerty123!",
                  "role": "COURIER"
                }
                """);
        assertThat(createCourierResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(objectMapper.readTree(createCourierResponse.body()).get("role").asText())
                .isEqualTo(UserRole.COURIER.name());

        HttpResponse<String> adminUsersResponse = sendGet("/users", adminToken);
        assertThat(adminUsersResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonNode users = objectMapper.readTree(adminUsersResponse.body());
        assertThat(users).hasSize(3);

        HttpResponse<String> duplicateRegisterResponse = sendPost("/auth/register", null, """
                {
                  "username": "Duplicate Customer",
                  "phone": "%s",
                  "password": "Qwerty123!"
                }
                """.formatted(customerPhone));
        assertThat(duplicateRegisterResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(objectMapper.readTree(duplicateRegisterResponse.body()).get("code").asText())
                .isEqualTo(ApiErrorCode.RESOURCE_ALREADY_EXISTS.name());

        HttpResponse<String> invalidRegisterResponse = sendPost("/auth/register", null, """
                {
                  "username": "",
                  "phone": "not-a-phone",
                  "password": "123"
                }
                """);
        assertThat(invalidRegisterResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        JsonNode validationError = objectMapper.readTree(invalidRegisterResponse.body());
        assertThat(validationError.get("code").asText()).isEqualTo(ApiErrorCode.REQUEST_VALIDATION.name());
        assertThat(validationError.get("fieldErrors")).isNotEmpty();
    }

    private HttpResponse<String> sendGet(String path, String bearerToken) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path)).GET();
        addCommonHeaders(builder, bearerToken);
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPost(String path, String bearerToken, String jsonBody) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        addCommonHeaders(builder, bearerToken);
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void addCommonHeaders(HttpRequest.Builder builder, String bearerToken) {
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        builder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
