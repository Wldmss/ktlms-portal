package com.kt.ktedu.main.controller;

import com.kt.ktedu.auth.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/* KT지니어스 포털 메인 페이지 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final JwtProvider jwtProvider;

    /* 크롬 devtools pageNotFound 로그 숨김 처리 */
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    @ResponseBody
    public void ignoreChromeDevToolsRequest() {
    }

    /* 메인 페이지 TODO 메인 주소 변경("/") */
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String mainPage(HttpServletRequest request) {
        if (jwtProvider.hasValidAccessToken(request)) {
            return "sample/sample";
        }

        return "redirect:/login";
    }
}
