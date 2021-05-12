package com.example.tinder.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
@Entity
public class Notification {
    @Id
    private int notification_id;

    @Enumerated(EnumType.STRING)
    private ActivityType type ;

    private int userid;
    private int from_userid;


}
