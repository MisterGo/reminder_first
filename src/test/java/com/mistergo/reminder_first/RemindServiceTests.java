package com.mistergo.reminder_first;

import com.mistergo.reminder_first.data.model.RemindEntity;
import com.mistergo.reminder_first.data.model.UserEntity;
import com.mistergo.reminder_first.data.repository.RemindRepository;
import com.mistergo.reminder_first.data.repository.UserRepository;
import com.mistergo.reminder_first.model.RemindDto;
import com.mistergo.reminder_first.model.RemindFilterDto;
import com.mistergo.reminder_first.quartz.RemindQuartzService;
import com.mistergo.reminder_first.service.RemindServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class RemindServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RemindRepository remindRepository;
    @Mock
    private RemindQuartzService remindQuartzService;
    @Mock
    private Principal principalMock;
    @Mock
    private Pageable pageable;
    @InjectMocks
    private RemindServiceImpl remindService;

    private UserEntity userEntity;

    @BeforeEach
    void initBeforeEach() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("admin");
        when(userRepository.findByUsername(anyString())).thenReturn(userEntity);

        when(principalMock.getName()).thenReturn(userEntity.getUsername());
    }

    @Test
    void create() throws SchedulerException {
        var remindDto = createRemindDto(null, "Title", "Description", LocalDateTime.now());
        var remindEntity = createRemindEntity(1L, remindDto.getTitle(), remindDto.getDescription(), remindDto.getRemind());
        when(remindRepository.save(notNull())).thenReturn(remindEntity);

        var result = remindService.create(remindDto, principalMock);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertEquals(remindDto.getTitle(), result.getTitle());
        assertEquals(remindDto.getDescription(), result.getDescription());
        assertEquals(remindDto.getRemind(), result.getRemind());

        verify(remindQuartzService).createRemindTrigger(notNull());
        reset(remindQuartzService);
    }

    @Test
    void edit() throws Exception {
        var oldRemindDto = createRemindDto(1L, "Old Title", "Old Description", LocalDateTime.now());

        var oldRemindEntity = createRemindEntity(oldRemindDto.getId(), oldRemindDto.getTitle(), oldRemindDto.getDescription(), oldRemindDto.getRemind());
        when(remindRepository.findById(anyLong())).thenReturn(Optional.of(oldRemindEntity));

        var editedRemindDto = createRemindDto(oldRemindDto.getId(), "Edited Title", "Edited Description", oldRemindDto.getRemind().plusDays(1));
        var editedRemindEntity = createRemindEntity(editedRemindDto.getId(), editedRemindDto.getTitle(), editedRemindDto.getDescription(), editedRemindDto.getRemind());;
        when(remindRepository.save(notNull())).thenReturn(editedRemindEntity);

        var result = remindService.edit(editedRemindDto, principalMock);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertEquals(editedRemindDto.getTitle(), result.getTitle());
        assertEquals(editedRemindDto.getDescription(), result.getDescription());
        assertEquals(editedRemindDto.getRemind(), result.getRemind());

        verify(remindQuartzService).editRemindTrigger(notNull());
        reset(remindQuartzService);
    }

    @Test
    void deleteTest() throws Exception {
        var remindEntity = createRemindEntity(1L, "Title", "Description", LocalDateTime.now());
        when(remindRepository.findById(anyLong())).thenReturn(Optional.of(remindEntity));

        remindService.delete(1L, principalMock);
        verify(remindRepository).delete(eq(remindEntity));
        verify(remindQuartzService).deleteRemindTrigger(eq(1L));
        reset(remindRepository, remindQuartzService);
    }

    @Test
    void deleteTestException() {
        when(remindRepository.findById(anyLong())).thenThrow(NoSuchElementException.class);
        assertThrows(NoSuchElementException.class, () -> remindService.delete(1L, principalMock));
    }

    @Test
    void filter() {
        var filterDto = new RemindFilterDto();
        filterDto.setDateTimeFrom(LocalDateTime.now());
        filterDto.setDateTimeTo(LocalDateTime.now().plusDays(1));

        var remindEntity1 = createRemindEntity(1L, "Title 1", "Description 1", LocalDateTime.now());
        var remindEntity2 = createRemindEntity(2L, "Title 2", "Description 2", LocalDateTime.now().plusDays(1));

        when(remindRepository.findByUser_IdAndRemindBetween(notNull(), notNull(), notNull(), notNull()))
                .thenReturn(new PageImpl<>(List.of(remindEntity1, remindEntity2)));

        var result = remindService.filter(PageRequest.ofSize(10), principalMock, filterDto);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("Title 1", result.getContent().get(0).getTitle());
        assertEquals("Title 2", result.getContent().get(1).getTitle());
    }

    @Test
    void sort() {
    }

    @Test
    void list() {
        var remindEntity1 = createRemindEntity(1L, "Title 1", "Description 1", LocalDateTime.now());
        var remindEntity2 = createRemindEntity(2L, "Title 2", "Description 2", LocalDateTime.now().plusDays(1));

        when(remindRepository.findByUser_Id(notNull(), notNull()))
                .thenReturn(new PageImpl<>(List.of(remindEntity1, remindEntity2)));

        var result = remindService.list(PageRequest.ofSize(10), principalMock);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("Title 1", result.getContent().get(0).getTitle());
        assertEquals("Title 2", result.getContent().get(1).getTitle());
    }

    private RemindDto createRemindDto(Long id, String title, String oldDescription, LocalDateTime remind) {
        var remindDto = new RemindDto();
        remindDto.setId(id);
        remindDto.setTitle(title);
        remindDto.setDescription(oldDescription);
        remindDto.setRemind(remind);
        return remindDto;
    }

    private RemindEntity createRemindEntity(Long id, String title, String description, LocalDateTime remind) {
        var remindEntity = new RemindEntity();
        remindEntity.setUser(userEntity);
        remindEntity.setId(id);
        remindEntity.setTitle(title);
        remindEntity.setDescription(description);
        remindEntity.setRemind(remind);
        return remindEntity;
    }
}