package com.kt.ktedu.core.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

/**
 * redirect/message 공통 유틸.
 *
 * <p>두 가지 사용 방식을 모두 지원한다.</p>
 * <ul>
 *     <li>신규 코드: {@link RedirectAttributes} flash 메시지 + Spring MVC redirect view</li>
 *     <li>이관 코드: genius/lms 레거시 {@code RedirectUtil.redirect(request, response, ...)} 호출 호환</li>
 * </ul>
 *
 * <pre>{@code
 * // 신규 코드
 * return RedirectUtil.successRedirect(ra, "저장되었습니다.", "/board/list");
 *
 * // 이관 코드
 * RedirectUtil.redirect(request, response, "저장되었습니다.", "/board/list");
 * RedirectUtil.historyBack(request, response, "입력값을 확인해주세요.");
 * }</pre>
 */
public class RedirectUtil {

    private static final Logger log = LoggerFactory.getLogger(RedirectUtil.class);
    private static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";

    public static final String MESSAGE_KEY = "flashMessage";
    public static final String TYPE_KEY = "flashMessageType";

    public static final String TYPE_SUCCESS = "success";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_INFO = "info";
    public static final String TYPE_WARN = "warn";

    protected RedirectUtil() {
    }

    public static void success(RedirectAttributes ra, String message) {
        set(ra, message, TYPE_SUCCESS);
    }

    public static void error(RedirectAttributes ra, String message) {
        set(ra, message, TYPE_ERROR);
    }

    public static void info(RedirectAttributes ra, String message) {
        set(ra, message, TYPE_INFO);
    }

    public static void warn(RedirectAttributes ra, String message) {
        set(ra, message, TYPE_WARN);
    }

    public static String successRedirect(RedirectAttributes ra, String message, String url) {
        success(ra, message);
        return redirectViewName(url);
    }

    public static String errorRedirect(RedirectAttributes ra, String message, String url) {
        error(ra, message);
        return redirectViewName(url);
    }

    public static String infoRedirect(RedirectAttributes ra, String message, String url) {
        info(ra, message);
        return redirectViewName(url);
    }

    public static String warnRedirect(RedirectAttributes ra, String message, String url) {
        warn(ra, message);
        return redirectViewName(url);
    }

    private static void set(RedirectAttributes ra, String message, String type) {
        if (ra == null) return;
        ra.addFlashAttribute(MESSAGE_KEY, message);
        ra.addFlashAttribute(TYPE_KEY, type);
    }

    private static String redirectViewName(String url) {
        return "redirect:" + internalRedirectPath(url);
    }

    /**
     * (레거시) 검증 실패 시 alert 후 이전 페이지로 돌아가던(history.back) 메서드.
     *
     * @deprecated 검증 실패는 응답으로 알리고 클라이언트가 처리하게 바꾼다.
     * <ul>
     *   <li>AJAX 요청: {@code throw new ApiException("메시지")} 또는 {@code ResponseDTO.fail("메시지")}.
     *       → {@code common-ajax.js} 가 openAlert 로 표시(화면 유지).</li>
     *   <li>화면(폼 제출): 입력 폼 뷰 이름을 다시 반환하고 {@code model} 에 에러 메시지 + 입력값을 담아
     *       그 자리에서 다시 표시한다(history.back 대신 폼 재렌더링).</li>
     * </ul>
     */
    @Deprecated(since = "2026-07-07")
    public static void historyBack(HttpServletRequest request, HttpServletResponse response, String message) {
        writeScript(response, script(message, "history.back();"));
    }

    /**
     * (레거시) alert 후 viewName 으로 POST 이동(현재 url 을 hidden "url" 로 전달)하던 메서드.
     *
     * @deprecated 컨트롤러가 {@link RedirectAttributes} 를 인자로 받고 redirect 뷰 이름을 반환하도록 바꾼다.
     * <pre>{@code
     * // 오류 메시지 후 이동
     * return RedirectUtil.errorRedirect(ra, "권한이 없습니다.", "/main");
     * // 성공 메시지 후 이동
     * return RedirectUtil.successRedirect(ra, "저장되었습니다.", "/board/list");
     * }</pre>
     * 이동 대상에 "이전 url" 등 파라미터를 넘겨야 하면 {@code ra.addAttribute("key", value)} 로 쿼리 파라미터를 붙인다.
     */
    @Deprecated(since = "2026-07-07")
    public static void redirect(HttpServletRequest request,
                                HttpServletResponse response,
                                String message,
                                String viewName
    ) {
        String currentUrl = currentRequestPath(request);
        String action = contextUrl(request, viewName);
        StringBuilder html = new StringBuilder();

        html.append("<form name=\"redirectForm\" id=\"redirectForm\" action=\"")
                .append(escapeHtmlAttr(action))
                .append("\" method=\"post\">");
        appendHidden(html, "url", currentUrl);
        appendCsrfHidden(html, request);
        html.append("</form>");
        html.append("<script>");
        appendAlert(html, message);
        html.append("document.redirectForm.submit();");
        html.append("</script>");

        writeHtml(response, html.toString());
    }

