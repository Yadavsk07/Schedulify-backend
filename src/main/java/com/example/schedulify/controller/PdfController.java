package com.example.schedulify.controller;

import com.example.schedulify.dto.SlotDto;
import com.example.schedulify.model.School;
import com.example.schedulify.model.Teacher;
import com.example.schedulify.repository.SchoolRepository;
import com.example.schedulify.repository.TeacherRepository;
import com.example.schedulify.service.PdfService;
import com.example.schedulify.service.TimetableQueryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin
public class PdfController {

    private final TimetableQueryService queryService;
    private final PdfService pdfService;
    private final SchoolRepository schoolRepo;
    private final TeacherRepository teacherRepo;

    public PdfController(
            TimetableQueryService queryService,
            PdfService pdfService,
            SchoolRepository schoolRepo,
            TeacherRepository teacherRepo
    ) {
        this.queryService = queryService;
        this.pdfService = pdfService;
        this.schoolRepo = schoolRepo;
        this.teacherRepo = teacherRepo;
    }

    /* =====================================================
       TEACHER TIMETABLE PDF
       ===================================================== */
    @GetMapping("/teacher/{schoolId}/{teacherId}")
    public void downloadTeacherPdf(
            @PathVariable String schoolId,
            @PathVariable String teacherId,
            HttpServletResponse response
    ) throws Exception {

        School school = schoolRepo.findById(schoolId).orElseThrow();
        Teacher teacher = teacherRepo.findById(teacherId).orElseThrow();

        Map<String, List<SlotDto>> timetable =
                queryService.getTeacherTimetable(schoolId, teacherId).getTimetable();

        byte[] pdf = pdfService.generateTeacherTimetablePdf(
                school.getName(),
                teacher.getName(),
                timetable
        );

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"Teacher_" +
                        teacher.getName().replaceAll("\\s+", "_") + ".pdf\""
        );
        response.setContentType("application/pdf");
        response.getOutputStream().write(pdf);
    }

    /* =====================================================
       CLASS TIMETABLE PDF  ✅ FIX ADDED
       ===================================================== */
    @GetMapping("/class/{schoolId}/{classId}/{sectionId}")
    public void downloadClassPdf(
            @PathVariable String schoolId,
            @PathVariable String classId,
            @PathVariable String sectionId,
            HttpServletResponse response
    ) throws Exception {

        School school = schoolRepo.findById(schoolId).orElseThrow();

        Map<String, List<SlotDto>> timetable =
                queryService
                        .getClassTimetable(schoolId, classId, sectionId)
                        .getTimetable();

        byte[] pdf = pdfService.generateClassTimetablePdf(
                school.getName(),
                classId,
                sectionId,
                timetable
        );

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"Class_" +
                        classId + "_" + sectionId + ".pdf\""
        );
        response.setContentType("application/pdf");
        response.getOutputStream().write(pdf);
    }
}
