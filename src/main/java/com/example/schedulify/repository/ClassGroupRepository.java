package com.example.schedulify.repository;

import com.example.schedulify.model.ClassGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClassGroupRepository extends MongoRepository<ClassGroup, String> {
    List<ClassGroup> findBySchoolId(String schoolId);
    Optional<ClassGroup> findBySchoolIdAndId(String schoolId, String id);

    long countBySchoolId(String schoolId);
}
