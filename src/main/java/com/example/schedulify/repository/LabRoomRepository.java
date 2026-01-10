package com.example.schedulify.repository;

import com.example.schedulify.model.LabRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LabRoomRepository extends MongoRepository<LabRoom, String> {
    List<LabRoom> findBySchoolId(String schoolId);

    long countBySchoolId(String schoolId);
}
