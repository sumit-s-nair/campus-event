package com.example.campuseventplatform.dto;

import com.example.campuseventplatform.model.EventStatus;

public class StatusUpdateDTO {
    private EventStatus status;
    private Long requestingUserId;

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Long getRequestingUserId() {
        return requestingUserId;
    }

    public void setRequestingUserId(Long requestingUserId) {
        this.requestingUserId = requestingUserId;
    }
}
