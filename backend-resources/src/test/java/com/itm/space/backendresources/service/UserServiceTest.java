package com.itm.space.backendresources.service;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.fail;


public class UserServiceTest extends BaseIntegrationTest {
    @Autowired // Аннотация, указывающая на автоматическое внедрение зависимости
    private UserServiceImpl userService; // Внедрение UserServiceImpl для тестирования

    @Autowired // Внедрение Keycloak для тестирования интеграции с ним
    private Keycloak keycloak;

    @Test // Объявление тестового метода
    @Order(0) // Указание порядка выполнения теста
    void createUserTest() { // Начало метода тестирования создания пользователя
        UserRequest userRequest = createUserRequest(); // Создание объекта запроса пользователя
        userService.createUser(userRequest); // Вызов метода создания пользователя

        UserRepresentation userRepresentation = keycloak.realm("ITM").users().search("Beast").get(0); // Поиск созданного пользователя в Keycloak

        Assertions.assertEquals(userRepresentation.getUsername(), userRequest.getUsername()); // Проверка, что имя пользователя соответствует ожидаемому
        Assertions.assertEquals(userRepresentation.getEmail(), userRequest.getEmail()); // Проверка, что электронная почта пользователя соответствует ожидаемой
        Assertions.assertEquals(userRepresentation.getFirstName(), userRequest.getFirstName()); // Проверка, что имя пользователя соответствует ожидаемому
        Assertions.assertEquals(userRepresentation.getLastName(), userRequest.getLastName()); // Проверка, что фамилия пользователя соответствует ожидаемой
    }

    @Test // Объявление тестового метода
    @Order(1) // Указание порядка выполнения теста
    void getUserByIdTest() { // Начало метода тестирования получения пользователя по идентификатору
        UserRequest userRequest = createUserRequest(); // Создание объекта запроса пользователя
        userService.createUser(userRequest); // Вызов метода создания пользователя

        String id = keycloak.realm("ITM").users().search("Beast").get(0).getId(); // Получение идентификатора созданного пользователя

        UserResponse userResponse = userService.getUserById(UUID.fromString(id)); // Получение информации о пользователе по идентификатору

        Assertions.assertEquals(userRequest.getFirstName(), userResponse.getFirstName()); // Проверка, что имя пользователя соответствует ожидаемому
        Assertions.assertEquals(userRequest.getLastName(), userResponse.getLastName()); // Проверка, что фамилия пользователя соответствует ожидаемой
        Assertions.assertEquals(userRequest.getEmail(), userResponse.getEmail()); // Проверка, что электронная почта пользователя соответствует ожидаемой
    }

    @AfterEach
        // Объявление метода, который будет выполняться после каждого тестового метода
    void cleanUp() { // Начало метода очистки
        UserRepresentation userRepresentation = keycloak.realm("ITM").users().search("Beast").get(0); // Поиск созданного пользователя в Keycloak
        keycloak.realm("ITM").users().get(userRepresentation.getId()).remove(); // Удаление созданного пользователя
    }

    private UserRequest createUserRequest() { // Метод для создания объекта запроса пользователя
        return new UserRequest("beast", "email@email.com", "qwerty123",
                "User", "Vich"); // Возврат созданного объекта запроса
    }
    private UserRequest createBadUserRequest() {
        return new UserRequest("", "email", "qwerty123",
                "User", "Vich");
    }



}
