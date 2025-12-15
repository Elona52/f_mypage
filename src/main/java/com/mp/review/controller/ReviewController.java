package com.mp.review.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mp.review.domain.Review;
import com.mp.review.service.ReviewService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") int id) {
        return reviewService.getReview(id);
    }

    @GetMapping("/instructor/{instructorId}")
    public List<Review> getByInstructor(@PathVariable("instructorId") int instructorId){
        return reviewService.getReviewByInstructor(instructorId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getByUser(@PathVariable("userId") int userId){
        return reviewService.getReviewByUser(userId);
    }

    @GetMapping("/reservation/{reservationId}")
    public Review getByReservation(@PathVariable("reservationId") int reservationId) {
        return reviewService.getReviewByReservation(reservationId);
    }

    @PostMapping
    public int createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping("/{id}")
    public int updateReview(@PathVariable("id") int id, @RequestBody Review review) {
        review.setId(id);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public int deleteReview(@PathVariable("id") int id) {
        return reviewService.deleteReview(id);
    }
}
