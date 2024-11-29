package com.sw.essentiabackend.domain.review.dto;

import com.sw.essentiabackend.domain.review.entity.Review;
import lombok.Getter;

import java.util.List;

@Getter
public class ReviewResponseDto {

    private Long id;
    private Long userId;
    private String title;
    private String fragranceType;
    private String content;
    private List<String> tags;
    private String reviewImageUrl;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.title = review.getTitle();
        this.fragranceType = review.getFragranceType();
        this.content = review.getContent();
        this.tags = review.getTags();
        this.reviewImageUrl = review.getReviewImageUrl();
    }
}