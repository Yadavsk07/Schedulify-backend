package com.example.schedulify.controller;

import com.example.schedulify.model.School;
import com.example.schedulify.model.Teacher;
import com.example.schedulify.repository.SchoolRepository;
import com.example.schedulify.repository.TeacherRepository;
import com.example.schedulify.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final SchoolRepository schoolRepo;
    private final TeacherRepository teacherRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(SchoolRepository schoolRepo,
                          TeacherRepository teacherRepo,
                          JwtUtil jwtUtil) {
        this.schoolRepo = schoolRepo;
        this.teacherRepo = teacherRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/admin/register")
    public ResponseEntity<?> registerSchool(@RequestBody Map<String,String> body) {
        String name = body.get("name");
        String schoolCode = body.get("schoolCode");
        String adminEmail = body.get("email");
        String password = body.get("password");
        if (name == null || adminEmail == null || password == null || schoolCode == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing fields"));
        }
        School s = new School();
        s.setName(name);
        s.setSchoolCode(schoolCode);
        s.setAdminEmail(adminEmail);
        s.setPasswordHash(encoder.encode(password));
        schoolRepo.save(s);
        return ResponseEntity.ok(Map.of("schoolId", s.getId()));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String,String> body) {
        String schoolCode = body.get("schoolCode");
        String password = body.get("password");
        if (schoolCode == null || password == null) return ResponseEntity.badRequest().body(Map.of("error","Missing"));
        Optional<School> os = schoolRepo.findBySchoolCode(schoolCode);
        if (os.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        School s = os.get();
        if (!encoder.matches(password, s.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put("role","ADMIN");
        claims.put("schoolId", s.getId());
        String token = jwtUtil.generate(claims, s.getSchoolCode());
        return ResponseEntity.ok(Map.of("token", token, "schoolId", s.getId(), "schoolName", s.getName()));
    }

    @PostMapping("/teacher/login")
    public ResponseEntity<?> teacherLogin(@RequestBody Map<String,String> body) {
        String schoolCode = body.get("schoolCode");
        String teacherId = body.get("teacherId");
        if (schoolCode == null || teacherId == null) return ResponseEntity.badRequest().body(Map.of("error","Missing"));
        Optional<School> os = schoolRepo.findBySchoolCode(schoolCode);
        if (os.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","Invalid school code"));
        School s = os.get();
        Optional<Teacher> ot = teacherRepo.findBySchoolIdAndId(s.getId(), teacherId);
        if (ot.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","Invalid teacher code"));
        Teacher t = ot.get();
        Map<String,Object> claims = new HashMap<>();
        claims.put("role","TEACHER");
        claims.put("schoolId", s.getId());
        claims.put("teacherId", t.getId());
        String token = jwtUtil.generate(claims, t.getTeacherCode());
        return ResponseEntity.ok(Map.of("token", token, "teacherId", t.getId(), "teacherName", t.getName()));
    }
}
