package com.kt.ktedu.auth.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * MyBatis mapper migrated from genius src/conf/sql/usr/login/Login.xml.
 */
@Mapper
public interface LoginMapper {
    Integer getIsMember(Map<String, Object> params);

    Integer getPossibleLoginStatus(Map<String, Object> params);

    Map<String, Object> getMemberInfo(Map<String, Object> params);

    Map<String, Object> getSendSmsInfo(Map<String, Object> params);

    Map<String, Object> getEmailInfo(Map<String, Object> params);

    int updateLoginCnt(Map<String, Object> params);

    int insertLoginLog(Map<String, Object> params);

    int updateLoginFailCnt(Map<String, Object> params);

    int updateLoginFailInit(Map<String, Object> params);

    Integer getLoginOptCnt(Map<String, Object> params);

    int insertLoginFailLog(Map<String, Object> params);

    Integer getIsEtcMember(Map<String, Object> params);

    Map<String, Object> getEtcMemberInfo(Map<String, Object> params);

    int updateConfirmPwd(Map<String, Object> params);

    int updateTnTestMeberPwyn(Map<String, Object> params);

    String getLdapOrgCd(Map<String, Object> params);

    Integer getCheckLoginIp(Map<String, Object> params);

    String getOtpLockCheck(Map<String, Object> params);

    Map<String, Object> getPopupInfo();

    Integer menuUrlChk(Map<String, Object> params);

    String getRsaPublicKey(Map<String, Object> params);

    String getRsaPrivateKey(Map<String, Object> params);

    String getLdapCompanyYn(@Param("comp") String comp);

    String getSmsBypassYn();

    int upsertUserSession(Map<String, Object> params);

    String getSessionIdByUser(Map<String, Object> params);

    int updateExpireLoginKey(@Param("userId") String userId, @Param("loginKey") String loginKey);
}
