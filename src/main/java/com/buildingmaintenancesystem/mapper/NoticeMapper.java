package com.buildingmaintenancesystem.mapper;

import com.buildingmaintenancesystem.dto.NoticeRequest;
import com.buildingmaintenancesystem.dto.NoticeResponseDTO;
import com.buildingmaintenancesystem.entity.Notice;
import org.springframework.stereotype.Component;

@Component
public class NoticeMapper {
    public Notice toEntity(NoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        return notice;
    }
    public NoticeResponseDTO toResponseDTO(Notice notice) {
        if (notice == null) return null;
        NoticeResponseDTO dto = new NoticeResponseDTO();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setCreatedDate(notice.getCreatedAt());
        return dto;
    }
}