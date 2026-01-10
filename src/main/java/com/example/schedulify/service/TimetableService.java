package com.example.schedulify.service;

import com.example.schedulify.model.*;
import com.example.schedulify.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final TimetableSlotRepository slotRepo;
    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;
    private final ClassGroupRepository classRepo;
    private final ClassSubjectRepository classSubjectRepo;

    public TimetableService(
            TimetableSlotRepository slotRepo,
            TeacherRepository teacherRepo,
            SubjectRepository subjectRepo,
            ClassGroupRepository classRepo,
            ClassSubjectRepository classSubjectRepo) {
        this.slotRepo = slotRepo;
        this.teacherRepo = teacherRepo;
        this.subjectRepo = subjectRepo;
        this.classRepo = classRepo;
        this.classSubjectRepo = classSubjectRepo;
    }

    // ======================================================
    // MAIN ENTRY
    // ======================================================
    @Transactional
    public boolean generateFullSchoolTimetable(String schoolId) {

        slotRepo.deleteBySchoolId(schoolId);

        List<ClassGroup> classes = classRepo.findBySchoolId(schoolId);
        List<Subject> subjects = subjectRepo.findBySchoolId(schoolId);
        List<Teacher> teachers = teacherRepo.findBySchoolId(schoolId);

        if (classes.isEmpty() || subjects.isEmpty() || teachers.isEmpty()) {
            throw new RuntimeException("Classes / Subjects / Teachers missing");
        }

        Map<String, Subject> subjectMap = subjects.stream()
                .collect(Collectors.toMap(Subject::getId, s -> s));

        // --------------------------------------------------
        // INDEX TEACHERS: subject → class → teachers
        // --------------------------------------------------
        Map<String, Map<String, List<Teacher>>> teacherIndex = new HashMap<>();

        for (Teacher t : teachers) {
            for (String subjectId : t.getSubjectIds()) {
                for (String classId : t.getClassGroupIds()) {
                    teacherIndex
                            .computeIfAbsent(subjectId, k -> new HashMap<>())
                            .computeIfAbsent(classId, k -> new ArrayList<>())
                            .add(t);
                }
            }
        }

        // --------------------------------------------------
        // TEACHER LOAD TRACKING
        // --------------------------------------------------
        Map<String, Integer> load = new HashMap<>();
        teachers.forEach(t -> load.put(t.getId(), 0));

        // --------------------------------------------------
        // FIX: CONSISTENT SUBJECT → TEACHER PER CLASS+SECTION
        // --------------------------------------------------
        Map<String, String> subjectTeacherAssignment = new HashMap<>();
        // key = classId|section|subjectId

        List<String> days = List.of("MON","TUE","WED","THU","FRI");
        int periodsPerDay = 8;
        int totalSlots = days.size() * periodsPerDay;

        List<TimetableSlot> result = new ArrayList<>();

        // ======================================================
        // GENERATE TIMETABLE
        // ======================================================
        for (ClassGroup cg : classes) {

            List<ClassSubject> classSubjects =
                    classSubjectRepo.findByClassGroupId(cg.getId());

            if (classSubjects.isEmpty()) {
                throw new RuntimeException(
                        "No subjects assigned for class " + cg.getId()
                );
            }

            List<Subject> weeklyPlan =
                    buildWeeklyPlan(classSubjects, subjectMap, totalSlots);

            for (String section : cg.getSections()) {

                int idx = 0;

                for (String day : days) {
                    for (int p = 0; p < periodsPerDay; p++) {

                        Subject subject = weeklyPlan.get(idx++);
                        String key = cg.getId() + "|" + section + "|" + subject.getId();

                        Teacher teacher;

                        if (subjectTeacherAssignment.containsKey(key)) {
                            teacher = teacherRepo.findById(
                                    subjectTeacherAssignment.get(key)
                            ).orElseThrow(() ->
                                    new RuntimeException("Teacher not found")
                            );
                        } else {
                            teacher = pickTeacher(
                                    teacherIndex,
                                    load,
                                    subject.getId(),
                                    cg.getId()
                            );
                            subjectTeacherAssignment.put(key, teacher.getId());
                        }

                        load.put(
                                teacher.getId(),
                                load.get(teacher.getId()) + 1
                        );

                        TimetableSlot slot = new TimetableSlot();
                        slot.setId(UUID.randomUUID().toString());
                        slot.setSchoolId(schoolId);
                        slot.setClassGroupId(cg.getId());
                        slot.setSectionId(section);
                        slot.setDay(day);
                        slot.setPeriod(p);
                        slot.setSubjectId(subject.getId());
                        slot.setTeacherId(teacher.getId());
                        slot.setLocked(false);

                        result.add(slot);
                    }
                }
            }
        }

        slotRepo.saveAll(result);
        return true;
    }

    // ======================================================
    // TEACHER PICKER (SAFE)
    // ======================================================
    private Teacher pickTeacher(
            Map<String, Map<String, List<Teacher>>> index,
            Map<String, Integer> load,
            String subjectId,
            String classId) {

        List<Teacher> pool =
                Optional.ofNullable(index.get(subjectId))
                        .map(m -> m.get(classId))
                        .orElse(null);

        if (pool == null || pool.isEmpty()) {
            throw new RuntimeException(
                    "No teacher for subject " + subjectId +
                            " in class " + classId
            );
        }

        return pool.stream()
                .filter(t -> load.get(t.getId()) < t.getMaxPeriodsPerWeek())
                .min(Comparator.comparingInt(t -> load.get(t.getId())))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Teacher overload for subject " + subjectId +
                                        " in class " + classId
                        ));
    }

    // ======================================================
    // WEEKLY PLAN BUILDER
    // ======================================================
    private List<Subject> buildWeeklyPlan(
            List<ClassSubject> classSubjects,
            Map<String, Subject> subjectMap,
            int totalSlots) {

        List<Subject> plan = new ArrayList<>();

        for (ClassSubject cs : classSubjects) {
            Subject s = subjectMap.get(cs.getSubjectId());
            if (s == null) continue;

            for (int i = 0; i < cs.getPeriodsPerWeek(); i++) {
                plan.add(s);
            }
        }

        if (plan.size() != totalSlots) {
            throw new RuntimeException(
                    "Weekly plan size " + plan.size() +
                            " != " + totalSlots
            );
        }

        Collections.shuffle(plan);
        return plan;
    }
}
