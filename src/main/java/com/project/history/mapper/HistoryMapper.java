package com.project.history.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.history.domain.HistoryEntry;

@Mapper
public interface HistoryMapper {

	int insert(HistoryEntry entry);

	List<HistoryEntry> findByUser(Long userId);

	List<HistoryEntry> findByReservation(Long reservationId);

	List<HistoryEntry> findByTrade(Long tradeId);
}

