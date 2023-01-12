package de.telran.userrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest        // стартует спринг-приложение целиком
@AutoConfigureMockMvc  // для создания MockMVC
// MockMVC не выполняет http запросы
// при старте проекта с аннотацией @AutoConfigureMockMvc веб-сервер не стартует

// @SpringBootTest - стартует спринг-приложение целиком - интеграционное тестирование
// @AutoConfigureMockMvc - стартует спринг-приложение, но не стартует веб-сервер
// @WebMvcTest - стартует только веб-сервер и нужна для тестирования бизнес-логики в контроллерах
// @DataJpaTest - стартует только часть проекта по работе с базой данных

public class RepositoryIntegrationTest {
    // будем вызывать метода контроллера для добавления пользователей
    // проверим, что пользователи добавились в репозиторий
    @Autowired
    UserController controller;

    @Autowired
    UserRepository repository;

    @Autowired
    MockMvc mockMvc;  // механизм для выполнения Http запросов

    @Test
    public void addTwoUsers() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content("{\"name\":\"bob\", \"email\":\"bob@google.com\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content("{\"name\":\"rob\", \"email\":\"rob@google.com\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // в репозитории 2 пользователя
        assertThat(repository.count(), is(2L));

        // имена их bob и rob
        assertThat(
                repository.findAll(), containsInAnyOrder(
                        hasProperty("name", is("bob")),
                        hasProperty("name", is("rob"))
                )
        );

        // напишите проверку e-mail'ов этих пользователей
        assertThat(
                repository.findAll(), containsInAnyOrder(
                        hasProperty("email", is("bob@google.com")),
                        hasProperty("email", is("rob@google.com"))
                )
        );

    }
}