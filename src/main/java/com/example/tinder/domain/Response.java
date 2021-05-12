package com.example.tinder.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Response {
    private String message;
    private Interactive interactive;
}
