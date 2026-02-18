package com.internship.management.services;

import com.internship.management.interfaces.ChartInterface;
import com.internship.management.interfaces.DepartmentInternshipStat;
import com.internship.management.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipChartServiceImpl implements ChartInterface {

    private final StudentRepository studentRepository;

    public ByteArrayInputStream exportInternshipsByDepartment() throws IOException {
        List<DepartmentInternshipStat> stats = studentRepository.countInternsByDepartment();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Internships by sector");

            // En-tête
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Sector");
            header.createCell(1).setCellValue("Number of students on internship");

            // Données
            int rowNum = 1;
            for (DepartmentInternshipStat stat : stats) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat.getDepartment());
                row.createCell(1).setCellValue(stat.getCount());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<DepartmentInternshipStat> getInternshipsByDepartment() {
        return studentRepository.countInternsByDepartment();
    }
}
