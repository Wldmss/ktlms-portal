package com.kt.ktedu.common.common.controller;

import com.kt.ktedu.common.web.DeviceResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 딥링크 진입점. 레거시 direct/pageLink.jsp, direct/shareLink.jsp 를 대체한다.
 * (기존 WebConfig 의 direct 뷰 매핑 → 컨트롤러 이관)
 *
 * <ul>
 *   <li>{@code /pageLink} : 서버측 리다이렉트. return_page(내부 경로)로 이동하되,
 *       모바일/앱 접속이면 {@code /mobile/m} prefix 를 붙인다.</li>
 *   <li>{@code /shareLink} : 네이티브 앱(genius-app)이 설치돼 있으면 앱 스킴({@value #APP_SCHEME_PREFIX})으로,
 *       아니면 {@code /pageLink}(웹)로 폴백하는 브리지 페이지를 내려준다.</li>
 * </ul>
 *
 * 레거시 대비 변경점:
 * <ul>
 *   <li>모바일 판별: 세션 isDevice → {@link DeviceResolver} (UA/헤더 기반, JWT 무관)</li>
 *   <li>오픈 리다이렉트 방지: return_page 는 내부 상대경로(/로 시작, {@code //}·{@code ://} 불가)만 허용.
 *       외부 URL 이면 홈("/")으로 보낸다.</li>
 * </ul>
 */
@Controller
@RequiredArgsConstructor
public class LinkController {

    private final DeviceResolver deviceResolver;

    /** 이동 대상을 지정하는 요청 파라미터명 (레거시 유지) */
    private static final String RETURN_PAGE_PARAM = "return_page";

    /** 모바일/앱 접속 시 붙이는 경로 prefix (레거시 유지) */
    private static final String MOBILE_PREFIX = "/mobile/m";

    /** 네이티브 앱 딥링크 스킴 (genius-app 과 합의된 값) */
    public static final String APP_SCHEME_PREFIX = "ktgenius://deeplink?url=";

    /**
     * 딥링크 리다이렉트. return_page + 나머지 파라미터로 내부 경로를 만들고,
     * 모바일/앱이면 {@link #MOBILE_PREFIX} 를 붙여 redirect.
     */
    // 레거시 /pageLink.do, /pageLink.jsp 는 UrlSuffixRedirectFilter 가 /pageLink 로 떼어주므로 여기선 clean 경로만 매핑
    @RequestMapping("/pageLink")
    public String pageLink(HttpServletRequest request) {
        String target = buildInternalTarget(request);
        if (target == null) {
            return "redirect:/";
        }
        String prefix = deviceResolver.resolve(request).isMobile() ? MOBILE_PREFIX : "";
        return "redirect:" + prefix + target;
    }

    /**
     * 앱-우선 브리지 페이지. 뷰에서 앱 스킴 실행을 시도하고, 미설치 시 {@code /pageLink} 로 폴백한다.
     * (모바일 prefix 는 뷰/폴백 단계에서 처리하므로 여기서는 붙이지 않는다.)
     */
    // 레거시 /shareLink.do, /shareLink.jsp 는 UrlSuffixRedirectFilter 가 /shareLink 로 떼어주므로 여기선 clean 경로만 매핑
    @RequestMapping("/shareLink")
    public String shareLink(HttpServletRequest request, Model model) {
        String target = buildInternalTarget(request);
        model.addAttribute("returnPage", target != null ? target : "/");
        model.addAttribute("appSchemePrefix", APP_SCHEME_PREFIX);
        model.addAttribute("mobilePrefix", MOBILE_PREFIX);
        return "link/shareLink";
    }

    /**
     * return_page + 기타 파라미터 → 검증된 내부 상대경로. 안전하지 않으면 null.
     * 레거시처럼 return_page 를 제외한 파라미터를 {@code &name=value} 로 이어붙인다.
     */
    private String buildInternalTarget(HttpServletRequest request) {
        String returnPage = request.getParameter(RETURN_PAGE_PARAM);
        if (returnPage == null || returnPage.isBlank()) {
            return null;
        }

        StringBuilder sb = new StringBuilder(returnPage);
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (RETURN_PAGE_PARAM.equals(entry.getKey()) || entry.getValue() == null) {
                continue;
            }
            for (String value : entry.getValue()) {
                sb.append('&').append(entry.getKey()).append('=').append(value);
            }
        }

        // CRLF/따옴표/꺾쇠 제거 (헤더 인젝션·XSS 방지)
        String target = sb.toString().replaceAll("[\\r\\n\"'<>]", "");
        return isInternalPath(target) ? target : null;
    }

    /** 내부 상대경로만 허용(오픈 리다이렉트 방지): "/" 로 시작, 프로토콜/네트워크경로 불가 */
    private boolean isInternalPath(String path) {
        if (!path.startsWith("/") || path.startsWith("//") || path.startsWith("/\\")) {
            return false;
        }
        String lower = path.toLowerCase();
        return !lower.contains("://") && !lower.contains("\\") && !lower.startsWith("/javascript:");
    }
}
