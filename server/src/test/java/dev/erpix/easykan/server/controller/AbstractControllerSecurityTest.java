package dev.erpix.easykan.server.controller;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractControllerSecurityTest {

    @Autowired
    protected MockMvc mockMvc;

    protected abstract Stream<Arguments> provideProtectedEndpoints();

    @ParameterizedTest
    @MethodSource("provideProtectedEndpoints")
    void protectedEndpoints_shouldReturnUnauthorized_whenUserIsNotAuthenticated(
            String httpMethod, String endpointPath) throws Exception {

        var requestBuilder = MockMvcRequestBuilders.request(HttpMethod.valueOf(httpMethod), endpointPath);

        if (httpMethod.equals("POST") || httpMethod.equals("PUT") || httpMethod.equals("PATCH")) {
            requestBuilder
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());
    }

}
