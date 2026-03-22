package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.entity.Notice;
import com.buildingmaintenancesystem.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public Notice postNotice(Notice notice) {
        // Business Rule: You could add logic here to send an email notification later
        return noticeRepository.save(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new RuntimeException("Notice not found with ID: " + id);
        }
        noticeRepository.deleteById(id);
    }
}