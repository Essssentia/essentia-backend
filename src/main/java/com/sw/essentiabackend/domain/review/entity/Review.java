package com.sw.essentiabackend.domain.review.entity;

import com.sw.essentiabackend.common.Timestamped;
import com.sw.essentiabackend.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "reviews")
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String fragranceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = true)
    private String reviewImageUrl;

    public Review(User user, String title, String fragranceType, String content, List<String> tags,
        String reviewImageUrl) {
        this.user = user;
        this.title = title;
        this.fragranceType = fragranceType;
        this.content = content;
        this.tags = tags;
        this.reviewImageUrl = reviewImageUrl;
    }

    public void update(String title, String fragranceType, String content, List<String> tags,
        String reviewImageUrl) {
        this.title = title;
        this.fragranceType = fragranceType;
        this.content = content;
        this.tags = tags;
        this.reviewImageUrl = reviewImageUrl;
    }
}