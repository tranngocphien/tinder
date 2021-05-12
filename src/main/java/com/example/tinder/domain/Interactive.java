package com.example.tinder.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor

public class Interactive implements Serializable {
    @EmbeddedId
    private InteractiveID interactiveID;
    @Column
    private int status;
}
