package com.buildingmaintenancesystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeResponseDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
}