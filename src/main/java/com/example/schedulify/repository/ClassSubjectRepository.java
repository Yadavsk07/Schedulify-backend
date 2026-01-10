package com.example.schedulify.repository;

import com.example.schedulify.model.ClassSubject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClassSubjectRepository extends MongoRepository<ClassSubject, String> {

    List<ClassSubject> findBySchoolId(String schoolId);

    long countBySchoolId(String schoolId);

    List<ClassSubject> findByClassGroupId(String classGroupId);

    List<ClassSubject> findBySchoolIdAndClassGroupId(String schoolId, String classId);

    boolean existsBySchoolIdAndClassGroupIdAndSubjectId(
            String schoolId,
            String classGroupId,
            String subjectId
    );
}
