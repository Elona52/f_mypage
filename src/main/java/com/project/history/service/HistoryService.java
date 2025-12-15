package com.project.history.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.history.domain.HistoryEntry;
import com.project.history.mapper.HistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryMapper historyMapper;

	public int record(Long userId, Long reservationId, Long tradeId, String action, String detail) {
		HistoryEntry entry = new HistoryEntry();
		entry.setUserId(userId);
		entry.setReservationId(reservationId);
		entry.setTradeId(tradeId);
		entry.setAction(action);
		entry.setDetail(detail);
		return historyMapper.insert(entry);
	}

	public List<HistoryEntry> getByUser(Long userId) {
		return historyMapper.findByUser(userId);
	}

	public List<HistoryEntry> getByReservation(Long reservationId) {
		return historyMapper.findByReservation(reservationId);
	}

	public List<HistoryEntry> getByTrade(Long tradeId) {
		return historyMapper.findByTrade(tradeId);
	}
}

