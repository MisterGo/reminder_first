package com.mistergo.reminder_first.data.repository;

import com.mistergo.reminder_first.data.model.RemindEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RemindRepository extends JpaRepository<RemindEntity, Long> {
    Page<RemindEntity> findByUser_Id(Long userId, Pageable pageable);

    Page<RemindEntity> findByUser_IdAndRemindBetween(Long userId, LocalDateTime remindStart, LocalDateTime remindEnd, Pageable pageable);

    Page<RemindEntity> findByUser_IdOrderByRemindAsc(Long id, Pageable pageable);

    Page<RemindEntity> findByUser_IdOrderByRemindDesc(Long id, Pageable pageable);

    Page<RemindEntity> findByUser_IdOrderByTitleAsc(Long id, Pageable pageable);

    Page<RemindEntity> findByUser_IdOrderByTitleDesc(Long id, Pageable pageable);
}
