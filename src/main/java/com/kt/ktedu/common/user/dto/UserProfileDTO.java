package com.kt.ktedu.common.user.dto;

import lombok.*;

/**
 * 로그인 사용자의 "부가 프로필 정보".
 * <p>
 * JWT 에는 신원/권한 최소값(userId, userNm, comp, orgCd, role, adminGrade)만 담고,
 * 자주 안 바뀌거나 민감한 아래 값들은 토큰에 넣지 않고 userId 로 DB 조회해서 채운다.
 * (session-to-jwt-guide.md 2절 C 항목 참고)
 * <p>
 * 필드는 genius/lms 레거시 세션 키를 기준으로 잡았고, 주석에 대응 레거시 키를 남긴다.
 * DB 연결 후 실제 컬럼/쿼리에 맞춰 필요한 것만 남기고 정리한다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    /** 로그인 ID (조회 키) — 레거시 userid */
    private String userId;

    // --- 연락처 ---
    /** 이메일 — 레거시 email_id */
    private String email;
    /** 전화번호 — 레거시 hometel */
    private String phone;
    /**
     * 주민등록번호 (민감정보) — 레거시 resno.
     * 절대 JWT 에 담지 않으며, 조회 시에도 마스킹/암호화 정책을 반드시 적용한다.
     */
    private String resno;

    // --- 직함/직책/직급/직무 ---
    /** 직함명 — 레거시 title_nm */
    private String titleNm;
    /** 직책명 — 레거시 post_nm */
    private String postNm;
    /** 직급명 — 레거시 position_nm */
    private String positionNm;
    /** 직무 코드 — 레거시 jobcd */
    private String jobCd;
    /** 직무명 — 레거시 job_nm */
    private String jobNm;

    // --- 조직 ---
    /** 부서 코드 — 레거시 dept_cd */
    private String deptCd;
    /** 부서명 — 레거시 dept_nm */
    private String deptNm;
    /** 본부 조직코드 — 레거시 hqorgcd */
    private String hqOrgCd;
    /** 사업부/부문 코드 — 레거시 bonbu_cd */
    private String bonbuCd;
    /** 조직 전체 경로명 — 레거시 org_full_nm */
    private String orgFullNm;

    // --- 업무 부가 플래그 (메뉴 이관하며 실제 사용되는 것만 유지) ---
    /** 대행사 코드 — 레거시 agency_cd */
    private String agencyCd;
    /** 경력 구분 — 레거시 gyungryuk */
    private String gyungryuk;
    /** HRDC 플래그 — 레거시 hrdc */
    private String hrdc;
    /** 매니저 여부(Y/N) — 레거시 mgr_yn */
    private String mgrYn;
    /** 관리자 여부(Y/N) — 레거시 admr_yn */
    private String admrYn;
    /** 퇴직 여부 — 레거시 isretire */
    private String isRetire;
}
