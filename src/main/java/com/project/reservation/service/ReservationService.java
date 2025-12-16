package com.project.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.reservation.domain.Reservation;
import com.project.reservation.mapper.ReservationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	
	private final ReservationMapper reservationMapper;

	public List<Reservation> getAllReservations() {
		return reservationMapper.findAll();
	}

	public Reservation getReservation(Long id) {
		return reservationMapper.findById(id);
	}

	public List<Reservation> getReservationsByUser(String userId) {
		return reservationMapper.findByUser(userId);
	}

	public List<Reservation> getReservationsByPaymentStatus(String paymentStatus) {
		return reservationMapper.findByPaymentStatus(paymentStatus);
	}

	public List<Reservation> getReservationsByUserAndPaymentStatus(String userId, String paymentStatus) {
		return reservationMapper.findByUserAndPaymentStatus(userId, paymentStatus);
	}

	/**
	 * 이용중인 수강권/강의내역 조회 (결제완료 상태인 예약만)
	 * @param userId 사용자 ID
	 * @return 결제완료 상태인 예약 목록
	 */
	public List<Reservation> getActiveReservationsByUser(String userId) {
		return reservationMapper.findByUserAndPaymentStatus(userId, "결제완료");
	}

	public int reserve(Reservation reservation) {
		// 상태코드가 지정되지 않으면 기본값 "예약완료" 설정
		if (reservation.getSttsCd() == null || reservation.getSttsCd().isEmpty()) {
			reservation.setSttsCd("예약완료");
		}
		return reservationMapper.insert(reservation);
	}

	public int updateReservation(Reservation reservation) {
		return reservationMapper.update(reservation);
	}

	public int updatePaymentStatus(Long id, String paymentStatus) {
		return reservationMapper.updatePaymentStatus(id, paymentStatus);
	}

	public int deleteReservation(Long id) {
		// 소프트 삭제 (논리적 삭제) - 상태코드를 '취소'로 변경
		return reservationMapper.softDelete(id);
	}

}
