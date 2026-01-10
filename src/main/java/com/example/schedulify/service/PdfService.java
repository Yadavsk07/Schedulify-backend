package com.example.schedulify.service;

import com.example.schedulify.dto.SlotDto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

@Service
public class PdfService {

    /* =====================================================
       TEACHER TIMETABLE PDF (UNCHANGED)
       ===================================================== */
    public byte[] generateTeacherTimetablePdf(
            String schoolName,
            String teacherName,
            Map<String, List<SlotDto>> timetable
    ) throws Exception {

        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph p = new Paragraph(schoolName + " — Teacher Timetable", title);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Teacher: " + teacherName));
        doc.add(new Paragraph(" "));

        for (String day : timetable.keySet()) {
            doc.add(new Paragraph(day, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

            PdfPTable table = new PdfPTable(new float[]{1, 4, 3});
            table.setWidthPercentage(100);

            addHeader(table, "Period");
            addHeader(table, "Subject");
            addHeader(table, "Class / Section");

            List<SlotDto> slots = timetable.get(day);
            slots.sort(Comparator.comparingInt(SlotDto::getPeriod));

            for (SlotDto s : slots) {
                table.addCell(String.valueOf(s.getPeriod() + 1));
                table.addCell(s.getSubjectName() != null ? s.getSubjectName() : "-");
                table.addCell(
                        (s.getClassGroupId() != null ? s.getClassGroupId() : "-") +
                                " / " +
                                (s.getSectionId() != null ? s.getSectionId() : "-")
                );
            }

            doc.add(table);
            doc.add(new Paragraph(" "));
        }

        doc.close();
        return baos.toByteArray();
    }

    /* =====================================================
       CLASS TIMETABLE PDF ✅ NEW
       ===================================================== */
    public byte[] generateClassTimetablePdf(
            String schoolName,
            String classId,
            String sectionId,
            Map<String, List<SlotDto>> timetable
    ) throws Exception {

        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph header = new Paragraph(
                schoolName + " — Class Timetable",
                title
        );
        header.setAlignment(Element.ALIGN_CENTER);
        doc.add(header);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Class: " + classId + "   Section: " + sectionId));
        doc.add(new Paragraph(" "));

        List<String> days = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT");
        int maxPeriods = 8;

        PdfPTable table = new PdfPTable(days.size() + 1);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 2, 2, 2, 2, 2});

        // Header row
        addHeader(table, "Period");
        for (String d : days) {
            addHeader(table, d);
        }

        // Body
        for (int period = 0; period < maxPeriods; period++) {
            table.addCell(String.valueOf(period + 1));

            for (String day : days) {
                SlotDto slot = findSlot(timetable, day, period);

                if (slot == null) {
                    table.addCell("-");
                } else {
                    PdfPCell cell = new PdfPCell();
                    cell.addElement(new Phrase(
                            slot.getSubjectName() != null ? slot.getSubjectName() : slot.getSubjectId(),
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)
                    ));
                    cell.addElement(new Phrase(
                            slot.getTeacherName() != null ? slot.getTeacherName() : slot.getTeacherId(),
                            FontFactory.getFont(FontFactory.HELVETICA, 8)
                    ));
                    if (slot.getLabRoomId() != null) {
                        cell.addElement(new Phrase(
                                "Lab: " + slot.getLabRoomId(),
                                FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC)
                        ));
                    }
                    table.addCell(cell);
                }
            }
        }

        doc.add(table);
        doc.close();
        return baos.toByteArray();
    }

    /* =====================================================
       HELPERS
       ===================================================== */
    private SlotDto findSlot(
            Map<String, List<SlotDto>> timetable,
            String day,
            int period
    ) {
        if (timetable == null || !timetable.containsKey(day)) return null;
        return timetable.get(day)
                .stream()
                .filter(s -> s.getPeriod() == period)
                .findFirst()
                .orElse(null);
    }

    private void addHeader(PdfPTable table, String text) {
        PdfPCell c = new PdfPCell(
                new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11))
        );
        c.setBackgroundColor(Color.LIGHT_GRAY);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(6);
        table.addCell(c);
    }
}
