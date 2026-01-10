package com.example.schedulify.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CreateSubjectDTO {

    @Id
    private String id;
    private String name;
    private String code;
    //private int periodsPerWeek;
    private boolean requiresConsecutive;
    private int consecutiveSize;
    private String roomType;
}
