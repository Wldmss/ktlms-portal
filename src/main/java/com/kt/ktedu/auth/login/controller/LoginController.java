package com.kt.ktedu.auth.login.controller;

import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.auth.jwt.service.TokenService;
import com.kt.ktedu.auth.login.dto.LoginRequestDTO;
import com.kt.ktedu.auth.login.service.LoginService;
import com.kt.ktedu.common.util.core.MapUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/* 로그인 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final JwtProvider jwtProvider;
    private final LoginService loginService;
    private final TokenService tokenService;

    @Value("${url.portal.main}")
    private String mainUrl;

    /* 로그인 페이지 */
    @RequestMapping(value = {"/login", "/mobile/m/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String loginPage(@RequestParam Map<String, Object> input, HttpServletRequest request, Model model, @RequestParam(value = "mode", required = false) String mode) {
        captureSamlInput(input, request);

        if (jwtProvider.hasValidAccessToken(request)) {
            // 로그인 token이 유효한 경우 메인 페이지로 이동
            return "redirect:" + mainUrl;
        }

        applyLoginModel(input, request, model, mode != null ? mode : "default");
        return "login/login";
    }

    /* 로그아웃 */
    @RequestMapping(value = {"/logout", "/login/logout", "/mobile/m/login/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpServletResponse response) {
        tokenService.clearCookies(response);
        return "common/pages/logout-redirect";
    }

    /* 공개키 가져오기 */
    @PostMapping({"/login/getEncKeyAjax", "/mobile/m/login/getEncKeyAjax"})
    public ResponseEntity<Map<String, Object>> getEncKeyAjax(@RequestParam Map<String, Object> input, HttpServletRequest request) {
        Map<String, Object> output = new LinkedHashMap<>();

        int encSeq = ThreadLocalRandom.current().nextInt(1, 1_000_001); // 난수 생성(1부터 100만까지)
        input.put("encSeq", encSeq);

        String publicKey = loginService.getRsaPublicKey(input);
        if (publicKey == null || publicKey.isBlank()) {
            output.put("result", "N");
            output.put("message", "다시 시도해 주세요.");
        } else {
            request.getSession(true).setAttribute("s_encSeq", encSeq);  // TODO 세션 확인 필요
            output.put("result", "Y");
            output.put("encKey", publicKey);
        }
        return ResponseEntity.ok(output);
    }

    /* 로그인 Ajax */
    @PostMapping({"/login/loginProcAjax", "/mobile/m/login/loginProcAjax"})
    public ResponseEntity<Map<String, Object>> loginProcAjax(@ModelAttribute LoginRequestDTO loginRequest,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        return ResponseEntity.ok(loginService.processLogin(loginRequest.toMap(), request, response, LoginService.LoginFlow.DEFAULT));
    }

    /* 로그인 처리 TODO - mkate 연동 확인 */
    @RequestMapping(value = {"/login/loginProc", "/mobile/m/login/loginProc"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String loginProc(@RequestParam Map<String, Object> input,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Model model) {
        Map<String, Object> result = loginService.processLogin(input, request, response, LoginService.LoginFlow.DEFAULT);
        if ("Y".equals(result.get("result"))) {
            return "redirect:" + result.get("resultUrl");
        }

        // 로그인 실패 처리
        input.put("message", MapUtil.valueAsString(result, "message"));
        applyLoginModel(input, request, model, "default");
        return "login/login";
    }

    /* 로그인 인증번호 문자 발송 */
    @RequestMapping(value = {"/login/sendSmsOptAuthAjax", "/mobile/m/login/sendSmsOptAuthAjax"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> sendSmsOptAuthAjax(@RequestParam Map<String, Object> input, HttpServletRequest request) {
        return ResponseEntity.ok(loginService.sendSmsOtp(input, request));
    }

    /* 로그인 인증번호 확인 */
    @PostMapping({"/login/sendSmsAuthCheckAjax", "/mobile/m/login/sendSmsAuthCheckAjax"})
    public ResponseEntity<Map<String, Object>> sendSmsAuthCheckAjax(@RequestParam Map<String, Object> input, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(loginService.checkSmsOtp(input, request, response));
    }

    /* 비밀번호 변경 - 외부 조직 : 로그인 이후에만 접근 가능 */
    @RequestMapping(value = {"/changePw", "/mobile/m/changePw"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String changePw(@RequestParam Map<String, Object> input, Model model) {
        model.addAttribute("output", input);
        return "login/changePw";
    }

    /* 비밀번호 변경 화면 - 외부 조직 TODO changePw 병합 */
    @Deprecated
    @RequestMapping(value = {"/login/myclass/course/userInfoPw", "/mobile/m/login/myclass/course/userInfoPw"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userInfoPw(@RequestParam Map<String, Object> input, Model model) {
        model.addAttribute("etcInfo", input);
        return "login/changePw";
    }

    /* 비밀번호 업데이트 - 외부 조직 */
    @RequestMapping(value = {"/login/confirmPwdAjax", "/mobile/m/login/confirmPwdAjax"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> confirmPwdAjax(@RequestParam Map<String, Object> input) {
        return ResponseEntity.ok(loginService.confirmPassword(input));
    }

    /* 인증성공시 TZ_SMSAUTH 테이블 정보 UPDATE TODO 사용하는지 check - pub 에만 존재 */
    @Deprecated
    @RequestMapping(value = {"/login/updateOptInfoAjax", "/mobile/m/login/updateOptInfoAjax"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> updateOptInfoAjax(@RequestParam Map<String, Object> input) {
        return ResponseEntity.ok(loginService.updateOptInfo(input));
    }

    /* 로그인 페이지 팝업 TODO 확인 필요 */
    @Deprecated
    @RequestMapping(value = {"/login/popupOpen", "/mobile/m/login/popupOpen"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String popupOpen() {
        return "login/popupOpen";
    }

    /* 사용자용 관리자 페이지  TODO 미사용 */
    @Deprecated
    @PostMapping({"/login/testSendAdminSmsAuthCheckAjax", "/mobile/m/login/testSendAdminSmsAuthCheckAjax"})
    public ResponseEntity<Map<String, Object>> testSendAdminSmsAuthCheckAjax(@RequestParam Map<String, Object> input, HttpServletRequest request) {
        return ResponseEntity.ok(loginService.testSendAdminSmsAuthCheck(input, request));
    }

    /*-------------OTP 우회 로그인 페이지-------------*/
    /* 임시 로그인페이지(역량 결과조회)
     * /login?mode=cds 로 url 변경 (TODO)
     * */
    @RequestMapping(value = {"/login/checkResult", "/mobile/m/login/checkResult"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String checkResult(@RequestParam Map<String, Object> input, HttpServletRequest request, Model model) {
        applyLoginModel(input, request, model, "cds");
        return "login/login";
    }

    /* 임시 로그인페이지(대량평가 결과조회)
     * /login?mode=exam 로 url 변경 (TODO)
     * */
    @RequestMapping(value = {"/login/testDev", "/mobile/m/login/testDev"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String testDev(@RequestParam Map<String, Object> input, HttpServletRequest request, Model model) {
        applyLoginModel(input, request, model, "exam");
        return "login/login";
    }

    /* 역량 결과조회 로그인 Ajax (로그인 시 문자인증X, RSA X) */
    @PostMapping({"/login/cdsLoginProcAjax", "/mobile/m/login/cdsLoginProcAjax"})
    public ResponseEntity<Map<String, Object>> cdsLoginProcAjax(@ModelAttribute LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(loginService.processLogin(loginRequest.toMap(), request, response, LoginService.LoginFlow.CDS));
    }

    /* 대량평가 결과조회 로그인 Ajax (로그인 시 문자인증X, RSA X) */
    @PostMapping({"/login/examLoginProcAjax", "/mobile/m/login/examLoginProcAjax"})
    public ResponseEntity<Map<String, Object>> examLoginProcAjax(@ModelAttribute LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(loginService.processLogin(loginRequest.toMap(), request, response, LoginService.LoginFlow.EXAM));
    }

    /* 중복 세션 로그아웃 */
    @PostMapping({"/login/forceLoginAjax", "/mobile/m/login/forceLoginAjax"})
    public ResponseEntity<Map<String, Object>> forceLoginAjax(@RequestParam Map<String, Object> input,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        return ResponseEntity.ok(loginService.forceLogin(input, request, response));
    }

    /*-------------공통-------------*/
    /* 로그인 페이지 attribute setting */
    private void applyLoginModel(Map<String, Object> input, HttpServletRequest request, Model model, String mode) {
        model.addAttribute("input", input);
        model.addAttribute("popupInfo", loginService.getPopupInfo());   // 공지 팝업
        model.addAttribute("loginMode", mode);
        model.addAttribute("ssoMessage", input.get("message"));
        model.addAttribute("ssoStatus", input.get("sso"));

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("SAMLChannel") != null) {
            model.addAttribute("samlChannel", session.getAttribute("SAMLChannel"));
        }
    }

    /* saml session 남기기 */
    private void captureSamlInput(Map<String, Object> input, HttpServletRequest request) {
        String samlRequest = value(input, "SAMLRequest");
        String relayState = value(input, "RelayState");
        String channel = value(input, "SAMLChannel", "channel");
        if (samlRequest == null || channel == null) {
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("SAMLRequest", samlRequest);
        session.setAttribute("RelayState", relayState);
        session.setAttribute("SAMLChannel", channel);
    }

    private String value(Map<String, Object> input, String... keys) {
        for (String key : keys) {
            Object value = input.get(key);
            if (value != null && !String.valueOf(value).isBlank()) {
                return String.valueOf(value);
            }
        }
        return null;
    }
}
