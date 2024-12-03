package com.sw.essentiabackend.domain.comment.entity;

import com.sw.essentiabackend.common.Timestamped;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    public Comment(User user, Review review, String content) {
        this.user = user;
        this.review = review;
        this.content = content;
    }

    public Comment(User user, Review review, String content, Comment parent) {
        this.user = user;
        this.review = review;
        this.content = content;
        this.parent = parent;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}