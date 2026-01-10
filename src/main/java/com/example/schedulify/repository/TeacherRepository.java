package com.example.schedulify.repository;

import com.example.schedulify.model.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends MongoRepository<Teacher, String> {
    List<Teacher> findBySchoolId(String schoolId);
    Optional<Teacher> findBySchoolIdAndTeacherCode(String schoolId, String teacherCode);

    long countBySchoolId(String schoolId);

    Optional<Teacher> findBySchoolIdAndId(String id, String teacherId);

    void deleteBySchoolId(String schoolId);
}
