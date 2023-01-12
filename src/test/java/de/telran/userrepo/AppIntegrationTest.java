package de.telran.userrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
// для запуска тестов под JUnit4, для JUnit5 не нужна эта аннотация, все происходит автоматически
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// запускать тесты на незанятом случайном порту
public class AppIntegrationTest {

    @Value(value = "${local.server.port}")
    private int port;

    // TestRestTemplate - класс для выполнения http запросов
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Test
    public void createUserTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");

        // тело запроса + заголовки + url
        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"rob\", \"email\":\"rob@email.com\"}",
                headers
        );

        String body =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/users",
                        request,
                        String.class
                ).getBody();

        assertEquals(body, "User is valid");
    }

    @Test
    public void testUserCreation() throws Exception {
        User user = new User();
        user.setName("Sveta");
        user.setEmail("sveta@google.com");

        User savedUser = userRepository.save(user);

        assertEquals(savedUser.getId(), 1L);
    }
}