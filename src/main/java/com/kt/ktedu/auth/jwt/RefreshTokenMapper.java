package com.kt.ktedu.auth.jwt;

import com.kt.ktedu.auth.jwt.dto.RefreshTokenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RefreshTokenMapper {

    /**
     * Refresh Token 저장 (기기/세션별 신규 1건 추가)
     */
    void insert(RefreshTokenDTO refreshToken);

    /**
     * userId + tokenId 로 단건 조회 (refresh rotation 대상 검증용)
     */
    RefreshTokenDTO findByUserIdAndTokenId(@Param("userId") String userId, @Param("tokenId") String tokenId);

    /**
     * userId 로 전체 세션(기기) 목록 조회
     */
    List<RefreshTokenDTO> findAllByUserId(@Param("userId") String userId);

    /**
     * userId + tokenId 로 단건 삭제 (rotation 시 기존 토큰 제거, 단일 기기 로그아웃)
     */
    int deleteByUserIdAndTokenId(@Param("userId") String userId, @Param("tokenId") String tokenId);

    /** userId 로 전체 세션 삭제 (탈취 감지 시 전체 세션 무효화, 전체 기기 로그아웃) */
    int deleteByUserId(@Param("userId") String userId);

    /** 만료된 토큰 일괄 삭제 (배치용) */
    void deleteExpired();
}
