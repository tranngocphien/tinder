package com.example.tinder.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDetail {
    private Status status;
    private List<Like1> likeList;
    private List<Comment> listComment;
}