    /**
     * (레거시) alert 후 {@code top.location} 으로 이동(프레임셋 탈출)하던 메서드.
     *
     * @deprecated 프레임셋을 쓰지 않는 신규 구조에선 일반 redirect 와 동일하다.
     * {@code return RedirectUtil.errorRedirect(ra, message, viewName)} (또는 successRedirect) 로 대체한다.
     */
    @Deprecated(since = "2026-07-07")
    public static void redirectAdmin(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String message,
                                     String viewName
    ) {
        String url = escapeJs(contextUrl(request, viewName));
        writeScript(response, script(message, "top.document.location.href='" + url + "';"));
    }

    /**
     * (레거시) 팝업에서 부모창(opener)을 viewName 으로 이동시키고 팝업을 닫던 메서드.
     *
     * @deprecated 신규는 window.open 팝업 대신 in-page 모달({@code openModal})을 쓴다.
     * 처리 후 {@code closeModal()} + 목록 재조회 콜백으로 부모 화면을 갱신한다.
     * 실제 새 창을 유지해야 하면 프런트에서 {@code window.opener.location.href=...; window.close();} 로 처리한다.
     */
    @Deprecated(since = "2026-07-07")
    public static void popRedirect(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String message,
                                   String viewName
    ) {
        String url = escapeJs(contextUrl(request, viewName));
        writeScript(response, script(message,
                "if (window.opener && !window.opener.closed) { window.opener.location.href='" + url + "'; }"
                        + "window.close();"));
    }

    /**
     * (레거시) 팝업 처리 후 부모창(opener)을 reload 하고 팝업을 닫던 메서드.
     *
     * @deprecated 모달({@code openModal})이면 {@code closeModal()} 후 목록 재조회 콜백으로 갱신한다(reload 불필요).
     * 실제 새 창이면 프런트에서 {@code window.opener.location.reload(); window.close();} 로 처리한다.
     */
    @Deprecated(since = "2026-07-07")
    public static void popRedirectReload(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String message
    ) {
        writeScript(response, script(message,
                "if (window.opener && !window.opener.closed) { window.opener.location.reload(); }"
                        + "window.close();"));
    }

    /**
     * (레거시) 팝업 처리 후 부모창(opener)의 특정 JS 함수를 호출하고 팝업을 닫던 메서드.
     *
     * @deprecated 모달({@code openModal}) + 콜백으로 대체한다 — 처리 후 {@code closeModal()} 하고
     * openModal 완료 콜백에서 부모 화면의 함수를 직접 호출한다.
     * 실제 새 창이면 프런트에서 {@code window.opener.함수(); window.close();} 또는 {@code postMessage} 로 처리한다.
     */
    @Deprecated(since = "2026-07-07")
    public static void popRedirectFunctionActive(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 String message,
                                                 String function
    ) {
        String functionName = safeFunctionName(function);
        String actionScript = functionName.isBlank()
                ? ""
                : "if (window.opener && !window.opener.closed && typeof window.opener." + functionName
                        + " === 'function') { window.opener." + functionName + "(); }";
        writeScript(response, script(message,
                actionScript + "window.close();"));
    }

