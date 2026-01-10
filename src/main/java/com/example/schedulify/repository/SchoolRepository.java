package com.example.schedulify.repository;

import com.example.schedulify.model.School;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SchoolRepository extends MongoRepository<School, String> {
    Optional<School> findByAdminEmail(String email);
    Optional<School> findBySchoolCode(String schoolCode);
}
