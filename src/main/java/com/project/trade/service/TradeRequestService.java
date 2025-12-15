package com.project.trade.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.history.service.HistoryService;
import com.project.trade.domain.TradeRequest;
import com.project.trade.mapper.TradeRequestMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeRequestService {

	private final TradeRequestMapper tradeRequestMapper;
	private final HistoryService historyService;

	public List<TradeRequest> getAll() {
		return tradeRequestMapper.findAll();
	}

	public TradeRequest getById(Long id) {
		return tradeRequestMapper.findById(id);
	}

	public List<TradeRequest> getByUser(Long userId) {
		return tradeRequestMapper.findByUser(userId);
	}

	public List<TradeRequest> getByReservation(Long reservationId) {
		return tradeRequestMapper.findByReservation(reservationId);
	}

	public int create(TradeRequest tradeRequest) {
		// 기본 상태를 PENDING으로 설정
		if (tradeRequest.getStatus() == null || tradeRequest.getStatus().isEmpty()) {
			tradeRequest.setStatus("PENDING");
		}
		int inserted = tradeRequestMapper.insert(tradeRequest);
		// history 기록
		if (inserted > 0) {
			historyService.record(
					tradeRequest.getSellerUserId(), // 신청 주체를 seller로 간주
					tradeRequest.getReservationId(),
					tradeRequest.getId(),
					"TRADE_CREATED",
					"거래 신청이 생성되었습니다.");
		}
		return inserted;
	}

	public int updateStatus(Long id, String status) {
		int updated = tradeRequestMapper.updateStatus(id, status);
		if (updated > 0) {
			// trade 정보 재조회하여 기록
			TradeRequest trade = tradeRequestMapper.findById(id);
			if (trade != null) {
				historyService.record(
						trade.getSellerUserId(),
						trade.getReservationId(),
						id,
						"TRADE_STATUS_" + status,
						"거래 상태 변경: " + status);
			}
		}
		return updated;
	}
}

