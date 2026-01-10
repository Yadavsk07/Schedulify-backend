package com.example.schedulify.repository;

import com.example.schedulify.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubjectRepository extends MongoRepository<Subject, String> {
    List<Subject> findBySchoolId(String schoolId);

    long countBySchoolId(String schoolId);
}
