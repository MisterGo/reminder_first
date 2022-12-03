package com.mistergo.reminder_first.controller;

import com.mistergo.reminder_first.model.RemindDto;
import com.mistergo.reminder_first.model.RemindFilterDto;
import com.mistergo.reminder_first.service.RemindService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {
    private final RemindService remindService;

    @PostMapping("/create")
    public ResponseEntity<RemindDto> create(@RequestBody RemindDto remindDto, Principal principal) throws SchedulerException {
        return new ResponseEntity<>(remindService.create(remindDto, principal), HttpStatus.CREATED);
    }

    @PostMapping("/edit")
    public ResponseEntity<RemindDto> edit(@RequestBody RemindDto remindDto, Principal principal) throws SchedulerException {
        return new ResponseEntity<>(remindService.edit(remindDto, principal), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id, Principal principal) throws SchedulerException {
        remindService.delete(id, principal);
    }

    @GetMapping("/sort")
    public Page<RemindDto> sort(@RequestParam String orderBy, @RequestParam @Nullable boolean desc, Pageable pageable, Principal principal) {
        return remindService.sort(pageable, principal, orderBy, desc);
    }

    @PostMapping("/filter")
    public Page<RemindDto> filter(@RequestBody RemindFilterDto filter, Pageable pageable, Principal principal) {
        return remindService.filter(pageable, principal, filter);
    }

    @GetMapping("/list")
    public Page<RemindDto> list(Pageable pageable, Principal principal) {
        return remindService.list(pageable, principal);
    }

    @ExceptionHandler(SchedulingException.class)
    public ResponseEntity<Object> handleSchedulerException() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
