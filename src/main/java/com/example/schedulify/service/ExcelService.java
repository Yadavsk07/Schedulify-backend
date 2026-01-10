package com.example.schedulify.service;

import com.example.schedulify.model.*;
import com.example.schedulify.repository.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ExcelService {

    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;
    private final ClassGroupRepository classRepo;
    private final LabRoomRepository labRepo;
    private final ClassSubjectRepository classSubjectRepo;

    public ExcelService(TeacherRepository teacherRepo,
                        SubjectRepository subjectRepo,
                        ClassGroupRepository classRepo,
                        LabRoomRepository labRepo,
                        ClassSubjectRepository classSubjectRepo) {
        this.teacherRepo = teacherRepo;
        this.subjectRepo = subjectRepo;
        this.classRepo = classRepo;
        this.labRepo = labRepo;
        this.classSubjectRepo = classSubjectRepo;
    }

    public Map<String, Integer> importMasterExcel(MultipartFile file, String schoolId) throws Exception {
        Workbook wb = new XSSFWorkbook(file.getInputStream());
        Map<String, Integer> result = new HashMap<>();

        result.put("teachers", importTeachers(wb, schoolId));
        result.put("subjects", importSubjects(wb, schoolId));
        result.put("classes", importClasses(wb, schoolId));
        result.put("labs", importLabs(wb, schoolId));
        result.put("classSubjects", importClassSubjects(wb, schoolId));

        wb.close();
        return result;
    }

    // ---------------- TEACHERS ----------------
    private int importTeachers(Workbook wb, String schoolId) {
        Sheet sheet = wb.getSheet("Teachers");
        if (sheet == null) return 0;

        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            Teacher t = new Teacher();
            t.setId(getStr(r, 0));
            t.setSchoolId(schoolId);
            t.setName(getStr(r, 1));
            t.setSubjectIds(csv(getStr(r, 2)));
            t.setClassGroupIds(csv(getStr(r, 3)));
            t.setMaxPeriodsPerWeek((int) getNum(r, 4, 20));

            teacherRepo.save(t);
            count++;
        }
        return count;
    }

    // ---------------- SUBJECTS ----------------
    private int importSubjects(Workbook wb, String schoolId) {
        Sheet sheet = wb.getSheet("Subjects");
        if (sheet == null) return 0;

        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            Subject s = new Subject();
            s.setId(getStr(r, 0));
            s.setSchoolId(schoolId);
            s.setName(getStr(r, 1));
            s.setCode(getStr(r, 2));
            s.setRoomType(parseRoomType(getStr(r, 3)));
            s.setRequiresConsecutive(Boolean.parseBoolean(getStr(r, 4)));
            s.setConsecutiveSize((int) getNum(r, 5, 0));

            subjectRepo.save(s);
            count++;
        }
        return count;
    }

    // ---------------- CLASSES ----------------
    private int importClasses(Workbook wb, String schoolId) {
        Sheet sheet = wb.getSheet("Classes");
        if (sheet == null) return 0;

        Map<String, ClassGroup> classMap = new HashMap<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            String classGroupId = getStr(r, 0);
            String className = getStr(r, 1);

            ClassGroup cg = classMap.computeIfAbsent(classGroupId, id -> {
                ClassGroup c = new ClassGroup();
                c.setId(id);
                c.setSchoolId(schoolId);
                c.setName(className);
                c.setSections(new ArrayList<>());
                c.setSubjectIds(csv(getStr(r, 3)));
                return c;
            });

            String sectionsCsv = getStr(r, 2);
            List<String> sections = csv(sectionsCsv);
            cg.getSections().addAll(sections);
        }

        classMap.values().forEach(classRepo::save);
        return classMap.size();
    }

    // ---------------- LABS ----------------
    private int importLabs(Workbook wb, String schoolId) {
        Sheet sheet = wb.getSheet("LabRooms");
        if (sheet == null) return 0;

        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            LabRoom lab = new LabRoom();
            lab.setId(getStr(r, 0));
            lab.setSchoolId(schoolId);
            lab.setName(getStr(r, 1));
            lab.setCapacity((int) getNum(r, 2, 30));
            lab.setSubjectType("GENERAL");

            labRepo.save(lab);
            count++;
        }
        return count;
    }

    // ---------------- CLASS SUBJECTS ----------------
    private int importClassSubjects(Workbook wb, String schoolId) {
        Sheet sheet = wb.getSheet("ClassSubjects");
        if (sheet == null) return 0;

        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            String classId = getStr(r, 0);
            String subjectId = getStr(r, 1);
            String teacherId = getStr(r, 2);

            ClassSubject cs = new ClassSubject();
            cs.setId(classId + "_" + subjectId);
            cs.setSchoolId(schoolId);
            cs.setClassGroupId(classId);
            cs.setSubjectId(subjectId);
            cs.setTeacherId(teacherId);

            cs.setPeriodsPerWeek((int) getNum(r, 3, 0));
            cs.setRoomType(parseRoomType(getStr(r, 4)));

            String consecutiveStr = getStr(r, 5);
            boolean requiresConsecutive =
                    "Yes".equalsIgnoreCase(consecutiveStr) ||
                            "True".equalsIgnoreCase(consecutiveStr);
            cs.setRequiresConsecutive(requiresConsecutive);
            cs.setConsecutiveSize(requiresConsecutive ? 2 : 0);

            classSubjectRepo.save(cs);
            count++;
        }
        return count;
    }

    // ---------------- HELPERS ----------------
    private String getStr(Row r, int i) {
        Cell c = r.getCell(i);
        if (c == null) return null;
        switch (c.getCellType()) {
            case STRING: return c.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int) c.getNumericCellValue());
            case BOOLEAN: return String.valueOf(c.getBooleanCellValue());
            default: return c.toString().trim();
        }
    }

    private double getNum(Row r, int i, double def) {
        Cell c = r.getCell(i);
        if (c == null) return def;
        try {
            if (c.getCellType() == CellType.NUMERIC) {
                return c.getNumericCellValue();
            } else {
                return Double.parseDouble(c.toString().trim());
            }
        } catch (Exception e) {
            return def;
        }
    }

    private List<String> csv(String v) {
        return v == null ? List.of()
                : Arrays.stream(v.split(",")).map(String::trim).toList();
    }

    private RoomType parseRoomType(String v) {
        if (v == null) return RoomType.CLASSROOM;
        try {
            return RoomType.valueOf(v.trim().toUpperCase());
        } catch (Exception e) {
            return RoomType.CLASSROOM;
        }
    }
}