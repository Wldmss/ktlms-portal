package com.kt.ktedu.auth.jwt;

import com.kt.ktedu.auth.jwt.dto.RefreshTokenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    /** Refresh Token 저장 (있으면 덮어쓰기) */
    void upsert(RefreshTokenDTO refreshToken);

    /** userId 로 조회 */
    RefreshTokenDTO findByUserId(@Param("userId") String userId);

    /** token 값으로 조회 */
    RefreshTokenDTO findByToken(@Param("token") String token);

    /** userId 로 삭제 (로그아웃) */
    void deleteByUserId(@Param("userId") String userId);

    /** 만료된 토큰 일괄 삭제 (배치용) */
    void deleteExpired();
}
