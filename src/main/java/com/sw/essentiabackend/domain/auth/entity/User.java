package com.sw.essentiabackend.domain.auth.entity;

import com.sw.essentiabackend.common.Timestamped;
import com.sw.essentiabackend.domain.profile.entity.Profile;
import com.sw.essentiabackend.domain.review.entity.Review;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 250)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private Long googleId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    // 추가: 관리자 승인 상태 필드
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    // 탈퇴 여부 및 탈퇴 시점 저장
    @Column(nullable = true)
    private LocalDateTime resignedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    // 기본 생성자
    public User(String username, String password, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.resignedAt = null;
    }

    // 구글 로그인 사용자 생성자
    public User(String username, String password, String email, UserRoleEnum role,Long googleId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.googleId = googleId;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.resignedAt = null;
    }

    // 관리자로 승격하는 메서드
    public void approveAdmin() {
        this.role = UserRoleEnum.ADMIN;
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    // 승인 거부하는 메서드
    public void rejectAdmin() {
        this.approvalStatus = ApprovalStatus.REJECTED;
    }

    // 탈퇴 처리 메서드 (탈퇴 시점 기록)
    public void resign() {
        this.resignedAt = LocalDateTime.now();
    }

    // 탈퇴 여부 확인
    public boolean isResigned() {
        return this.resignedAt != null;
    }

    // 구글 ID 업데이트 메서드
    public User googleIdUpdate(Long googleId) {
        this.googleId = googleId;
        return this; // 메서드 체이닝을 위해 this 반환
    }
}