package com.example.schedulify.repository;

import com.example.schedulify.model.SchoolSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SchoolSettingsRepository extends MongoRepository<SchoolSettings, String> {
    Optional<SchoolSettings> findBySchoolId(String schoolId);
    void deleteBySchoolId(String schoolId);

}
