package de.telran.userrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest  // в рамках теста спринг загружает только базу данных
public class DatabaseTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testUserCreation() throws Exception {
        User user = new User();
        user.setName("Sveta");
        user.setEmail("sveta@google.com");

        User savedUser = userRepository.save(user);

        assertEquals(savedUser.getId(), 1L);
    }
}