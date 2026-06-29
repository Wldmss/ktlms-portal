package com.kt.ktedu.auth.ldap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/* ldap response */
public class LdapResponseDTO {
    private Response response;

    @Getter
    @Setter
    public static class Response {
        private Object returnresult;
    }

    private ReturnResult returnResult;

    @Getter
    @Setter
    public static class ReturnResult {
        private String isAuth;          // 인증여부 (TRUE/FALSE) AES256
        private String oldCN;           // 구사번(9자리) AES256
        private String pwdExpiredDate;  // 비밀번호 만료 일자 AES256
    }

    private String returncode;           // 결과 코드 (0: Fail, 1: Success)
    private String returndescription;    // 결과 설명 (Fail / Success)
    private String errorcode;            // 에러 코드 (returncode == 0 일 때만 할당)
    private String errordescription;     // 에러 설명
    private String transactionid;
    private String sequenceno;
}