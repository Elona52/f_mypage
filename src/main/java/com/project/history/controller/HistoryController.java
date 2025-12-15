package com.project.history.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.history.domain.HistoryEntry;
import com.project.history.service.HistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<HistoryEntry>> getByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(historyService.getByUser(userId));
	}

	@GetMapping("/reservation/{reservationId}")
	public ResponseEntity<List<HistoryEntry>> getByReservation(@PathVariable Long reservationId) {
		return ResponseEntity.ok(historyService.getByReservation(reservationId));
	}

	@GetMapping("/trade/{tradeId}")
	public ResponseEntity<List<HistoryEntry>> getByTrade(@PathVariable Long tradeId) {
		return ResponseEntity.ok(historyService.getByTrade(tradeId));
	}
}

