package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.entity.Flat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportResidentDirectory(List<Flat> flats) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Resident Directory");

            // 1. Create Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // 2. Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Flat Number", "Wing", "Floor", "Resident Name", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. Fill Data Rows
            int rowIdx = 1;
            for (Flat flat : flats) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(flat.getFlatNumber());
                row.createCell(1).setCellValue(flat.getWing());
                row.createCell(2).setCellValue(flat.getFloor());
                row.createCell(3).setCellValue(flat.getOwner() != null ? flat.getOwner().getFullName() : "N/A");
                row.createCell(4).setCellValue(flat.getStatus().toString());
            }

            // Auto-size columns for better look
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
