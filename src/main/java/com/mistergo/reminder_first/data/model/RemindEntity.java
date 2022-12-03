package com.mistergo.reminder_first.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity(name = "REMIND")
@Getter
@Setter
public class RemindEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private LocalDateTime remind;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;
}
