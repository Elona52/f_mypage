package com.project.review.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.review.domain.Review;

@Mapper
public interface ReviewMapper {

	public List<Review> findAll();
	
	public abstract Review findById(int id);
	
	public List<Review> findByInstructor(int instructorId);

	public List<Review> findByUser(int userId);

	public Review findByReservation(int reservationId);

	public int insert(Review review);

	public int update(Review review);

	public int delete(int id);

}
