package com.sw.essentiabackend.domain.profile.entity;

import com.sw.essentiabackend.common.Timestamped;
import com.sw.essentiabackend.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "profiles")
public class Profile extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = true, length = 250)
    private String bio;

    @Column(nullable = true)
    private String profileImageUrl;

    public Profile(User user, String bio, String profileImageUrl) {
        this.user = user;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String bio, String profileImageUrl) {
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }
}