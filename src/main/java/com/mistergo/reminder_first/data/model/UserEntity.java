package com.mistergo.reminder_first.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "USERS")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

    private boolean enabled;

    private String email;

    private String telegramId;
}
