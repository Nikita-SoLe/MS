package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // Использование MockitoExtension для управления мок-объектами
@WithMockUser(roles = "MODERATOR") // Создание фиктивного пользователя с ролью "MODERATOR" для тестов
public class UserControllerTest extends BaseIntegrationTest {

    @MockBean // Создание мок-объекта UserService для инъекции в контроллер
    private UserService userService;

    @Test // Объявление тестового метода
    void createTest() throws Exception { // Начало метода тестирования создания пользователя
        UserRequest userRequest = createUserRequest(); // Создание объекта запроса пользователя
        MockHttpServletRequestBuilder request = requestWithContent(post("/api/users"), userRequest); // Создание HTTP-запроса для отправки запроса пользователя

        Mockito.doNothing().when(userService).createUser(any(UserRequest.class)); // Настройка мок-объекта UserService для игнорирования вызова метода createUser
        mvc.perform(request) // Выполнение HTTP-запроса
                .andExpect(status().isOk()); // Проверка ожидаемого статуса ответа
    }

    @Test
    void createNegativeTest() throws Exception {
        UserRequest userRequest = createBadUserRequest();
        MockHttpServletRequestBuilder request = requestWithContent(post("/api/users"), userRequest);

        Mockito.doNothing().when(userService).createUser(any(UserRequest.class));
        mvc.perform(request)
             .andExpect(status().isBadRequest());
    }

    @Test // Объявление тестового метода
    void getUserByIdTest() throws Exception { // Начало метода тестирования получения пользователя по идентификатору
        UserResponse userResponse = createUserResponse(); // Создание объекта ответа пользователя
        UUID id = UUID.randomUUID(); // Создание случайного идентификатора

        Mockito.when(userService.getUserById(id)).thenReturn(userResponse); // Настройка мок-объекта UserService для возврата заданного ответа при вызове метода getUserById с указанным идентификатором
        mvc.perform(get("/api/users/{id}", id.toString())) // Выполнение HTTP-запроса на получение пользователя по идентификатору
                .andExpect(status().isOk()) // Проверка ожидаемого статуса ответа
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@email.com")) // Проверка ожидаемого значения поля email в JSON-ответе
                .andReturn(); // Возврат результата выполнения запроса
    }

    @Test // Объявление тестового метода
    void helloTest() throws Exception { // Начало метода тестирования приветствия
        mvc.perform(get("/api/users/hello") // Выполнение HTTP-запроса на получение приветствия
                        .contentType(MediaType.APPLICATION_JSON)) // Установка типа контента запроса
                .andExpect(status().isOk()); // Проверка ожидаемого статуса ответа
    }
    @Test
    @WithMockUser(roles = "USER")
    void helloNegativeTest() throws Exception {
        mvc.perform(get("/api/users/hello")
                .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().is4xxClientError());
    }

    private UserRequest createUserRequest() { // Метод для создания объекта запроса пользователя
        return new UserRequest("beast", "email@email.com", "qwerty123",
                "User", "Vich"); // Возврат нового объекта запроса
    }

    private UserRequest createBadUserRequest() {
        return new UserRequest("bad", "bad@n.com", "1", "User", "V");
    }

    private UserResponse createUserResponse() { // Метод для создания объекта ответа пользователя
        return new UserResponse("User", "Vich", "email@email.com",
                List.of("ROLE_USER"), List.of("users")); // Возврат нового объекта ответа
    }
}