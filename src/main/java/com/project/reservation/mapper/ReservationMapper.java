package com.project.reservation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.reservation.domain.Reservation;

@Mapper
public interface ReservationMapper {

	public List<Reservation> findAll();
	
	public Reservation findById(Long id);
	
	public List<Reservation> findByUser(String userId);

	public List<Reservation> findByPaymentStatus(String paymentStatus);

	public List<Reservation> findByUserAndPaymentStatus(String userId, String paymentStatus);

	public int insert(Reservation reservation);

	public int update(Reservation reservation);

	public int updatePaymentStatus(Long id, String paymentStatus);

	public int softDelete(Long id);

	public int delete(Long id);

}

