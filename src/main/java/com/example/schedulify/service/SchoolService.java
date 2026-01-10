package com.example.schedulify.service;

import com.example.schedulify.model.School;
import com.example.schedulify.repository.SchoolRepository;
import org.springframework.stereotype.Service;

@Service
public class SchoolService {
    private final SchoolRepository repo;

    public SchoolService(SchoolRepository repo) {
        this.repo = repo;
    }

    public School getSchoolById(String schoolId) {
        return repo.findById(schoolId).orElse(null);
    }
}