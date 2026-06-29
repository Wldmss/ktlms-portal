package com.kt.ktedu.auth.ldap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.ktedu.auth.ldap.dto.LdapDTO;
import com.kt.ktedu.auth.ldap.dto.LdapResponseDTO;
import com.kt.ktedu.auth.ldap.dto.LdapResultDTO;
import com.kt.ktedu.auth.ldap.dto.LoginDTO;
import com.kt.ktedu.common.util.crypto.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
// kt ldap 서비스
public class LdapService {

    private final LdapDTO ldapDTO;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * ldap api 호출
     * local 에서는 ldap 연동 안됨, 개발 ldap 은 idList 만 확인 가능
     *
     */
    public LdapResultDTO authenticate(final LoginDTO loginDTO) {
        String[] idList = {"82047550", "82047551", "82047552", "82047553"}; // ldap 개발 테스트용 계정 (82047553:비번만료계정) 비번: new1234!

        // 운영:: user_id = test% 허용, 개발:: idList 만 ldap 적용
        boolean doLdap = (activeProfile.equals("dev") && Arrays.asList(idList).contains(loginDTO.getUsername()))
                || (activeProfile.equals("prod") && !loginDTO.getUsername().startsWith("test"));

        if (doLdap) return this.ldapPeriod(loginDTO);

        return LdapResultDTO.getLdapPassResult();
    }

    public LdapResultDTO authenticateTest(final LoginDTO loginDTO) {
        return ldapPeriod(loginDTO);
    }

    // ldapperiod api 통신
    private LdapResultDTO ldapPeriod(LoginDTO loginDTO) {
        LdapResultDTO result = new LdapResultDTO();
        HttpURLConnection conn = null;

        try {
            String urlString = ldapDTO.getUrl() + "loginperiod";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            result.setCpAuth(ldapDTO.getCPAuth()); // TEST
            result.setLdapAesKey(ldapDTO.getAesKey()); // TEST

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + ldapDTO.getCPAuth());
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            Map<String, Object> requestMap = new HashMap<>();
            Map<String, String> requestData = new HashMap<>();

            requestData.put("connID", ldapEncrypt(ldapDTO.getConnId()));
            requestData.put("connPwd", ldapEncrypt(ldapDTO.getConnPwd()));
            requestData.put("loginID", ldapEncrypt(loginDTO.getUsername()));
            requestData.put("loginPwd", ldapEncrypt(loginDTO.getPassword()));

            requestMap.put("request", requestData);

            // Map을 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestMap);

            result.setRequestParam(requestMap); // TEST

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            BufferedReader reader;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                reader.close();

                result.setLdapResponseOrigin(response.toString()); // TEST
                ObjectMapper mapper = new ObjectMapper();
                LdapResponseDTO ldapResponse = mapper.readValue(response.toString(), LdapResponseDTO.class);

                // check response
                LdapResponseDTO.Response res = ldapResponse.getResponse();
                if (res == null) {
                    result.setMessage(ldapResponse.getErrordescription());
                    return result;
                }

                Object returnresult = res.getReturnresult();

                if (!(returnresult instanceof String)) {
                    LdapResponseDTO.ReturnResult returnResult = mapper.convertValue(returnresult, LdapResponseDTO.ReturnResult.class);
                    ldapResponse.setReturnResult(returnResult);
                    result.setLdapResponseDTO(ldapResponse); // TEST
                } else {
                    result.setLdapResponseDTO(ldapResponse); // TEST
                    String errorMessage = getErrorMessage(ldapResponse.getErrordescription());
                    result.setMessage(errorMessage);

                    if (ldapResponse.getErrorcode().equals("0004")) result.setIsExpired(true); // 비번 만료
                    return result;
                }

                // check returnResult
                LdapResponseDTO.ReturnResult returnResult = ldapResponse.getReturnResult();
                if (returnResult == null) {
                    result.setMessage(ldapResponse.getErrordescription());
                    return result;
                }

                // check isAuth
                String encryptIsAuth = returnResult.getIsAuth();
                String decryptIsAuth = ldapDecrypt(encryptIsAuth);
                boolean isAuth = decryptIsAuth != null && decryptIsAuth.equals("TRUE");
                result.setIsAuth(isAuth);

                if (!isAuth) {
                    // 인증 실패
                    String encryptPwdExpiredDate = returnResult.getPwdExpiredDate();
                    String pwdExpiredDate = ldapDecrypt(encryptPwdExpiredDate);

                    // 비번 만료 확인
                    if (pwdExpiredDate != null) {
                        LocalDate expireDate = LocalDate.parse(pwdExpiredDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalDate today = LocalDate.now();
                        result.setIsExpired(expireDate.isBefore(today));
                    }
                } else {
                    // 인증 성공
                    result.setMessage("로그인 성공");
                }
            } else {
                result.setMessage("ldap 통신 오류");
            }
        } catch (Exception e) {
            result.setMessage(e.toString());
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }

    // ldap AES256 암호화 TODO
    public String ldapEncrypt(String value) throws Exception {
        return AES256Util.encrypt(value);
//        return AES256Util.encrypt(ldapDTO.getAesKey(), value);
    }

    // ldap AES256 복호화 TODO
    public String ldapDecrypt(String value) throws Exception {
        return value != null ? AES256Util.decrypt(value) : null;
//        return value != null ? AES256Util.decrypt(ldapDTO.getAesKey(), value) : null;
    }

    // 오류 메시지 추출
    public String getErrorMessage(String errordescription) {
        Pattern pattern = Pattern.compile("^(\\d{4})/.+\\[comment: (.+?)]");
        Matcher matcher = pattern.matcher(errordescription);

        if (matcher.find()) {
            return matcher.group(2) + "(" + matcher.group(1) + ")";
        }

        return null;
    }
}