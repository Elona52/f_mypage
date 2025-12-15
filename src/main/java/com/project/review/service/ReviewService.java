package com.project.review.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.review.domain.Review;
import com.project.review.mapper.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	
	private final ReviewMapper reviewMapper;

	public List<Review> getAllReviews() {
		return reviewMapper.findAll();
	}

	public Review getReview(int id) {
		return reviewMapper.findById(id);
	}

	public List<Review> getReviewByInstructor(int instructorId) {
		return reviewMapper.findByInstructor(instructorId);
	}

	public List<Review> getReviewByUser(int userId) {
		return reviewMapper.findByUser(userId);
	}

	public Review getReviewByReservation(int reservationId) {
		return reviewMapper.findByReservation(reservationId);
	}

	public int createReview(Review review) {
		return reviewMapper.insert(review);
	}

	public int updateReview(Review review) {
		return reviewMapper.update(review);
	}

	public int deleteReview(int id) {
		return reviewMapper.delete(id);
	}

}
