package com.example.schedulify.service;

import com.example.schedulify.model.*;
import com.example.schedulify.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassGroupRepository classGroupRepository;
    private final LabRoomRepository labRoomRepository;
    private final ClassSubjectRepository classSubjectRepository;

    public AdminService(TeacherRepository teacherRepository,
                        SubjectRepository subjectRepository,
                        ClassGroupRepository classGroupRepository,
                        LabRoomRepository labRoomRepository,
                        ClassSubjectRepository classSubjectRepository) {

        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.classGroupRepository = classGroupRepository;
        this.labRoomRepository = labRoomRepository;
        this.classSubjectRepository = classSubjectRepository;
    }

    // -------------------
    // TEACHERS
    // -------------------
    public Teacher saveTeacher(Teacher t) {
        return teacherRepository.save(t);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public void deleteTeacher(String id) {
        teacherRepository.deleteById(id);
    }

    // -------------------
    // SUBJECTS (GENERIC)
    // -------------------
    public Subject saveSubject(Subject s) {
        return subjectRepository.save(s);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void deleteSubject(String id) {
        subjectRepository.deleteById(id);
    }

    // -------------------
    // CLASS GROUPS
    // -------------------
    public ClassGroup saveClassGroup(ClassGroup c) {
        return classGroupRepository.save(c);
    }

    public List<ClassGroup> getAllClassGroups() {
        return classGroupRepository.findAll();
    }

    public void deleteClassGroup(String id) {
        classGroupRepository.deleteById(id);
    }

    // -------------------
    // CLASS SUBJECTS (NEW)
    // -------------------
    public ClassSubject saveClassSubject(ClassSubject cs) {
        return classSubjectRepository.save(cs);
    }

    public List<ClassSubject> getAllClassSubjects() {
        return classSubjectRepository.findAll();
    }

    public List<ClassSubject> getClassSubjectsByClass(String classGroupId) {
        return classSubjectRepository.findByClassGroupId(classGroupId);
    }

    public void deleteClassSubject(String id) {
        classSubjectRepository.deleteById(id);
    }

    // -------------------
    // LAB ROOMS
    // -------------------
    public LabRoom saveLabRoom(LabRoom room) {
        return labRoomRepository.save(room);
    }

    public List<LabRoom> getAllLabRooms() {
        return labRoomRepository.findAll();
    }

    public void deleteLabRoom(String id) {
        labRoomRepository.deleteById(id);
    }
}
