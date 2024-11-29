package com.sw.essentiabackend.security;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 주어진 사용자 이름으로 사용자를 조회하여 UserDetails 객체 반환
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserDetailsImpl(user);
    }
}