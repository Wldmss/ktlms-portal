package com.kt.ktedu.auth.login.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * MyBatis mapper migrated from genius src/conf/sql/usr/login/AdminAuth.xml.
 */
@Mapper
public interface AdminAuthMapper {
    List<Map<String, Object>> getAdminAuthList(Map<String, Object> params);

    String getSmsAuthCheck(Map<String, Object> params);

    int insertSmsAuth(Map<String, Object> params);

    int insertSdkSmsSend(Map<String, Object> params);

    int updateOptAfter(Map<String, Object> params);

    String getAdminAuth(Map<String, Object> params);

    int updateSmsAuthFail(Map<String, Object> params);

    String getDeptAuth(Map<String, Object> params);

    String getNewDeptAuth(Map<String, Object> params);

    String getTutorAuth(Map<String, Object> params);
}
