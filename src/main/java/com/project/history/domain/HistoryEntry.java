package com.project.history.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistoryEntry {
	private Long id;
	private Long userId;
	private Long reservationId;
	private Long tradeId;
	private String action;   // e.g., TRADE_CREATED, TRADE_APPROVED, TRADE_REJECTED
	private String detail;   // optional description
	private LocalDateTime createdAt;
}

