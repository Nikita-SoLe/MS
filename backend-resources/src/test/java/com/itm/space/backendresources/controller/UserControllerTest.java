package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(roles = "MODERATOR")
public class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private Keycloak keycloak;

    @Test
    @Order(0)
    void createTest() throws Exception {
        UserRequest userRequest = createUserRequest();
        MockHttpServletRequestBuilder request = requestWithContent(post("/api/users"), userRequest);
        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void createNegativeTest() throws Exception {
        UserRequest userRequest = createBadUserRequest();
        MockHttpServletRequestBuilder request = requestWithContent(post("/api/users"), userRequest);
        mvc.perform(request)
             .andExpect(status().isBadRequest());
    }

    @Test
    @Order(1)
    void getUserByIdTest() throws Exception {
        String id = keycloak.realm("ITM").users().search("Beast").get(0).getId();
        mvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Vich"));
    }

    @Test
    void helloTest() throws Exception {
        mvc.perform(get("/api/users/hello")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "USER")
    void helloNegativeTest() throws Exception {
        mvc.perform(get("/api/users/hello")
                .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().is4xxClientError());
    }

    @AfterAll
    void cleanUp() {
        UserRepresentation userRepresentation = keycloak.realm("ITM").users().search("Beast").get(0);
        keycloak.realm("ITM").users().get(userRepresentation.getId()).remove();
    }

    private UserRequest createUserRequest() {
        return new UserRequest("Beast", "email@email.com", "qwerty123",
                "User", "Vich");
    }

    private UserRequest createBadUserRequest() {
        return new UserRequest("bad", "bad@n.com", "1", "User", "V");
    }

}