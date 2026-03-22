package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.entity.MaintenanceBill;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class PdfService {
    public byte[] generateMaintenanceReceipt(MaintenanceBill bill) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("SOCIETY MAINTENANCE RECEIPT").setBold().setFontSize(18));
        document.add(new Paragraph("Receipt ID: #REC-" + bill.getId()));
        document.add(new Paragraph("Date: " + LocalDate.now()));
        document.add(new Paragraph("--------------------------------------------"));
        document.add(new Paragraph("Resident Name: " + bill.getUser().getFullName()));
        document.add(new Paragraph("Flat Number: " + bill.getFlat().getFlatNumber()));
        document.add(new Paragraph("Billing Month: " + bill.getMonth()));
        document.add(new Paragraph("Amount Paid: ₹" + bill.getAmount()));
        document.add(new Paragraph("Status: PAID").setFontColor(com.itextpdf.kernel.colors.ColorConstants.GREEN));
        document.add(new Paragraph("--------------------------------------------"));
        document.add(new Paragraph("Thank you for your payment!"));

        document.close();
        return baos.toByteArray();
    }
}