package com.mistergo.reminder_first.service;

import com.mistergo.reminder_first.data.model.RemindEntity;
import com.mistergo.reminder_first.data.model.UserEntity;
import com.mistergo.reminder_first.data.repository.RemindRepository;
import com.mistergo.reminder_first.data.repository.UserRepository;
import com.mistergo.reminder_first.model.RemindDto;
import com.mistergo.reminder_first.model.RemindFilterDto;
import com.mistergo.reminder_first.quartz.RemindQuartzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemindServiceImpl implements RemindService {
    private final UserRepository userRepository;
    private final RemindRepository remindRepository;
    private final RemindQuartzService remindQuartzService;

    @Override
    public RemindDto create(RemindDto remindDto, Principal principal) throws SchedulerException {
        if (getUserEntity(principal) != null) {
            var createdDto = mapEntityToDto(remindRepository.save(mapDtoToEntity(remindDto, getUserEntity(principal))));
            remindQuartzService.createRemindTrigger(createdDto);
            return createdDto;
        }
        return null;
    }

    @Override
    public RemindDto edit(RemindDto remindDto, Principal principal) throws SchedulerException {
        if (getUserEntity(principal) != null) {
            var foundRemindEntity = remindRepository.findById(remindDto.getId()).orElseThrow();
            updateEntity(foundRemindEntity, remindDto);
            var editedRemind = mapEntityToDto(remindRepository.save(foundRemindEntity));
            remindQuartzService.editRemindTrigger(editedRemind);
            return editedRemind;
        }
        return null;
    }

    @Override
    public void delete(Long id, Principal principal) throws SchedulerException {
        if (getUserEntity(principal) != null) {
            var foundRemindEntity = remindRepository.findById(id).orElseThrow();
            remindRepository.delete(foundRemindEntity);
            remindQuartzService.deleteRemindTrigger(id);
        }
    }

    @Override
    public Page<RemindDto> filter(Pageable pageable, Principal principal, RemindFilterDto filter) {
        return remindRepository.findByUser_IdAndRemindBetween(getUserEntity(principal).getId(), filter.getDateTimeFrom(), filter.getDateTimeTo(), pageable)
                .map(this::mapEntityToDto);
    }

    @Override
    public Page<RemindDto> sort(Pageable pageable, Principal principal, String orderBy, boolean desc) {
        switch (orderBy) {
            case "title":
                return desc ? remindRepository.findByUser_IdOrderByTitleDesc(getUserEntity(principal).getId(), pageable).map(this::mapEntityToDto)
                        : remindRepository.findByUser_IdOrderByTitleAsc(getUserEntity(principal).getId(), pageable).map(this::mapEntityToDto);
            case "remind":
                return desc ? remindRepository.findByUser_IdOrderByRemindDesc(getUserEntity(principal).getId(), pageable).map(this::mapEntityToDto)
                        : remindRepository.findByUser_IdOrderByRemindAsc(getUserEntity(principal).getId(), pageable).map(this::mapEntityToDto);
            default:
                return null;
        }
    }

    @Override
    public Page<RemindDto> list(Pageable pageable, Principal principal) {
        return remindRepository.findByUser_Id(getUserEntity(principal).getId(), pageable)
                .map(this::mapEntityToDto);
    }

    private UserEntity getUserEntity(Principal principal) {
        return userRepository.findByUsername(principal.getName());
    }

    private void updateEntity(RemindEntity foundRemindEntity, RemindDto remindDto) {
        foundRemindEntity.setTitle(remindDto.getTitle());
        foundRemindEntity.setDescription(remindDto.getDescription());
        foundRemindEntity.setRemind(remindDto.getRemind());
    }
    private RemindDto mapEntityToDto(RemindEntity remindEntity) {
        var remindDto = new RemindDto();
        remindDto.setId(remindEntity.getId());
        remindDto.setTitle(remindEntity.getTitle());
        remindDto.setDescription(remindEntity.getDescription());
        remindDto.setRemind(remindEntity.getRemind());
        remindDto.setUserId(remindEntity.getUser().getId());
        return remindDto;
    }

    private RemindEntity mapDtoToEntity(RemindDto remindDto, UserEntity userEntity) {
        var remindEntity = new RemindEntity();
        remindEntity.setUser(userEntity);
        remindEntity.setTitle(remindDto.getTitle());
        remindEntity.setDescription(remindDto.getDescription());
        remindEntity.setRemind(remindDto.getRemind());
        return remindEntity;
    }
}
