package com.project.trade.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.trade.domain.TradeRequest;
import com.project.trade.service.TradeRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeRequestController {

	private final TradeRequestService tradeRequestService;

	@GetMapping
	public ResponseEntity<List<TradeRequest>> getAll() {
		return ResponseEntity.ok(tradeRequestService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<TradeRequest> getById(@PathVariable Long id) {
		TradeRequest tradeRequest = tradeRequestService.getById(id);
		if (tradeRequest == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(tradeRequest);
	}

	/**
	 * 이용중인 수강권/강의 내역 페이지에서 사용자의 거래 신청 목록 조회
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<TradeRequest>> getByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(tradeRequestService.getByUser(userId));
	}

	@GetMapping("/reservation/{reservationId}")
	public ResponseEntity<List<TradeRequest>> getByReservation(@PathVariable Long reservationId) {
		return ResponseEntity.ok(tradeRequestService.getByReservation(reservationId));
	}

	/**
	 * 이용권 거래 신청 생성
	 */
	@PostMapping
	public ResponseEntity<?> createTradeRequest(@RequestBody TradeRequest tradeRequest) {
		tradeRequestService.create(tradeRequest);
		return ResponseEntity.ok("거래 신청이 등록되었습니다.");
	}

	/**
	 * 거래 상태 변경 (예: PENDING -> APPROVED/REJECTED)
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<?> updateStatus(
			@PathVariable Long id,
			@RequestParam String status) {
		if (!status.equals("PENDING") && !status.equals("APPROVED") && !status.equals("REJECTED")) {
			return ResponseEntity.badRequest().body("status 값은 PENDING/APPROVED/REJECTED만 허용됩니다.");
		}
		int updated = tradeRequestService.updateStatus(id, status);
		if (updated > 0) {
			return ResponseEntity.ok("거래 상태가 변경되었습니다: " + status);
		}
		return ResponseEntity.notFound().build();
	}
}

