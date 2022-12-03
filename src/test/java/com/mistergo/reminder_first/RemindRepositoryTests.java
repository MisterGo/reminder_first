package com.mistergo.reminder_first;

import com.mistergo.reminder_first.data.model.RemindEntity;
import com.mistergo.reminder_first.data.model.UserEntity;
import com.mistergo.reminder_first.data.repository.RemindRepository;
import com.mistergo.reminder_first.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class RemindRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RemindRepository remindRepository;

    @Test
    public void findByUser_IdTest() {
        var adminUserEntity = createUserEntity(1L, "admin");
        var userEntity = createUserEntity(2L, "user");
        createRemindEntity(adminUserEntity, "Admin Title 1", "Description", LocalDateTime.now());
        createRemindEntity(adminUserEntity, "Admin Title 2", "Description", LocalDateTime.now());
        createRemindEntity(userEntity, "User Title 1", "Description", LocalDateTime.now());

        var adminResult = remindRepository.findByUser_Id(adminUserEntity.getId(), PageRequest.ofSize(10));
        assertNotNull(adminResult);
        assertEquals(2, adminResult.getContent().size());
        assertEquals("Admin Title 1", adminResult.getContent().get(0).getTitle());
        assertEquals("Admin Title 2", adminResult.getContent().get(1).getTitle());

        var userResult = remindRepository.findByUser_Id(userEntity.getId(), PageRequest.ofSize(10));
        assertNotNull(adminResult);
        assertEquals(1, userResult.getContent().size());
        assertEquals("User Title 1", userResult.getContent().get(0).getTitle());
    }

    @Test
    public void findByUser_IdAndRemindBetweenTest() {
        var userEntity = createUserEntity(1L, "admin");
        var now = LocalDateTime.now();
        createRemindEntity(userEntity, "Admin Title 1", "Description 1", now);
        createRemindEntity(userEntity, "Admin Title 2", "Description 2", now.plusDays(1));
        createRemindEntity(userEntity, "Admin Title 3", "Description 3", now.plusDays(2));

        var result = remindRepository
                .findByUser_IdAndRemindBetween(userEntity.getId(),
                        now.plusDays(1),
                        now.plusDays(3),
                        PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Admin Title 2", result.getContent().get(0).getTitle());
        assertEquals("Admin Title 3", result.getContent().get(1).getTitle());
    }

    @Test
    public void findByUser_IdOrderByTitleAscTest() {
        var userEntity = createUserEntity(1L, "admin");
        var now = LocalDateTime.now();
        createRemindEntity(userEntity, "Admin Title 1", "Description 1", now);
        createRemindEntity(userEntity, "Admin Title 2", "Description 2", now);
        createRemindEntity(userEntity, "Admin Title 3", "Description 3", now);

        var result = remindRepository
                .findByUser_IdOrderByTitleAsc(userEntity.getId(),
                        PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Admin Title 1", result.getContent().get(0).getTitle());
        assertEquals("Admin Title 2", result.getContent().get(1).getTitle());
        assertEquals("Admin Title 3", result.getContent().get(2).getTitle());
    }

    @Test
    public void findByUser_IdOrderByTitleDescTest() {
        var userEntity = createUserEntity(1L, "admin");
        var now = LocalDateTime.now();
        createRemindEntity(userEntity, "Admin Title 1", "Description 1", now);
        createRemindEntity(userEntity, "Admin Title 2", "Description 2", now);
        createRemindEntity(userEntity, "Admin Title 3", "Description 3", now);

        var result = remindRepository
                .findByUser_IdOrderByTitleDesc(userEntity.getId(),
                        PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Admin Title 3", result.getContent().get(0).getTitle());
        assertEquals("Admin Title 2", result.getContent().get(1).getTitle());
        assertEquals("Admin Title 1", result.getContent().get(2).getTitle());
    }

    @Test
    public void findByUser_IdOrderByRemindAscTest() {
        var userEntity = createUserEntity(1L, "admin");
        var now = LocalDateTime.now();
        createRemindEntity(userEntity, "Admin Title 1", "Description 1", now.plusDays(3));
        createRemindEntity(userEntity, "Admin Title 2", "Description 2", now.plusDays(2));
        createRemindEntity(userEntity, "Admin Title 3", "Description 3", now.plusDays(1));

        var result = remindRepository
                .findByUser_IdOrderByRemindAsc(userEntity.getId(),
                        PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Admin Title 3", result.getContent().get(0).getTitle());
        assertEquals("Admin Title 2", result.getContent().get(1).getTitle());
        assertEquals("Admin Title 1", result.getContent().get(2).getTitle());
    }

    @Test
    public void findByUser_IdOrderByRemindDescTest() {
        var userEntity = createUserEntity(1L, "admin");
        var now = LocalDateTime.now();
        createRemindEntity(userEntity, "Admin Title 1", "Description 1", now.plusDays(3));
        createRemindEntity(userEntity, "Admin Title 2", "Description 2", now.plusDays(2));
        createRemindEntity(userEntity, "Admin Title 3", "Description 3", now.plusDays(1));

        var result = remindRepository
                .findByUser_IdOrderByRemindDesc(userEntity.getId(),
                        PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Admin Title 1", result.getContent().get(0).getTitle());
        assertEquals("Admin Title 2", result.getContent().get(1).getTitle());
        assertEquals("Admin Title 3", result.getContent().get(2).getTitle());
    }

    private RemindEntity createRemindEntity(UserEntity userEntity, String title, String description, LocalDateTime remind) {
        var remindEntity = new RemindEntity();
        remindEntity.setUser(userEntity);
        remindEntity.setTitle(title);
        remindEntity.setDescription(description);
        remindEntity.setRemind(remind);
        remindRepository.save(remindEntity);
        return remindEntity;
    }

    private UserEntity createUserEntity(Long id, String username) {
        var userEntity = new UserEntity();
        userEntity.setUsername(username);
        userRepository.save(userEntity);
        return userEntity;
    }
}
