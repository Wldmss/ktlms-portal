package com.kt.ktedu.common.crypto.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsaKeyDTO {
    private Integer keySeq;
    private String  publicKey;
    private String  privateKey;  // GCM 으로 암호화된 상태로 저장
}
