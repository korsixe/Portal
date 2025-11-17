package com.mipt.portal.moderator.message;

import java.time.LocalDateTime;

import lombok.Data;

@Data

public class ModerationMessage {
    private Long id;
    private Long adId;
    private String moderatorEmail;
    private String action; // 'approve', 'reject', 'delete'
    private String reason;
    private LocalDateTime createdAt;

    public ModerationMessage() {}

    public ModerationMessage(Long adId, String moderatorEmail, String action, String reason) {
        this.adId = adId;
        this.moderatorEmail = moderatorEmail;
        this.action = action;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }
}