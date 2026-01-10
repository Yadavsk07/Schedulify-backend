package com.example.schedulify.service;

import com.example.schedulify.dto.SlotDto;
import com.example.schedulify.dto.ClassTimetableResponse;
import com.example.schedulify.dto.TeacherTimetableResponse;
import com.example.schedulify.model.TimetableSlot;
import com.example.schedulify.model.Subject;
import com.example.schedulify.model.Teacher;
import com.example.schedulify.model.LabRoom;
import com.example.schedulify.repository.TimetableSlotRepository;
import com.example.schedulify.repository.SubjectRepository;
import com.example.schedulify.repository.TeacherRepository;
import com.example.schedulify.repository.LabRoomRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableQueryService {

    private final TimetableSlotRepository slotRepo;
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final LabRoomRepository labRepo;

    public TimetableQueryService(TimetableSlotRepository slotRepo,
                                 SubjectRepository subjectRepo,
                                 TeacherRepository teacherRepo,
                                 LabRoomRepository labRepo) {
        this.slotRepo = slotRepo;
        this.subjectRepo = subjectRepo;
        this.teacherRepo = teacherRepo;
        this.labRepo = labRepo;
    }

    public ClassTimetableResponse getClassTimetable(String schoolId, String classGroupId, String sectionId) {
        List<TimetableSlot> slots = slotRepo.findBySchoolIdAndClassGroupIdAndSectionIdOrderByDayAscPeriodAsc(
                schoolId, classGroupId, sectionId);

        Map<String, List<SlotDto>> map = slots.stream()
                .map(this::toDto)
                .collect(Collectors.groupingBy(SlotDto::getDay, LinkedHashMap::new, Collectors.toList()));

        ClassTimetableResponse res = new ClassTimetableResponse();
        res.setClassGroupId(classGroupId);
        res.setSectionId(sectionId);
        res.setTimetable(map != null ? map : new LinkedHashMap<>());
        return res;
    }

    public TeacherTimetableResponse getTeacherTimetable(String schoolId, String teacherId) {
        List<TimetableSlot> slots = slotRepo.findBySchoolIdAndTeacherIdOrderByDayAscPeriodAsc(schoolId, teacherId);

        Map<String, List<SlotDto>> map = slots.stream()
                .map(this::toDto)
                .collect(Collectors.groupingBy(SlotDto::getDay, LinkedHashMap::new, Collectors.toList()));

        TeacherTimetableResponse res = new TeacherTimetableResponse();
        res.setTeacherId(teacherId);
        res.setTimetable(map != null ? map : new LinkedHashMap<>());
        return res;
    }

    private SlotDto toDto(TimetableSlot s) {
        SlotDto dto = new SlotDto();
        dto.setId(s.getId());
        dto.setDay(s.getDay());
        dto.setPeriod(s.getPeriod());
        dto.setClassGroupId(s.getClassGroupId());
        dto.setSectionId(s.getSectionId());
        dto.setLocked(s.isLocked());
        if (s.getSubjectId() != null) {
            subjectRepo.findById(s.getSubjectId()).ifPresent(sub -> {
                dto.setSubjectId(sub.getId());
                dto.setSubjectName(sub.getName());
            });
        }
        if (s.getTeacherId() != null) {
            teacherRepo.findById(s.getTeacherId()).ifPresent(t -> {
                dto.setTeacherId(t.getId());
                dto.setTeacherName(t.getName());
            });
        }
        if (s.getLabRoomId() != null) {
            labRepo.findById(s.getLabRoomId()).ifPresent(l -> {
                dto.setLabRoomId(l.getId());
                dto.setLabRoomName(l.getName());
            });
        }
        return dto;
    }

    public  String exportTeacherTimetableCsv(String schoolId, String teacherId) {

        List<TimetableSlot> slots = slotRepo.findBySchoolIdAndTeacherId(schoolId, teacherId);

        StringBuilder sb = new StringBuilder();
        sb.append("Day,Period,Subject,Class,Section,Room\n");

        for (TimetableSlot s : slots) {
            sb.append(s.getDay()).append(',')
                    .append(s.getPeriod() + 1).append(',')
                    .append(s.getSubjectId()).append(',')
                    .append(s.getClassGroupId()).append(',')
                    .append(s.getSectionId()).append(',')
                    .append(s.getLabRoomId() == null ? "CLASSROOM" : s.getLabRoomId())
                    .append("\n");
        }

        return sb.toString();
    }

}
