package com.kt.ktedu.core.security.auth;

import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 로그인 시 DB 에서 유저 정보를 로드하는 서비스
 * <p>
 * TODO: LoginMapper (또는 기존 로그인 서비스) 주입 후 실제 DB 조회로 교체 필요
 *       - 레거시 사용자 테이블 컬럼명, 패스워드 암호화 방식 확인 후 구현
 */
@Deprecated
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // TODO: @Autowired private LoginMapper loginMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // TODO: DB 조회로 교체
        // LoginUserDTO user = loginMapper.findByUserId(userId);
        // if (user == null) throw new UsernameNotFoundException("존재하지 않는 사용자: " + userId);
        //
        // JwtDTO jwtDTO = JwtDTO.builder()
        //         .userId(user.getUserId())
        //         .userNm(user.getUserNm())
        //         .orgCd(user.getOrgCd())
        //         .comp(user.getComp())
        //         .role(user.getRole())
        //         .build();
        //
        // return new CustomUserDetails(jwtDTO, user.getPassword());

        // ── 임시 하드코딩 (개발 테스트용 - 어떤 값이든 로그인 허용) ──
        JwtDTO jwtDTO = JwtDTO.builder()
                .userId(userId)
                .userNm("테스트유저")
                .orgCd("1001")
                .comp("KT")
                .role("ROLE_ADMIN")
                .build();

        // BCrypt 인코딩된 "admin1234" - 비밀번호도 임시로 고정
        String encodedPassword = "$2a$10$TMiCvABsaQnS7ewf9SFdT.uBK5TylKlt1yls3JiVKlWuBo/hpxBbu";
        return new CustomUserDetails(jwtDTO, encodedPassword);
    }
}
