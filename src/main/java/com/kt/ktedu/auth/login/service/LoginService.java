package com.kt.ktedu.auth.login.service;

import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import com.kt.ktedu.auth.jwt.service.TokenService;
import com.kt.ktedu.auth.ldap.dto.LdapResultDTO;
import com.kt.ktedu.auth.login.dto.LoginDTO;
import com.kt.ktedu.auth.login.dto.LoginRequestDTO;
import com.kt.ktedu.auth.login.mapper.AdminAuthMapper;
import com.kt.ktedu.auth.login.mapper.LoginMapper;
import com.kt.ktedu.core.security.auth.SecurityUtil;
import com.kt.ktedu.common.crypto.util.RSAUtil;
import com.kt.ktedu.common.util.core.DateUtil;
import com.kt.ktedu.common.util.core.MapUtil;
import com.kt.ktedu.common.util.core.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.kt.ktedu.common.util.core.MapperUtil.callOrDefault;

import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

@Service("loginService")
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    public enum LoginFlow {
        DEFAULT,    // 일반 로그인
        CDS,        // 역량진단 결과조회
        EXAM        // 대량평가 결과조회
    }

    private static final Pattern PASSWORD_COMPLEXITY =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,}$");

    @Value("${url.portal.main}")
    private String mainUrl;

    @Value("${login.except.value:5}")
    private int loginExceptionValue;

    @Value("${login.except.isuse:true}")
    private boolean loginExceptIsUse;

    @Value("${server.type}")
    private String serverType;

    private final AuthenticationManager authenticationManager;
    private final LoginMapper loginMapper;
    private final AdminAuthMapper adminAuthMapper;
    private final TokenService tokenService;
    private final ConcurrentMap<String, String> fallbackOtpStore = new ConcurrentHashMap<>();

    /* 로그인 처리 */
    public Map<String, Object> processLogin(Map<String, Object> input,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            LoginFlow flow) {
        Map<String, Object> output = new LinkedHashMap<>();
        Map<String, Object> params = normalize(input);

        String userId = resolveLoginUserId(params, request);
        String password = MapUtil.firstNonBlank(params, "pwd", "loginPwd");

        boolean isKateLogin = checkMkateSsoLogin(params);  // mkate sso 여부
        boolean crmLogin = "Y".equalsIgnoreCase(MapUtil.valueAsString(params, "p_crmlogin"));   // crm 관련

        // 비밀번호 복호화
        if (!isKateLogin && "Y".equalsIgnoreCase(MapUtil.valueAsString(params, "encYn"))) {
            password = decryptLoginPassword(password, request);
        }

        // ID/PW black check
        if (StringUtil.isBlankParam(userId)) {
            return fail(output, -1, "ID 또는 PW가 잘못되었습니다.", defaultFailUrl(flow));
        }
        if (!isKateLogin && !crmLogin && StringUtil.isBlankParam(password)) {
            return fail(output, -1, "ID 또는 PW가 잘못되었습니다.", defaultFailUrl(flow));
        }

        params.put("userid", userId);
        params.put("pwd", password);
        params.put("lgip", request.getRemoteAddr());
        params.putIfAbsent("con_device", resolveDevice(request));
        params.putIfAbsent("isDevice", "M".equals(params.get("con_device")) ? "Y" : "N");

        String successRedirectUrl = resolveSuccessRedirect(params, request, flow);
        String failRedirectUrl = defaultFailUrl(flow);

        // 외부(경기도교육청 교사양성/KDT) 사용자 로그인
        params.put("etcCompCd", "8888");
        int isGgdEduOk = getIsEtcMember(params, userId);
        if (isGgdEduOk == 1) {
            return processEtcMemberLogin(params, userId, successRedirectUrl, request, response, failRedirectUrl);
        }

        Optional<Map<String, Object>> memberInfoResult = findMemberInfo(userId);
        if (memberInfoResult.isEmpty()) {
            // user 정보 없음
            return fail(output, -1, "ID 또는 PW가 잘못되었습니다.", failRedirectUrl);
        }
        Map<String, Object> memberInfo = memberInfoResult.get();

        /* member status check
         *  0 : 사용자의 조직이 미사용 조직
         *  1 : 성공
         * -1 : ID 없음
         * -3 : PW 틀림
         * -4 : 계정 잠김 (인증번호 5회 실패)
         * -6 : 탈퇴한 회원
         * -8 : ldap 체크 대상자이면서 PW 틀림
         * -9 : PW 만료
         * -999 : 미존재 계정
         */
        int isOk = isMember(params);
        String redirectMessage = null;
        String isTest = MapUtil.valueAsString(memberInfo, "isTest");

        if (isOk != 1) {
            // member 아님
            redirectMessage = "ID 또는 PW가 잘못되었습니다.";

            // mkate p_ktfcheck : 조직이 없어도 p_ktfcheck=Y 인 경우 PASS
            if (isKateLogin && isOk == 0 && "Y".equalsIgnoreCase(MapUtil.valueAsString(params, "p_ktfcheck"))) {
                isOk = 1;
                redirectMessage = null;
            }
        } else if (isLocked(memberInfo)) {
            // 계정 잠김
            isOk = -4;
            redirectMessage = "비밀번호 " + loginExceptionValue + "회 연속 오류로 계정잠금되어 로그인이 불가합니다. "
                    + "SMS 번호인증으로 계정잠금을 해제해 주시기 바랍니다. "
                    + "비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.";
        } else if ("Y".equalsIgnoreCase(MapUtil.valueAsString(memberInfo, "isretire"))) {
            // 탈퇴한 회원
            isOk = -6;
            redirectMessage = "ID 또는 PW가 잘못되었습니다.";
        } else if (!isKateLogin) {
            // ldap login
            LoginPasswordDecision decision = authenticatePassword(params, memberInfo, password);
            if (!decision.success()) {
                isOk = decision.status();
                redirectMessage = decision.message();
                if (isOk == -3 || isOk == -8 || isOk == -9) {
                    // PW 틀림 || PW 만료
                    params.put("lgfailcnt", parseInt(MapUtil.valueAsString(memberInfo, "lgfail"), 0) + 1);
                    updateLoginFailCnt(params);
                }
            }
        }

        // crm 관련 : pw 없어도 PASS
        if (crmLogin) {
            isOk = 1;
            redirectMessage = null;
        }

        // 로그인 실패 처리
        if (redirectMessage != null) {
            // 계정 잠금 처리
            if (isOk == -4) {
                output.put("lgFailCnt", loginExceptionValue);

                if (isSmsBypassEnabled()) {
                    // OTP 인증 bypass 인 경우 계정 잠금 해제 불가
                    output.put("otpError", "error");
                    redirectMessage = "비밀번호 " + loginExceptionValue + "회 연속 오류로 계정잠금되어 로그인이 불가합니다. " +
                            "현재 번호 인증이 되지 않는 관계로, 관리자에게 문의바랍니다. " +
                            "비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.";
                } else {
                    // 계정 잠금 시 해제
                    output.put("otpToken", tokenService.createLoginChallenge(
                            userId, JwtProvider.CHALLENGE_OTP, successRedirectUrl, true, request));
                }
            }
            insertLoginFailLog(params);
            return fail(output, normalizeStatus(isOk), redirectMessage, failRedirectUrl);
        }

        // 로그인 성공
        enrichMemberInfo(memberInfo, params, request, successRedirectUrl);
        updateLoginCnt(params, memberInfo);

        /* saml sso */
        String samlChannel = checkSAMLSession(request, memberInfo);
        if (!StringUtil.isBlankParam(samlChannel)) {
            successRedirectUrl = "/sso/" + samlChannel + "/login";
            params.put("loginPath", samlChannel);
            output.put("samlChannel", samlChannel);
            output.put("samlLoginUrl", successRedirectUrl);
        }

        /* kate sso */
        if (isKateLogin) {
            params.put("loginPath", "KATE");
            insertLoginLog(params);

            if (isKateReturn(params)) {
                output.put("target", "kate_return");
                output.put("return_page", MapUtil.valueAsString(params, "return_page"));
            }

            doJwtLogin(output, memberInfo, userId, successRedirectUrl, response);
            return success(output, 1, successRedirectUrl);
        }

        /* OTP 인증 우회 - test 계정, mode=cds,exam */
        if ("Y".equalsIgnoreCase(isTest) || flow != LoginFlow.DEFAULT) {
            params.put("loginPath", flow == LoginFlow.DEFAULT ? "GENIUS" : flow.name());
            doJwtLogin(output, memberInfo, userId, successRedirectUrl, response);
            insertLoginLog(params);
            return success(output, 1, successRedirectUrl);
        }

        /* 5분 내 로그인 없이 5회 초과 인증여부 */
        String lockCheck = StringUtil.defaultIfBlank(getOtpLockCheck(params), "S");
        if (!"S".equalsIgnoreCase(lockCheck)) {
            return fail(output, 1, "로그인 없이 5회 초과 인증 요청시 5분간 발송이 제한됩니다.", failRedirectUrl);
        }

        /* 오늘 동일한 IP에서 OTP 인증성공 내역 여부 */
        int ipCheck = getCheckLoginIp(params);
        if (ipCheck > 0 || isSmsBypassEnabled()) {
            params.put("loginPath", "GENIUS");
            doJwtLogin(output, memberInfo, userId, successRedirectUrl, response);
            insertLoginLog(params);
            return success(output, 1, successRedirectUrl);
        }

        /* 인증번호 발송 */
        output.put("otpToken", tokenService.createLoginChallenge(
                userId, JwtProvider.CHALLENGE_OTP, successRedirectUrl, false, request));
        output.put("result", "N");
        output.put("isOk", 1);
        output.put("otpCnt", ipCheck);
        output.put("userid", userId);
        output.put("message", "인증번호를 입력해주세요.");
        return output;
    }

    /* OTP 문자 발송 */
    public Map<String, Object> sendSmsOtp(Map<String, Object> input, HttpServletRequest request) {
        Map<String, Object> output = new LinkedHashMap<>();
        JwtProvider.LoginChallenge challenge;
        try {
            challenge = tokenService.verifyLoginChallenge(
                    MapUtil.firstNonBlank(input, "otpToken", "loginChallenge"),
                    JwtProvider.CHALLENGE_OTP,
                    request
            );
        } catch (Exception e) {
            output.put("message", "로그인 인증 정보가 만료되었습니다.\n처음부터 다시 진행해 주시기 바랍니다.");
            output.put("resultCode", "0");
            output.put("errorCode", "O");
            return output;
        }
        String userId = challenge.userId();

        Map<String, Object> params = normalize(input);
        params.put("userid", userId);
        params.put("s_userid", userId);
        params.put("lgIp", request.getRemoteAddr());

        String lockCheck = StringUtil.defaultIfBlank(getOtpLockCheck(params), "S");
        if (!"S".equalsIgnoreCase(lockCheck)) {
            output.put("message", "인증번호 5회 연속 발송되어 계정잠금되어 로그인이 불가합니다. 5분 후에 다시 시도하시기 바랍니다.");
            output.put("resultCode", "0");
            output.put("errorCode", "O");
            return output;
        }

        Map<String, Object> smsInfo = getSendSmsInfo(params).orElse(null);
        if (smsInfo == null) {
            output.put("message", "휴대전화 정보가 존재하지 않습니다.\n조직별 인사담당자에게 문의 부탁드립니다.");
            output.put("resultCode", "0");
            return output;
        }
        String serial = makeSerial();
        params.put("serial", serial);
        params.put("opt", "N");
        params.put("smsMsg", "[지니어스] 인증번호는 " + serial + "입니다. 3분 내에 입력해 주세요. (문의:1588-3391)");
        params.put("sender", "15883391");
        params.put("destInfo", MapUtil.valueAsString(smsInfo, "name") + "^" + StringUtil.normalizePhone(MapUtil.valueAsString(smsInfo, "handphone")));
        params.put("reserved1", "N");
        params.put("reserved2", "N");

        boolean fallbackSms = false;
        try {
            adminAuthMapper.insertSmsAuth(params);
        } catch (Exception e) {
            // DB 미연동 개발 환경용 OTP 저장소
            fallbackOtpStore.put(userId, serial);
            fallbackSms = true;
        }

        String maskedPhone = StringUtil.maskCellPhone(MapUtil.valueAsString(smsInfo, "handphone"));
        if (maskedPhone.length() > 9) {
            output.put("resultCode", "1");
            output.put("message", "시스템에 등록된 휴대전화 " + StringUtil.defaultIfBlank(maskedPhone, "") + "(으)로 인증번호가\n발송되었습니다. 3분 내로 인증번호를 입력해 주세요.");
            if (isLocalLike() || fallbackSms) {
                output.put("serial", serial);
            }
            output.put("otpToken", MapUtil.firstNonBlank(input, "otpToken", "loginChallenge"));
        } else {
            output.put("message", "휴대전화 정보가 존재하지 않습니다.\n\n조직별 인사담당자에게 문의 부탁드립니다.");
            output.put("resultCode", "0");
        }

        return output;
    }

    /* OTP 확인 */
    public Map<String, Object> checkSmsOtp(Map<String, Object> input,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        Map<String, Object> output = new LinkedHashMap<>();
        JwtProvider.LoginChallenge challenge;
        try {
            challenge = tokenService.verifyLoginChallenge(
                    MapUtil.firstNonBlank(input, "otpToken", "loginChallenge"),
                    JwtProvider.CHALLENGE_OTP,
                    request
            );
        } catch (Exception e) {
            output.put("message", "로그인 인증 정보가 만료되었습니다.\n처음부터 다시 진행해 주시기 바랍니다.");
            output.put("resultCode", "0");
            return output;
        }
        String userId = challenge.userId();
        String serial = MapUtil.valueAsString(input, "serial");

        if (StringUtil.isBlankParam(serial)) {
            output.put("message", "인증번호를 입력해 주세요.");
            output.put("resultCode", "0");
            return output;
        }

        Map<String, Object> params = normalize(input);
        params.put("s_userid", userId);
        params.put("userid", userId);
        params.put("serial", serial);
        String check;
        try {
            check = adminAuthMapper.getSmsAuthCheck(params);
        } catch (Exception e) {
            check = serial.equals(fallbackOtpStore.get(userId)) ? "Y" : "N";
            if ("Y".equals(check)) {
                fallbackOtpStore.remove(userId);
            }
        }

        if ("Y".equalsIgnoreCase(check)) {
            callOrDefault(() -> loginMapper.updateLoginFailInit(params), 0);
            callOrDefault(() -> adminAuthMapper.updateOptAfter(params), 0);

            if (challenge.accountLocked()) {
                output.put("message", "인증되었습니다. 계정잠금이 해제되었으니 다시 로그인해 주세요.\n"
                        + "비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.");
                output.put("resultCode", "2");
                return output;
            }

            Map<String, Object> memberInfo = requireMemberInfo(userId);
            String successRedirectUrl = StringUtil.defaultIfBlank(challenge.resultUrl(), mainUrl);
            params.put("loginPath", "GENIUS");
            params.put("con_device", resolveDevice(request));
            params.put("lgip", request.getRemoteAddr());

            doJwtLogin(output, memberInfo, userId, successRedirectUrl, response);
            insertLoginLog(params);

            output.put("message", "");
            output.put("resultCode", "1");
            output.put("resultUrl", successRedirectUrl);
            return output;
        }

        if ("F".equalsIgnoreCase(check)) {
            output.put("message", "SMS 인증번호 입력 횟수(5회)를 초과했습니다.\n처음부터 다시 진행해 주시기 바랍니다.");
            output.put("resultCode", "0");
        } else if ("T".equalsIgnoreCase(check)) {
            output.put("message", "SMS 인증번호 입력 대기시간이 3분을 초과했습니다.\n처음부터 다시 진행해 주시기 바랍니다.");
            output.put("resultCode", "0");
        } else {
            output.put("message", "발송된 SMS 인증번호와 입력하신 SMS 인증번호가 일치하지 않습니다.");
            output.put("resultCode", "-1");
            callOrDefault(() -> adminAuthMapper.updateSmsAuthFail(params), 0);
        }
        return output;
    }

    /* 인증성공시 TZ_SMSAUTH 테이블 정보 UPDATE TODO 사용하는지 check - pub 에만 존재 */
    @Deprecated
    public Map<String, Object> updateOptInfo(Map<String, Object> input) {
        Map<String, Object> output = new LinkedHashMap<>();
        int check = callOrDefault(() -> adminAuthMapper.updateOptAfter(normalize(input)), 0);
        if (check > 0) {
            String flag = MapUtil.firstNonBlank(input, "flag");
            output.put("error", "1".equals(flag) ? "1" : "2".equals(flag) ? "2" : "-1");
        } else {
            output.put("error", "-1");
        }
        return output;
    }

    /* 비밀번호 업데이트 - 외부 조직 */
    public Map<String, Object> confirmPassword(Map<String, Object> input) {
        Map<String, Object> output = new LinkedHashMap<>();
        Map<String, Object> params = normalize(input);

        JwtDTO currentUser = SecurityUtil.getCurrentUserOrNull();
        String currentUserId = currentUser == null ? null : currentUser.getUserId();
        if (StringUtil.isBlankParam(currentUserId)) {
            output.put("error", "0");
            return output;
        }

        params.put("userid", currentUserId);
        params.put("s_userid", currentUserId);
        params.put("etcCompCd", "8888");
        String currentPassword = MapUtil.valueAsString(params, "current_pwd");
        String newPassword = MapUtil.valueAsString(params, "conf_pwd");

        if (StringUtil.isBlankParam(currentPassword) || StringUtil.isBlankParam(newPassword)) {
            output.put("error", "0");
            return output;
        }

        // 외부 사용자 check
        int isEtc = getIsEtcMember(params, currentUserId);
        Map<String, Object> etcMemberInfo = isEtc == 1
                ? findEtcMemberInfo(params, currentUserId)
                : null;

        if (etcMemberInfo == null) {
            output.put("error", "0");
            return output;
        }

        String storedPassword = MapUtil.valueAsString(etcMemberInfo, "pwd", "PWD");
        if (!currentPassword.equals(storedPassword)) {
            output.put("error", "4");
            return output;
        }

        // 기존 비밀번호와 변경 비밀번호 동일한지 check
        if (newPassword.equals(storedPassword)) {
            output.put("error", "2");
            return output;
        }

        // 비밀번호 복잡성 체크 (최소 8자리, 문자/숫자/특수문자 포함)
        if (!PASSWORD_COMPLEXITY.matcher(newPassword).find()) {
            output.put("error", "3");
            return output;
        }

        params.put("s_pwd", newPassword);
        callOrDefault(() -> loginMapper.updateConfirmPwd(params), 0);   // 비번 update
        callOrDefault(() -> loginMapper.updateTnTestMeberPwyn(params), 0);  // 구분 update
        output.put("error", "1");
        return output;
    }

    /* 사용자용 관리자 페이지  TODO 미사용 */
    @Deprecated
    public Map<String, Object> testSendAdminSmsAuthCheck(Map<String, Object> input, HttpServletRequest request) {
        Map<String, Object> output = new LinkedHashMap<>();
        String auth = callOrDefault(() -> adminAuthMapper.getAdminAuth(normalize(input)), "");
        if (!StringUtil.isBlankParam(auth)) {
            output.put("message", "");
            output.put("error", "1");
            request.getSession(true).setAttribute("gadmin", MapUtil.firstNonBlank(input, "p_auth"));
            request.getSession(true).setAttribute("gadmin_open", "A");
        } else {
            output.put("message", "선택하신 관리자에 대한 권한이 없습니다. 다시 시도해 주세요.\n같은 문제가 계속된다면 관리자에게 문의바랍니다.");
            output.put("error", "-2");
        }
        return output;
    }

    /* 중복 세션 전체 로그아웃 */
    public Map<String, Object> forceLogin(Map<String, Object> input,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        Map<String, Object> output = new LinkedHashMap<>();
        JwtDTO currentUser = SecurityUtil.getCurrentUserOrNull();
        if (currentUser == null || StringUtil.isBlankParam(currentUser.getUserId())) {
            output.put("result", "N");
            output.put("message", "로그인 후 다시 시도해 주세요.");
            return output;
        }
        String userId = currentUser.getUserId();
        try {
            tokenService.revokeAll(userId);

            Map<String, Object> memberInfo = requireMemberInfo(userId);
            String requestedUrl = MapUtil.firstNonBlank(input, "resultUrl", "url");
            String resultUrl = StringUtil.defaultIfBlank(resolveSafeRedirect(requestedUrl), mainUrl);

            doJwtLogin(output, memberInfo, userId, resultUrl, response);
            output.put("result", "Y");
            output.put("resultUrl", resultUrl);
        } catch (Exception e) {
            log.warn("force login failed. userId={}, reason={}", userId, e.getMessage());
            output.put("result", "N");
        }
        return output;
    }

    /* OTP 인증 PASS 여부 */
    public boolean isSmsBypassEnabled() {
        return "Y".equalsIgnoreCase(callOrDefault(loginMapper::getSmsBypassYn, "N"));
    }

    /* 앱 로그아웃 시 기기 자동로그인 키를 만료처리 */
    public void updateExpireLoginKey(String userId) {
        String loginKey = "EXPIRE" + userId + DateUtil.now(DateUtil.PATTERN_YMDHMS);
        loginMapper.updateExpireLoginKey(userId, loginKey);
    }

    /* user 존재 여부 */
    public boolean isUserIdExists(String userId) {
        if (StringUtil.isBlankParam(userId)) {
            return false;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userid", userId);
        return isMember(params) == 1;
    }

    /**
     * 사번(userId)으로 SAML NameID 용 이메일을 조회한다.
     * (genius {@code OpenSAMLController} 의 {@code loginService.getEmailInfo(userid)} 이관 —
     * Udemy/Ping 등 SP 는 이메일을 식별자로 요구하므로 사번이 아닌 실제 이메일을 넘겨야 한다.)
     *
     * @return 이메일. 미조회/DB 미연동 시 {@code null}
     */
    public String getUserEmail(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("userid", userId);
        Map<String, Object> emailInfo = callOrDefault(() -> loginMapper.getEmailInfo(param), null);
        return emailInfo == null ? null : MapUtil.valueAsString(emailInfo, "email", "EMAIL");
    }

    /**
     * SSO 왕복(세션 캐리오버)용 안전한 내부 redirect 경로 해석.
     * 오픈리다이렉트 가드({@link #isAllowedRedirect})를 통과하면 그대로, 아니면 {@code null}.
     * (Entra 등 SSO 는 로그인 시작 시점의 {@code url} 을 세션에 보관했다가 성공 후 이 경로로 복귀)
     */
    public String resolveSafeRedirect(String requested) {
        return isAllowedRedirect(requested) ? requested : null;
    }

    /* 공지 내용 가져오기 */
    public Map<String, Object> getPopupInfo() {
        return callOrDefault(loginMapper::getPopupInfo, Map.of());
    }

    /* login public key */
    public String getRsaPublicKey(Map<String, Object> input) {
        return callOrDefault(() -> loginMapper.getRsaPublicKey(normalize(input)), null);
    }

    /*------------------private function------------------*/

    /**
     * 프론트(JSEncrypt)가 공개키로 암호화한 비밀번호를,
     * getEncKeyAjax 시 세션에 저장한 s_encSeq 로 개인키를 찾아 복호화한다.
     * (세션/키가 없거나 복호화 실패 시 null → 상위에서 인증 실패 처리)
     */
    private String decryptLoginPassword(String encrypted, HttpServletRequest request) {
        if (StringUtil.isBlankParam(encrypted)) {
            return encrypted;
        }
        Object encSeq = request.getSession(true).getAttribute("s_encSeq");
        if (encSeq == null) {
            log.warn("login password decrypt skipped: no s_encSeq in session");
            return null;
        }
        try {
            Map<String, Object> keyParam = new HashMap<>();
            keyParam.put("s_encSeq", encSeq);
            String privateKeyStr = loginMapper.getRsaPrivateKey(keyParam);
            if (StringUtil.isBlankParam(privateKeyStr)) {
                return null;
            }
            PrivateKey privateKey = RSAUtil.toPrivateKey(privateKeyStr);
            return RSAUtil.decrypt(encrypted, privateKey);
        } catch (Exception e) {
            log.warn("login password decrypt failed: {}", e.getMessage());
            return null;
        }
    }

    /* ldap 검증 */
    private LoginPasswordDecision authenticatePassword(Map<String, Object> params,
                                                       Map<String, Object> memberInfo,
                                                       String password) {
        String userId = StringUtil.defaultIfBlank(MapUtil.valueAsString(memberInfo, "userid", "user_id"), MapUtil.firstNonBlank(params, "userid"));
        boolean localTestAccount = "Y".equalsIgnoreCase(MapUtil.valueAsString(memberInfo, "isTest"));
        String localPassword = MapUtil.valueAsString(memberInfo, "pwd");
        String ldapCompanyYn = StringUtil.defaultIfBlank(
                callOrDefault(() -> loginMapper.getLdapCompanyYn(MapUtil.valueAsString(memberInfo, "comp")), null),
                StringUtil.isBlankParam(localPassword) ? "Y" : "N"
        );

        if (localTestAccount && (StringUtil.isBlankParam(localPassword) || localPassword.equals(password))) {
            return LoginPasswordDecision.ok();
        }

        if (!"Y".equalsIgnoreCase(ldapCompanyYn) && !StringUtil.isBlankParam(localPassword)) {
            return localPassword.equals(password)
                    ? LoginPasswordDecision.ok()
                    : LoginPasswordDecision.fail(-3, "ID 또는 PW가 잘못되었습니다.");
        }

        try {
            LdapResultDTO ldapResult = ldapLogin(LoginDTO.builder().userId(userId).password(password).build());
            if (Boolean.TRUE.equals(ldapResult.getIsAuth())) {
                return LoginPasswordDecision.ok();
            }
            if (Boolean.TRUE.equals(ldapResult.getIsExpired())) {
                return LoginPasswordDecision.fail(-9, "비밀번호가 만료되었습니다.\n비밀번호를 변경해주세요.");
            }
            return LoginPasswordDecision.fail(-8, "ID 또는 PW가 잘못되었습니다.");
        } catch (BadCredentialsException e) {
            return LoginPasswordDecision.fail(-8, "ID 또는 PW가 잘못되었습니다.");
        } catch (Exception e) {
            log.warn("spring security LDAP authentication failed. userId={}, reason={}", userId, e.getMessage());
            return LoginPasswordDecision.fail(-8, "ID 또는 PW가 잘못되었습니다.");
        }
    }

    /**
     * 외부 교육 회원 분기
     */
    private Map<String, Object> processEtcMemberLogin(Map<String, Object> params,
                                                      String userId,
                                                      String successRedirectUrl,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      String failRedirectUrl) {
        Map<String, Object> output = new LinkedHashMap<>();
        Map<String, Object> etcMemberInfo = findEtcMemberInfo(params, userId);
        output.put("isGgdEduOk", 1);
        if (etcMemberInfo == null) {
            return fail(output, -1, "ID 또는 PW가 잘못되었습니다.", failRedirectUrl);
        }

        Map<String, Object> memberInfo = findMemberInfo(userId).orElse(null);
        if (memberInfo == null) {
            return fail(output, -1, "ID 또는 PW가 잘못되었습니다.", failRedirectUrl);
        }
        if ("Y".equalsIgnoreCase(MapUtil.valueAsString(memberInfo, "isretire", "ISRETIRE"))) {
            return fail(output, -6, "ID 또는 PW가 잘못되었습니다.", failRedirectUrl);
        }

        // 교육과정 목록으로 이동
        String resultUrl = "/education/list/courseList?type=MPACK";

        // 초기 비번 세팅일 경우
        if ("N".equalsIgnoreCase(MapUtil.valueAsString(etcMemberInfo, "pwyn", "PW_YN"))) {
            resultUrl = "/changePw?type=etc";
        }

        params.put("loginPath", "GENIUS-ETC");
        enrichMemberInfo(memberInfo, params, request, resultUrl);
        updateLoginCnt(params, memberInfo);
        doJwtLogin(output, memberInfo, userId, resultUrl, response);
        insertLoginLog(params);
        return success(output, 1, resultUrl);
    }

    /* 로그인 부가정보 */
    private void enrichMemberInfo(Map<String, Object> memberInfo,
                                  Map<String, Object> params,
                                  HttpServletRequest request,
                                  String successRedirectUrl) {
        memberInfo.put("userip", params.get("lgip"));
        memberInfo.put("lgip", params.get("lgip"));
        memberInfo.put("service_type", serverType);
        memberInfo.put("successRedirectURL", successRedirectUrl);
        memberInfo.put("con_device", params.get("con_device"));
        memberInfo.put("isDevice", params.get("isDevice"));
        memberInfo.put("loginPath", params.getOrDefault("loginPath", "GENIUS"));
        memberInfo.put("sessionId", request.getSession(true).getId());
    }

    /* ldap login */
    private LdapResultDTO ldapLogin(LoginDTO loginDTO) {
        if (loginDTO.getEncryptedPassword() != null) {
            loginDTO.setPassword(loginDTO.getEncryptedPassword());
        }

        if (loginDTO.getPassword() == null) {
            return LdapResultDTO.getLdapFailResult("비밀번호가 올바르지 않습니다.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword())
        );

        return (LdapResultDTO) authentication.getPrincipal();
    }

    /* user 계정 status check */
    private int isMember(Map<String, Object> params) {
        // 계정 존재 여부
        Integer isMemberCount = callOrDefault(() -> loginMapper.getIsMember(params), 1);
        if (isMemberCount == null || isMemberCount == 0) {
            return -1;
        }

        // 조직 사용 여부
        Integer possible = callOrDefault(() -> loginMapper.getPossibleLoginStatus(params), 1);
        return possible == null ? -1 : possible;
    }

    /* jwt login 처리 */
    private void doJwtLogin(Map<String, Object> output,
                            Map<String, Object> memberInfo,
                            String userId,
                            String resultUrl,
                            HttpServletResponse response) {
        JwtDTO jwtDTO = buildJwtDTO(memberInfo, userId);
        String accessToken = tokenService.issue(jwtDTO, response);
        output.put("accessToken", accessToken);
        output.put("userId", jwtDTO.getUserId());
        output.put("userNm", jwtDTO.getUserNm());
        output.put("role", jwtDTO.getRole());
        output.put("resultUrl", resultUrl);
    }

    /* jwt token 생성 - userId */
    public JwtDTO buildJwtDTO(String userId) {
        return buildJwtDTO(requireMemberInfo(userId), userId);
    }

    /* jwt token 생성 - memberInfo */
    private JwtDTO buildJwtDTO(Map<String, Object> memberInfo, String fallbackUserId) {
        String resolvedUserId = StringUtil.defaultIfBlank(MapUtil.valueAsString(memberInfo, "userid", "user_id", "USERID"), fallbackUserId);
        return JwtDTO.builder()
                .userId(resolvedUserId)
                .userNm(StringUtil.defaultIfBlank(MapUtil.valueAsString(memberInfo, "name", "user_nm", "USER_NM"), resolvedUserId))
                .orgCd(MapUtil.valueAsString(memberInfo, "org_cd", "dept_cd", "hq_org_cd", "bonbu_cd"))
                .comp(StringUtil.defaultIfBlank(MapUtil.valueAsString(memberInfo, "comp"), "KT"))
                .role(resolveRole(memberInfo))
                .adminGrade(MapUtil.valueAsString(memberInfo, "gubun", "gadmin", "admin_grade"))
                .build();
    }

    /* user info */
    public Optional<Map<String, Object>> findMemberInfo(String userId) {
        Map<String, Object> memberInfo = callOrDefault(() -> {
            Map<String, Object> params = new HashMap<>();
            params.put("userid", userId);
            return loginMapper.getMemberInfo(params);
        }, null);

        if (memberInfo != null) {
            return Optional.of(memberInfo);
        }
        return isFallbackUser(userId)
                ? Optional.of(fallbackMemberInfo(userId))
                : Optional.empty();
    }

    private Map<String, Object> requireMemberInfo(String userId) {
        return findMemberInfo(userId)
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 조회할 수 없습니다."));
    }

    /* sms info */
    private Optional<Map<String, Object>> getSendSmsInfo(Map<String, Object> params) {
        Map<String, Object> smsInfo = callOrDefault(() -> loginMapper.getSendSmsInfo(params), null);
        if (smsInfo != null) {
            return Optional.of(smsInfo);
        }
        String userId = MapUtil.firstNonBlank(params, "userid", "s_userid");
        if (isFallbackUser(userId)) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("userid", userId);
            fallback.put("name", "테스트유저");
            fallback.put("handphone", "010-2222-3333");
            fallback.put("email", "test@example.com");
            fallback.put("comp", "KT");
            return Optional.of(fallback);
        }
        return Optional.empty();
    }

    /* 외부 사용자 */
    private int getIsEtcMember(Map<String, Object> params, String userId) {
        try {
            Integer result = loginMapper.getIsEtcMember(params);
            return result == null ? 0 : result;
        } catch (Exception e) {
            return "etc-test".equalsIgnoreCase(userId) ? 1 : 0;
        }
    }

    /* 외부 사용자 정보 */
    private Map<String, Object> findEtcMemberInfo(Map<String, Object> params, String userId) {
        Map<String, Object> info = callOrDefault(() -> loginMapper.getEtcMemberInfo(params), null);
        if (info != null || !"etc-test".equalsIgnoreCase(userId)) {
            return info;
        }
        String password = MapUtil.valueAsString(params, "pwd");
        if (!StringUtil.isBlankParam(password) && !"test123!".equals(password)) {
            return null;
        }
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("userid", userId);
        fallback.put("name", "외부 테스트유저");
        fallback.put("comp", "8888");
        fallback.put("pwd", "test123!");
        fallback.put("pwyn", "N");
        fallback.put("delyn", "N");
        return fallback;
    }

    /* 계정 잠금 check */
    private String getOtpLockCheck(Map<String, Object> params) {
        return callOrDefault(() -> loginMapper.getOtpLockCheck(params), "S");
    }

    /* 오늘 동일한 IP에서 OTP 인증성공 내역 여부 */
    private int getCheckLoginIp(Map<String, Object> params) {
        Integer result = callOrDefault(() -> loginMapper.getCheckLoginIp(params), 0);
        return result == null ? 0 : result;
    }

    private void updateLoginCnt(Map<String, Object> params, Map<String, Object> memberInfo) {
        params.put("comp", MapUtil.valueAsString(memberInfo, "comp"));
        callOrDefault(() -> loginMapper.updateLoginCnt(params), 0);
    }

    private void updateLoginFailCnt(Map<String, Object> params) {
        callOrDefault(() -> loginMapper.updateLoginFailCnt(params), 0);
    }

    /* 로그인 실패횟수 초기화 */
    private void updateLoginFailInit(Map<String, Object> params) {
        callOrDefault(() -> loginMapper.updateLoginFailInit(params), 0);
    }

    /* login success log */
    private void insertLoginLog(Map<String, Object> params) {
        callOrDefault(() -> loginMapper.insertLoginLog(params), 0);
    }

    /* login fail log */
    private void insertLoginFailLog(Map<String, Object> params) {
        callOrDefault(() -> loginMapper.insertLoginFailLog(params), 0);
    }

    /* saml session check */
    private String checkSAMLSession(HttpServletRequest request, Map<String, Object> memberInfo) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        String samlRequest = attrAsString(session, "SAMLRequest");
        String relayState = attrAsString(session, "RelayState");
        String channel = attrAsString(session, "SAMLChannel");

        if (!StringUtil.isBlankParam(samlRequest) && !StringUtil.isBlankParam(channel)) {
            memberInfo.put("SAMLRequest", samlRequest);
            memberInfo.put("RelayState", relayState);
            memberInfo.put("SAMLChannel", channel);
            return channel;
        }
        return null;
    }

    /* 계정 잠금 여부 */
    private boolean isLocked(Map<String, Object> memberInfo) {
        int failCount = parseInt(MapUtil.valueAsString(memberInfo, "lgfail"), 0);
        return loginExceptIsUse && failCount >= loginExceptionValue;
    }

    /* 로그인 성공 시 redirect url */
    private String resolveSuccessRedirect(Map<String, Object> input, HttpServletRequest request, LoginFlow flow) {
        if (isKateReturn(input)) {
            return "/nsso_return.jsp";
        }

        String requested = MapUtil.firstNonBlank(input, "url", "redirect");
        if (isAllowedRedirect(requested)) {
            return requested;
        }

        return switch (flow) {
            case CDS -> "/cds/result/cdsPersonalResultDetails";
            case EXAM -> "/exam/result/examCourseResultLists";
            case DEFAULT -> mainUrl;
        };
    }

    /* mkate return page */
    private boolean isKateReturn(Map<String, Object> input) {
        return "kate_return".equalsIgnoreCase(MapUtil.valueAsString(input, "target"));
    }

    /* 로그인 실패 시 redirect url */
    private String defaultFailUrl(LoginFlow flow) {
        return switch (flow) {
            case CDS -> "/login/checkResult";
            case EXAM -> "/login/testDev";
            case DEFAULT -> "/login";
        };
    }

    /* redirect url 검증 */
    private boolean isAllowedRedirect(String value) {
        if (StringUtil.isBlankParam(value)) {
            return false;
        }
        if (!value.startsWith("/") || value.startsWith("//")) {
            return false;
        }
        if (value.contains("\\") || value.contains("\r") || value.contains("\n")) {
            return false;
        }
        if (value.equals("/login/loginProc") || value.equals("/login/loginProcAjax")
                || value.equals("/mobile/m/login/loginProc") || value.equals("/mobile/m/login/loginProcAjax")) {
            return false;
        }
        String delimiter = "." + "do";
        Integer menuCount = callOrDefault(() -> {
            Map<String, Object> params = new HashMap<>();
            String checkUrl = value;
            int idx = value.indexOf(delimiter);
            if (idx > -1) {
                checkUrl = value.substring(0, idx + delimiter.length());
            }
            params.put("chkUrl", checkUrl);
            return loginMapper.menuUrlChk(params);
        }, null);
        return menuCount == null || menuCount > 0 || !value.endsWith(delimiter);
    }

    /* user id 값 추출 */
    private String resolveLoginUserId(Map<String, Object> input, HttpServletRequest request) {
        if (checkMkateSsoLogin(input)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("sso_id") != null) {
                return String.valueOf(session.getAttribute("sso_id"));
            } else {
                return null;
            }
        }
        return MapUtil.firstNonBlank(input, "userid", "userId", "loginUserId", "s_userid", "p_userid");
    }

    /* mkate sso login 여부 */
    private boolean checkMkateSsoLogin(Map<String, Object> input) {
        return "Y".equalsIgnoreCase(MapUtil.firstNonBlank(input, "p_issso", "issso", "mKate"));
    }

    /* device type */
    private String resolveDevice(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("/mobile/m/")) {
            return "M";
        }
        return "W";
    }

    private Map<String, Object> fallbackMemberInfo(String userId) {
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("userid", userId);
        memberInfo.put("name", "테스트유저");
        memberInfo.put("org_cd", "1001");
        memberInfo.put("comp", "KT");
        memberInfo.put("isadmin", "N");
        memberInfo.put("isTest", "Y");
        memberInfo.put("pwd", "test123!");
        memberInfo.put("lgfail", "lock-test".equalsIgnoreCase(userId) ? "5" : "0");
        memberInfo.put("isretire", "N");
        memberInfo.put("handphone", "010-2222-3333");
        memberInfo.put("_fallback", Boolean.TRUE);
        return memberInfo;
    }

    private boolean isFallbackUser(String userId) {
        return "test".equalsIgnoreCase(userId)
                || "lock-test".equalsIgnoreCase(userId)
                || "etc-test".equalsIgnoreCase(userId);
    }

    private Map<String, Object> normalize(Map<String, Object> input) {
        Map<String, Object> params = new HashMap<>();
        if (input != null) {
            params.putAll(input);
        }
        return params;
    }

    private Map<String, Object> success(Map<String, Object> output, int isOk, String resultUrl) {
        output.put("result", "Y");
        output.put("isOk", isOk);
        output.put("resultUrl", resultUrl);
        return output;
    }

    private Map<String, Object> fail(Map<String, Object> output, int isOk, String message, String resultUrl) {
        output.put("result", "N");
        output.put("isOk", isOk);
        output.put("message", message);
        output.put("resultUrl", resultUrl);
        return output;
    }

    private int normalizeStatus(int isOk) {
        return isOk == -8 ? -1 : isOk;
    }

    private String resolveRole(Map<String, Object> memberInfo) {
        String isAdmin = MapUtil.valueAsString(memberInfo, "isadmin", "admr_yn", "mgr_yn");
        String grade = MapUtil.valueAsString(memberInfo, "gubun", "gadmin", "admin_grade");
        if ("Y".equalsIgnoreCase(isAdmin) || !StringUtil.isBlankParam(grade) && !"N".equalsIgnoreCase(grade)) {
            return "ROLE_ADMIN";
        }
        return "ROLE_USER";
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    /* OTP serial 생성 */
    private String makeSerial() {
        StringBuilder serial = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            serial.append(ThreadLocalRandom.current().nextInt(1, 10));
        }
        return serial.toString();
    }

    private String attrAsString(HttpSession session, String name) {
        Object value = session.getAttribute(name);
        return value == null ? null : String.valueOf(value);
    }

    private boolean isLocalLike() {
        return "local".equalsIgnoreCase(serverType)
                || "dev".equalsIgnoreCase(serverType)
                || "test".equalsIgnoreCase(serverType);
    }

    private record LoginPasswordDecision(boolean success, int status, String message) {
        static LoginPasswordDecision ok() {
            return new LoginPasswordDecision(true, 1, null);
        }

        static LoginPasswordDecision fail(int status, String message) {
            return new LoginPasswordDecision(false, status, message);
        }
    }
}
