package de.telran.userrepo;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest  // при тестировании загружается только веб-часть
@AutoConfigureMockMvc
public class TestUserController {

    @Autowired
    MockMvc mockMvc; // возможность выполнять запросы в контроллере не загружая веб-сервер

    @Autowired
    UserController userController;

    @MockBean  // нужно заменить реализацию UserRepository на заглушку
    // используется для тестирования методов контроллера не создавая
    // в памяти часть отвечающую за базу данных
    UserRepository userRepository;

    @Test
    public void normalUserCreation() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\"name\":\"rob\", \"email\":\"rob@email.com\"}")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(content().string("User is valid"));
    }

    @Test
    public void noNameIsError() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\"email\":\"bob@google.com\"}")
                                .contentType("application/json")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", Is.is("Name is mandatory")))
                .andDo(print());
    }

    @Test
    public void noEmailIsError() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\"name\":\"bob\"}")
                                .contentType("application/json")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", Is.is("Email is mandatory")))
                .andDo(print());
    }

    @Test
    public void notValidEmailIsError() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\"name\":\"bob\", \"email\":\"google.com\"}")
                                .contentType("application/json")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", Is.is("Email should be valid")))
                .andDo(print());
    }

    @Test
    public void blankUserIsError() throws Exception {

    }

    @Test
    public void blankUserIsErrorAndNotValidEmailIsError() throws Exception {

    }

    @Test
    public void testNameEmailCombo() throws Exception {
        // userRepository.findById(1L) -> User("rob", "rob@gmail.com")

        when(userRepository.findById(1L))
                .thenReturn(
                        Optional.of(new User(1L, "rob", "rob@gmail.com"))
                );

//        when(userRepository.findById(anyLong()))
//                .thenReturn(
//                        Optional.of(new User(1L, "rob", "rob@gmail.com")),
//                        Optional.of(new User(2L, "bob", "bob@gmail.com"))
//                );

        doReturn(
                Optional.of(new User(1L, "rob", "rob@gmail.com"))
        ).when(userRepository).findById(10L);

        // "/users/1" -> "rob:rob@gmail.com"
        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("rob:rob@gmail.com"));

        // метод repository.findById(1L) вызывался только один раз
        verify(userRepository, times(1)).findById(1L);
        // больше никаких взаимодействий с userRepository не было,
        // и никакие его методы более не вызывались
        verifyNoMoreInteractions(userRepository);
    }
}