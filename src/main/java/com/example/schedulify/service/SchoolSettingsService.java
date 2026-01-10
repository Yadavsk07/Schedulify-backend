package com.example.schedulify.service;

import com.example.schedulify.model.School;
import com.example.schedulify.model.SchoolSettings;
import com.example.schedulify.repository.SchoolSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SchoolSettingsService {

    private final SchoolSettingsRepository repo;

    public SchoolSettingsService(SchoolSettingsRepository repo) {
        this.repo = repo;
    }

    // MAIN METHOD - always used to fetch settings
    public SchoolSettings getSettingsBySchool(String schoolId) {
        return repo.findBySchoolId(schoolId)
                .orElseGet(() -> createDefault(schoolId));
    }

    // Alias used by timetable generator
    public SchoolSettings getSettings(String schoolId) {
        return getSettingsBySchool(schoolId);
    }

    // Create default settings for a school
    private SchoolSettings createDefault(String schoolId) {
        SchoolSettings s = new SchoolSettings();

        s.setSchoolId(schoolId);
        s.setWorkingDays(Arrays.asList("MON", "TUE", "WED", "THU", "FRI"));
        s.setPeriodsPerDay(8);
        s.setTotalDaysPerWeek(5);

        s.setHasMorningAssembly(true);
        s.setAssemblySlot(0);
        s.setSaturdayHalfDay(false);

        return repo.save(s);
    }

    // Update all settings for a school
    public SchoolSettings updateSettings(String schoolId, SchoolSettings newSettings) {

        SchoolSettings existing = getSettingsBySchool(schoolId);

        existing.setWorkingDays(newSettings.getWorkingDays());
        existing.setPeriodsPerDay(newSettings.getPeriodsPerDay());
        existing.setTotalDaysPerWeek(newSettings.getTotalDaysPerWeek());

        existing.setHasMorningAssembly(newSettings.isHasMorningAssembly());
        existing.setAssemblySlot(newSettings.getAssemblySlot());
        existing.setSaturdayHalfDay(newSettings.isSaturdayHalfDay());

        return repo.save(existing);
    }

    // Reset to default
    public SchoolSettings resetToDefault(String schoolId) {
        return createDefault(schoolId);
    }



}
