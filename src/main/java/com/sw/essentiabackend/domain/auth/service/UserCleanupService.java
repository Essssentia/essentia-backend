package com.sw.essentiabackend.domain.auth.service;

import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.auth.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    // 매일 자정(00:00)에 실행되는 스케줄러
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupResignedUsers() {
        // 30일 이상 지난 탈퇴 사용자들을 찾기
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<User> resignedUsers = userRepository.findAllByResignedAtBefore(thirtyDaysAgo);
        userRepository.deleteAll(resignedUsers);
    }
}