    /**
     * (레거시) modelmap 값들을 hidden 필드로 만들어 viewName 으로 POST 이동하던 메서드 (검색조건 유지용, 메시지 없음).
     *
     * @deprecated 파라미터(검색조건 등)를 유지한 이동은 POST 폼 대신 아래로 바꾼다.
     * <ul>
     *   <li>{@code ra.addAttribute("key", value)} → redirect URL 에 쿼리 파라미터로 붙고, 대상 화면은 GET 으로 재조회.</li>
     *   <li>단순 이동만이면 {@code return "redirect:" + viewName;}</li>
     * </ul>
     * 목록의 검색조건 유지는 화면에서 파라미터를 들고 다니거나 세션이 아닌 요청 파라미터로 처리한다.
     */
    @Deprecated(since = "2026-07-07")
    public static void redirect(HttpServletRequest request,
                                HttpServletResponse response,
                                Map<String, Object> modelmap,
                                String viewName
    ) {
        StringBuilder html = new StringBuilder();
        html.append("<form name=\"redirectForm\" id=\"redirectForm\" method=\"post\" action=\"")
                .append(escapeHtmlAttr(contextUrl(request, viewName)))
                .append("\">");

        appendNestedMap(html, modelmap, "getParameterMap", false);
        appendNestedMap(html, modelmap, "parameter", false);
        if (modelmap != null && !modelmap.containsKey("getParameterMap") && !modelmap.containsKey("parameter")) {
            appendHiddenInputs(html, modelmap, false);
        }
        appendCsrfHidden(html, request);

        html.append("</form>");
        html.append("<script>document.redirectForm.submit();</script>");
        writeHtml(response, html.toString());
    }

    /**
     * (레거시) reqMap 값을 hidden 으로 POST 이동 + alert 메시지 표시 (검색조건 유지 + 메시지).
     *
     * @deprecated 위 {@code redirect(request, response, modelmap, viewName)} 대체 방식(쿼리 파라미터/GET 재조회)에
     * 메시지를 더한 형태다. 메시지는 flash 로 넘긴다:
     * <pre>{@code
     * ra.addAttribute("searchType", searchType);          // 유지할 파라미터
     * return RedirectUtil.successRedirect(ra, "처리되었습니다.", viewName); // flash 메시지 + redirect
     * }</pre>
     */
    @Deprecated(since = "2026-07-07")
    public static void redirect(HttpServletRequest request,
                                HttpServletResponse response,
                                Map<String, Object> reqMap,
                                String redirectMessage,
                                String viewName
    ) {
        StringBuilder html = new StringBuilder();
        html.append("<form name=\"redirectForm\" id=\"redirectForm\" method=\"post\" action=\"")
                .append(escapeHtmlAttr(contextUrl(request, viewName)))
                .append("\">");
        appendHiddenInputs(html, reqMap, true);
        appendCsrfHidden(html, request);
        html.append("</form>");
        html.append("<script>");
        appendAlert(html, redirectMessage);
        html.append("document.redirectForm.submit();");
        html.append("</script>");

        writeHtml(response, html.toString());
    }

    /**
     * (레거시) alert 후 현재 창을 닫던(window.close) 메서드.
     *
     * @deprecated 서버가 창 닫기 스크립트를 내보내지 않는다.
     * 모달({@code openModal})이면 프런트에서 {@code closeModal()}, 실제 새 창이면 프런트에서 {@code window.close()} 로 닫는다.
     * 닫기 전에 알림이 필요하면 AJAX 응답({@code ResponseDTO})을 받아 프런트에서 {@code openAlert} 후 닫는다.
     */
    @Deprecated(since = "2026-07-07")
    public static void windowClose(HttpServletRequest request, HttpServletResponse response, String message) {
        writeScript(response, script(message, "window.close();"));
    }

    /**
     * (레거시) request 없이 alert 후 location.href 로 이동하던 메서드.
     *
     * @deprecated 컨트롤러 반환값으로 처리한다: {@code return RedirectUtil.errorRedirect(ra, message, url)}
     * (또는 successRedirect). request 없이 쓰던 경우라도 신규는 RedirectAttributes 기반으로 통일한다.
     */
    @Deprecated(since = "2026-07-07")
    public static void redirectInit(HttpServletResponse response, String message, String url) {
        writeScript(response, script(message, "document.location.href='" + escapeJs(internalRedirectPath(url)) + "';"));
    }

    private static void appendNestedMap(StringBuilder html,
                                        Map<String, Object> modelmap,
                                        String key,
                                        boolean skipSearchKeys
    ) {
        if (modelmap == null || !(modelmap.get(key) instanceof Map<?, ?> nestedMap)) return;
        appendHiddenInputs(html, nestedMap, skipSearchKeys);
    }

