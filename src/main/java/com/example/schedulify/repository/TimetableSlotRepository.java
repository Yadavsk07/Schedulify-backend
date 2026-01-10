package com.example.schedulify.repository;

import com.example.schedulify.model.TimetableSlot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TimetableSlotRepository extends MongoRepository<TimetableSlot, String> {
    List<TimetableSlot> findBySchoolIdAndClassGroupIdAndSectionIdOrderByDayAscPeriodAsc(
            String schoolId, String classGroupId, String sectionId);

    List<TimetableSlot> findBySchoolIdAndTeacherIdOrderByDayAscPeriodAsc(String schoolId, String teacherId);

    void deleteBySchoolIdAndClassGroupIdAndSectionId(String schoolId, String classGroupId, String sectionId);

    List<TimetableSlot> findBySchoolId(String schoolId);

    List<TimetableSlot> findBySchoolIdAndTeacherId(String schoolId, String teacherId);

    void deleteBySchoolId(String schoolId);
}
