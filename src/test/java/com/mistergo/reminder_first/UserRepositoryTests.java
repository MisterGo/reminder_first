package com.mistergo.reminder_first;

import com.mistergo.reminder_first.data.model.UserEntity;
import com.mistergo.reminder_first.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findUserTest() {
        var userEntity = createUserEntity();
        userRepository.save(userEntity);

        var result = userRepository.findByUsername("admin");
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertTrue(result.isEnabled());
        assertEquals("my@email.com", result.getEmail());
        assertEquals("@MyTelegramId", result.getTelegramId());
    }

    private static UserEntity createUserEntity() {
        var userEntity = new UserEntity();
        userEntity.setUsername("admin");
        userEntity.setEnabled(true);
        userEntity.setEmail("my@email.com");
        userEntity.setTelegramId("@MyTelegramId");
        return userEntity;
    }
}
