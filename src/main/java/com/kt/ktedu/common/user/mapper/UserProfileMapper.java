package com.kt.ktedu.common.user.mapper;

import com.kt.ktedu.common.user.dto.UserProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 사용자 부가 프로필 조회 Mapper.
 * DB 연결 전까지 매핑 XML/쿼리는 미작성 상태이며, 시그니처(계약)만 정의한다.
 */
@Mapper
public interface UserProfileMapper {

    /** userId 로 사용자 부가 프로필 단건 조회 */
    UserProfileDTO findByUserId(@Param("userId") String userId);
}
