package com.mp.review.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mp.review.domain.Review;
import com.mp.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewViewController {

    private final ReviewService reviewService;

    @GetMapping("/")
    public String index() {
        return "redirect:/reviews";
    }

    @GetMapping("/reviews")
    public String reviewList(Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "review-list";
    }

    @GetMapping("/reviews/{id}")
    public String reviewDetail(@PathVariable("id") int id, Model model) {
        Review review = reviewService.getReview(id);
        if (review == null) {
            return "redirect:/reviews";
        }
        model.addAttribute("review", review);
        return "review-detail";
    }
}

