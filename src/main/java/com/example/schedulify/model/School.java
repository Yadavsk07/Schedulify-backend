package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("schools")
@Data
public class School {
    @Id
    private String id;
    private String name;
    private String schoolCode; // unique short code admins will use
    private String adminEmail;
    private String passwordHash; // bcrypt
    private Instant createdAt = Instant.now();
    private String timezone = "Asia/Kolkata";


}
