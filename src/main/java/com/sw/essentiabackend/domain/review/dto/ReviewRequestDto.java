package com.sw.essentiabackend.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    private String title;
    private String fragranceType;
    private String content;
    private List<String> tags;
    private String reviewImageUrl;
}