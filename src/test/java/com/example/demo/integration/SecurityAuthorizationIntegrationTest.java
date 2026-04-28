package com.example.demo.integration;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@ActiveProfiles("test")
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
    }

    @Test
    void usersEndpointsShouldRespectRoles() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());

        String customerToken = registerAndGetToken("customer@example.com", "Qwerty123!");

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        User admin = new User("Admin", "admin@example.com", passwordEncoder.encode("Qwerty123!"), UserRole.ADMIN,
                LocalDateTime.now());
        userRepository.save(admin);

        String adminToken = loginAndGetToken("admin@example.com", "Qwerty123!");

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        String createPayload = """
                {
                  "name": "Courier User",
                  "email": "courier@example.com",
                  "password": "Qwerty123!",
                  "role": "COURIER"
                }
                """;

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isForbidden());
    }

    private String registerAndGetToken(String email, String password) throws Exception {
        String payload = """
                {
                  "name": "Customer",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        return JsonPath.parse(result.getResponse().getContentAsString()).read("$.accessToken", String.class);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String payload = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.parse(result.getResponse().getContentAsString()).read("$.accessToken", String.class);
    }
}