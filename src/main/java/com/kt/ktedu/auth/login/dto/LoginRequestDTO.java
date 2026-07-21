package com.kt.ktedu.auth.login.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 웹 로그인 요청 DTO.
 *
 * <p>세션→Security/JWT 로 새로 작성하는 웹 로그인의 <b>컨트롤러 요청 경계</b>를 타입화한다.
 * 일반/cds/exam/폼 로그인의 모든 진입점이 이 DTO 로 바인딩되며, 서비스({@code processLogin})가
 * 최초 진입에서 {@link #toMap()} 으로 변환해 내부 Map 처리 로직으로 넘긴다.</p>
 *
 * <p>필드명은 현재 프론트가 보내는 wire 파라미터명(예: {@code userid})과 일치시켜
 * {@code @ModelAttribute} 바인딩이 되도록 한다. (앱 전용 DTO 와 별개 — 앱은 deviceToken/osType 등 다른 형태)</p>
 */
@Getter
@Setter
public class LoginRequestDTO {

    private String userid;      // 사번(로그인 ID)
    private String loginUserId; // userid 별칭(레거시 호환)
    private String pwd;         // 비밀번호 (encYn=Y 면 RSA 암호문)
    private String url;         // 로그인 후 이동 URL
    private String isDevice;    // Y/N (모바일 여부)
    private String encYn;       // Y: pwd 가 RSA 로 암호화됨
    private String p_ktfcheck;  // genius/nSSO 레거시 값: 조직 사용 여부가 0이어도 로그인 판정 통과
    private String p_crmlogin;  // genius CRM 연동 로그인 플래그: 일반 비밀번호 판정 후 CRM 성공 처리
    private String p_issso;     // nSSO(KATE) 로그인 플래그: session의 sso_id를 사용해 비밀번호 검사 생략
    private String mKate;       // genius 모바일 KATE 로그인에서 사용하던 구형 SSO 플래그
    private String issso;       // p_issso를 내부에서 복사해 사용하던 genius 호환 SSO 플래그
    private String p_userid;    // nSSO가 전달하는 사용자 ID: SSO 로그인에서는 session의 sso_id를 우선 사용
    private String target;      // 로그인 성공 후 nSSO 반환 분기 식별자(kate_return 등)
    private String return_page; // nSSO 반환 페이지 또는 반환 alias(nsso_return.jsp에서 해석)
    private String message;     // login front send message

    /**
     * 기존 Map 기반 서비스(processLogin) 호환용 변환.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userid", userid);
        map.put("loginUserId", loginUserId);
        map.put("pwd", pwd);
        map.put("url", url);
        map.put("isDevice", isDevice);
        map.put("encYn", encYn);
        map.put("p_ktfcheck", p_ktfcheck);
        map.put("p_crmlogin", p_crmlogin);
        map.put("p_issso", p_issso);
        map.put("mKate", mKate);
        map.put("issso", issso);
        map.put("p_userid", p_userid);
        map.put("target", target);
        map.put("return_page", return_page);
        map.put("message", message);
        return map;
    }
}
