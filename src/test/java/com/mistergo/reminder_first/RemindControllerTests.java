package com.mistergo.reminder_first;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mistergo.reminder_first.model.RemindDto;
import com.mistergo.reminder_first.model.RemindFilterDto;
import com.mistergo.reminder_first.service.RemindService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ReminderFirstApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class RemindControllerTests {
    private static final String PREFIX = "/api/v1/reminder";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RemindService remindService;

    @Mock
    private Principal principalMock;

    @Test
    public void createRemindTest() throws  Exception {
        var remindDto = getRemindDto("title1", "desc1", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        when(remindService.create(notNull(), notNull())).thenReturn(remindDto);

        var mvcResult = mockMvc.perform(post(PREFIX + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(remindDto))
                        .principal(principalMock))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonNode = getJsonNode(mvcResult);
        assertEquals("title1", jsonNode.get("title").asText());
        assertEquals("desc1", jsonNode.get("description").asText());
    }

    @Test
    public void editRemindTest() throws  Exception {
        var editedRemindDto = getRemindDto("edited title1", "edited desc1", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        editedRemindDto.setId(1L);
        when(remindService.edit(notNull(), notNull())).thenReturn(editedRemindDto);

        var mvcResult = mockMvc.perform(post(PREFIX + "/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(editedRemindDto))
                        .principal(principalMock)
                )
                .andExpect(status().isOk())
                .andReturn();

        var jsonNode = getJsonNode(mvcResult);
        assertEquals("edited title1", jsonNode.get("title").asText());
        assertEquals("edited desc1", jsonNode.get("description").asText());
    }

    @Test
    public void deleteTest() throws Exception {
        doNothing().when(remindService).delete(notNull(), notNull());
        mockMvc.perform(delete(PREFIX + "/delete/1")
                        .principal(mock(Principal.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void listTest() throws Exception {
        var remindDto1 = getRemindDto("title1", "desc1", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        var remindDto2 = getRemindDto("title2", "desc2", LocalDateTime.of(LocalDate.now(), LocalTime.MIN.plusHours(1)));
        when(remindService.list(notNull(), notNull())).thenReturn(new PageImpl<>(List.of(remindDto1, remindDto2)));

        var mvcResult = mockMvc.perform(get(PREFIX + "/list")
                        .principal(principalMock))
                .andExpect(status().isOk())
                .andReturn();

        var jsonNode = getJsonNode(mvcResult);
        var content = jsonNode.get("content");

        assertEquals(2, content.size());
        assertEquals("title1", content.get(0).get("title").asText());
        assertEquals("title2", content.get(1).get("title").asText());
    }

    @Test
    public void sortingByRemindAscTest() throws Exception {
        var remindDto1 = getRemindDto("title1", "desc1", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        var remindDto2 = getRemindDto("title2", "desc2", LocalDateTime.of(LocalDate.now(), LocalTime.MIN.plusHours(1)));
        when(remindService.sort(notNull(), notNull(), anyString(), anyBoolean())).thenReturn(new PageImpl<>(List.of(remindDto1, remindDto2)));

        var mvcResult = mockMvc.perform(get(PREFIX + "/sort")
                        .principal(principalMock)
                        .param("orderBy", "remind"))
                .andExpect(status().isOk())
                .andReturn();

        var jsonNode = getJsonNode(mvcResult);
        var content = jsonNode.get("content");

        assertEquals(2, content.size());
        assertEquals("title1", content.get(0).get("title").asText());
        assertEquals("title2", content.get(1).get("title").asText());
    }

    @Test
    public void sortingByTitleDescTest() throws Exception {
        var remindDto1 = getRemindDto("title1", "desc1", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        var remindDto2 = getRemindDto("title2", "desc2", LocalDateTime.of(LocalDate.now(), LocalTime.MIN.plusHours(1)));
        when(remindService.sort(notNull(), notNull(), anyString(), anyBoolean())).thenReturn(new PageImpl<>(List.of(remindDto2, remindDto1)));

        var mvcResult = mockMvc.perform(get(PREFIX + "/sort")
                        .principal(principalMock)
                        .param("orderBy", "remind")
                        .param("desc", "true"))
                .andExpect(status().isOk())
                .andReturn();

        var jsonNode = getJsonNode(mvcResult);
        var content = jsonNode.get("content");

        assertEquals(2, content.size());
        assertEquals("title2", content.get(0).get("title").asText());
        assertEquals("title1", content.get(1).get("title").asText());
    }

    @Test
    public void filterTest() throws Exception {
        var remindDto1 = getRemindDto("title1", "desc1", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        var remindDto2 = getRemindDto("title2", "desc2", LocalDateTime.of(LocalDate.now(), LocalTime.MIN.plusHours(1)));
        when(remindService.filter(notNull(), notNull(), notNull())).thenReturn(new PageImpl<>(List.of(remindDto1, remindDto2)));

        var mvcResult = mockMvc.perform(post(PREFIX + "/filter")
                        .principal(principalMock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new RemindFilterDto())))
                .andExpect(status().isOk())
                .andReturn();

        var jsonNode = getJsonNode(mvcResult);
        var content = jsonNode.get("content");

        assertEquals(2, content.size());
        assertEquals("title1", content.get(0).get("title").asText());
        assertEquals("title2", content.get(1).get("title").asText());
    }

    private RemindDto getRemindDto(String title, String desc, LocalDateTime remind) {
        var remindDto = new RemindDto();
        remindDto.setTitle(title);
        remindDto.setDescription(desc);
        remindDto.setRemind(remind);
        return remindDto;
    }

    private byte[] toJson(Object object) throws IOException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsBytes(object);
    }

    private JsonNode getJsonNode(MvcResult mvcResult) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    }
}
