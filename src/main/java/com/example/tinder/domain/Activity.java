package com.example.tinder.domain;

import lombok.Data;


@Data
public class Activity{
    private ActivityType type;
    private int from_userid;
    private int to_userid;
    private int idStatus;

}
