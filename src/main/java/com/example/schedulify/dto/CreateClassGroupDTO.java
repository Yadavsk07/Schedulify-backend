package com.example.schedulify.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateClassGroupDTO {
    private String name;
    private List<String> sectionIds; // e.g. ["A","B"]
    private List<String> subjectIds;
}
