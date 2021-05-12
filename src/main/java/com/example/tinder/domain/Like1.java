package com.example.tinder.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
public class Like1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likeID;
    private int statusID;
    private int idUser;
}