    private static void appendHiddenInputs(StringBuilder html, Map<?, ?> values, boolean skipSearchKeys) {
        if (values == null || values.isEmpty()) return;

        values.forEach((key, value) -> {
            String name = safeString(key);
            if (name.isBlank()) return;
            if (skipSearchKeys && name.startsWith("s_")) return;
            if (value == null) return;

            if (value instanceof String[] array) {
                for (String item : array) {
                    appendHidden(html, name, item);
                }
                return;
            }

            if (value instanceof Iterable<?> iterable) {
                for (Object item : iterable) {
                    appendHidden(html, name, item);
                }
                return;
            }

            appendHidden(html, name, value);
        });
    }

    private static void appendHidden(StringBuilder html, String name, Object value) {
        html.append("<input type=\"hidden\" name=\"")
                .append(escapeHtmlAttr(name))
                .append("\" value=\"")
                .append(escapeHtmlAttr(safeString(value)))
                .append("\">");
    }

    private static void appendCsrfHidden(StringBuilder html, HttpServletRequest request) {
        if (request == null) return;

        Object attr = request.getAttribute(CsrfToken.class.getName());
        if (!(attr instanceof CsrfToken csrfToken)) return;

        appendHidden(html, csrfToken.getParameterName(), csrfToken.getToken());
    }

    private static String script(String message, String actionScript) {
        StringBuilder html = new StringBuilder();
        html.append("<script>");
        appendAlert(html, message);
        html.append(actionScript == null ? "" : actionScript);
        html.append("</script>");
        return html.toString();
    }

    private static void appendAlert(StringBuilder html, String message) {
        if (message == null || message.isBlank()) return;
        html.append("alert('").append(escapeJs(message)).append("');");
    }

    private static void writeScript(HttpServletResponse response, String script) {
        writeHtml(response, script);
    }

    private static void writeHtml(HttpServletResponse response, String body) {
        if (response == null) return;
        if (response.isCommitted()) {
            log.warn("redirect message response already committed");
            return;
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType(HTML_CONTENT_TYPE);

        try {
            PrintWriter out = response.getWriter();
            out.print("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>");
            out.print(body == null ? "" : body);
            out.print("</body></html>");
            out.flush();
        } catch (IOException e) {
            log.error("redirect message response write failed", e);
        }
    }

    private static String contextUrl(HttpServletRequest request, String viewName) {
        String url = internalRedirectPath(viewName);

        String contextPath = request == null ? "" : safeString(request.getContextPath());
        if (contextPath.isBlank()) return url;
        if (url.equals(contextPath) || url.startsWith(contextPath + "/")) return url;

        return contextPath + url;
    }

    private static String currentRequestPath(HttpServletRequest request) {
        if (request == null) return "";

        String url = safeString(request.getServletPath());
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            url += "?" + query;
        }

        return sanitizeUrlValue(url);
    }

    private static String sanitizeUrlValue(String value) {
        return safeString(value)
                .replace("\"", "")
                .replace("'", "")
                .replace("\0", "")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String internalRedirectPath(String value) {
        String url = safeString(value).trim();
        if (url.isBlank()) return "/";
        if (isBlockedRedirectUrl(url)) {
            log.warn("blocked unsafe redirect url: {}", url);
            return "/";
        }

        return url.startsWith("/") ? url : "/" + url;
    }

    private static boolean isBlockedRedirectUrl(String url) {
        String lowerUrl = url.toLowerCase(Locale.ROOT);
        return lowerUrl.startsWith("http://")
                || lowerUrl.startsWith("https://")
                || lowerUrl.startsWith("//")
                || lowerUrl.startsWith("\\")
                || lowerUrl.startsWith("javascript:")
                || lowerUrl.startsWith("data:")
                || lowerUrl.startsWith("vbscript:");
    }

    private static String safeFunctionName(String value) {
        String functionName = safeString(value).trim();
        if (functionName.endsWith("()")) {
            functionName = functionName.substring(0, functionName.length() - 2).trim();
        }
        if (functionName.matches("[A-Za-z_$][0-9A-Za-z_$]*(\\.[A-Za-z_$][0-9A-Za-z_$]*)*")) {
            return functionName;
        }

        log.warn("blocked unsafe redirect callback function: {}", value);
        return "";
    }

    private static String escapeHtmlAttr(String value) {
        return safeString(value)
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String escapeJs(String value) {
        String str = safeString(value);
        StringBuilder escaped = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\\' -> escaped.append("\\\\");
                case '\'' -> escaped.append("\\'");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                case '<' -> escaped.append("\\x3C");
                case '>' -> escaped.append("\\x3E");
                case '&' -> escaped.append("\\x26");
                default -> escaped.append(ch);
            }
        }

        return escaped.toString();
    }

    private static String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
