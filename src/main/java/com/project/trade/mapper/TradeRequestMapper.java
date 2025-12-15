package com.project.trade.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.trade.domain.TradeRequest;

@Mapper
public interface TradeRequestMapper {

	List<TradeRequest> findAll();

	TradeRequest findById(Long id);

	List<TradeRequest> findByUser(Long userId);

	List<TradeRequest> findByReservation(Long reservationId);

	int insert(TradeRequest tradeRequest);

	int updateStatus(Long id, String status);
}

