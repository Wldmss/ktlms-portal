package com.kt.ktedu.common.user.service;

import com.kt.ktedu.common.user.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 로그인 사용자의 부가 프로필 조회 서비스.
 * <p>
 * JWT 에 담지 않는 프로필성/민감 정보(이메일, 연락처, 직급, 부서 등)를 userId 로 DB 조회한다.
 * (session-to-jwt-guide.md 2절 C, 4.3 예시 참고)
 * <p>
 * 아직 DB 미연결 상태라 mapper 주입/실제 조회는 주석 처리하고 stub 을 반환한다.
 * DB 준비되면 mapper 주석을 풀고 stub 을 제거한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

//    private final UserProfileMapper userProfileMapper;

    /**
     * userId 로 부가 프로필 조회.
     * DB 연결 후: return userProfileMapper.findByUserId(userId);
     */
    public UserProfileDTO getByUserId(String userId) {
        // TODO: DB 연결 후 mapper 조회로 교체
        // return userProfileMapper.findByUserId(userId);

        log.debug("[stub] UserProfileService.getByUserId - userId: {}", userId);
        return UserProfileDTO.builder()
                .userId(userId)
                .build();
    }
}
