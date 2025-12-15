package com.project.trade.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TradeRequest {
	private Long id;
	private Long reservationId;
	private Long sellerUserId;
	private Long buyerUserId;
	private Integer price;
	private String status; // PENDING, APPROVED, REJECTED
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

