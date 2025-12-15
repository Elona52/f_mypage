package com.mp.review.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Review {
    private int id;
    private int userId;
    private int instructorId;
    private int reservationId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}

