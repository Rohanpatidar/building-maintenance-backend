package com.buildingmaintenancesystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 1. Send Simple Notification (For Notices & Bill Alerts)
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // 2. Send PDF Receipt (For Bill Payments)
    @Async
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] pdfBytes, String fileName)  {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            // Attach the PDF
            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        }catch (MessagingException e) {
            System.out.println("Sorry Not Sending Bill due to Some Issue Please go to website for downloading");
        }

    }
}