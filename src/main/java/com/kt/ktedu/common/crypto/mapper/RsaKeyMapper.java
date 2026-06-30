package com.kt.ktedu.common.crypto.mapper;

import com.kt.ktedu.common.crypto.dto.RsaKeyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RsaKeyMapper {

    /** RSA 키 쌍 일괄 저장 */
    void insertList(@Param("list") List<RsaKeyDTO> keyList);

    /** keySeq 로 단건 조회 */
    RsaKeyDTO findByKeySeq(@Param("keySeq") Integer keySeq);

    /** 저장된 키 총 개수 */
    long countAll();
}
