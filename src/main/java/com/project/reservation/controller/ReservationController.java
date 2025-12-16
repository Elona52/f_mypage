package com.project.reservation.controller;

import com.project.reservation.domain.Reservation;
import com.project.reservation.service.ReservationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservation(id);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable String userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<Reservation>> getReservationsByPaymentStatus(
            @PathVariable String paymentStatus) {
        return ResponseEntity.ok(reservationService.getReservationsByPaymentStatus(paymentStatus));
    }

    @GetMapping("/user/{userId}/payment-status/{paymentStatus}")
    public ResponseEntity<List<Reservation>> getUserReservationsByPaymentStatus(
            @PathVariable String userId, 
            @PathVariable String paymentStatus) {
        return ResponseEntity.ok(reservationService.getReservationsByUserAndPaymentStatus(userId, paymentStatus));
    }

    /**
     * 이용중인 수강권/강의내역 조회
     * 결제완료 상태인 예약만 반환합니다.
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Reservation>> getActiveReservations(@PathVariable String userId) {
        return ResponseEntity.ok(reservationService.getActiveReservationsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<?> makeReservation(@RequestBody Reservation reservation) {
        reservationService.reserve(reservation);
        return ResponseEntity.ok("예약 완료");
    }

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus) {
        // 상태코드 유효성 검증 (필요한 상태코드 값으로 수정)
        // 예: "예약완료", "결제완료", "취소" 등
        
        int result = reservationService.updatePaymentStatus(id, paymentStatus);
        if (result > 0) {
            return ResponseEntity.ok("상태코드가 변경되었습니다: " + paymentStatus);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        int result = reservationService.deleteReservation(id);
        if (result > 0) {
            return ResponseEntity.ok("예약 삭제 완료");
        }
        return ResponseEntity.notFound().build();
    }
}

