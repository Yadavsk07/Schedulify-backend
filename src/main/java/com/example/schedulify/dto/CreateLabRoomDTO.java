package com.example.schedulify.dto;

import lombok.Data;

@Data
public class CreateLabRoomDTO {

    private String id;
    private String name;
    private String subjectType;
    private int capacity;
}
