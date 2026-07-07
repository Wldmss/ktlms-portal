package com.kt.ktedu.common.web;

import com.kt.ktedu.common.util.core.NumberUtil;
import com.kt.ktedu.common.util.core.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * (레거시 호환 브릿지) genius/lms 의 {@code CommandMap} 대체.
 * <p>
 * 컨트롤러 메서드 인자로 선언하면 {@link CommandMapArgumentResolver} 가 <b>요청 파라미터</b>(+ AJAX JSON 바디)를
 * 자동으로 채워 넣는다. 그래서 레거시 컨트롤러의 {@code CommandMap input} + {@code input.get("키")} 코드가
 * <b>수정 없이</b> 동작한다.
 * <pre>{@code
 * public String list(CommandMap input, Model model) {
 *     String category = (String) input.get("category");   // 요청 파라미터 category
 *     ...
 *     model.addAttribute("list", service.getList(input.getMap()));  // Map 통째로 mapper 전달도 그대로
 * }
 * }</pre>
 *
 * <p><b>중요 — 세션 값은 들어있지 않다.</b> 레거시는 리졸버가 세션 사용자정보를 {@code s_} 접두어로
 * 이 맵에 주입했지만(예: {@code input.get("s_userid")}), 그 부분은 session → JWT 이관으로 제거됐다.
 * 로그인 사용자 정보는 {@code SecurityUtil.getCurrentUserId()} 등으로 얻는다. (session-to-jwt-guide 참고)</p>
 *
 * @deprecated 요청 파라미터 처리는 타입 안전한 방식으로 옮기는 것을 권장한다:
 * {@code @RequestParam}, DTO 바인딩({@code @ModelAttribute}), {@code @RequestBody}.
 * 이 클래스는 대량 이관을 무수정으로 넘기기 위한 과도기 브릿지이며, 신규 코드에서는 쓰지 않는다.
 */
@Deprecated(since = "2026-07-07")
public class CommandMap {

    private final Map<String, Object> map = new HashMap<>();

    /** 값 조회 (없으면 null). 레거시 그대로 Object 반환 → 호출부에서 캐스팅 */
    public Object get(String key) {
        return map.get(key);
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object remove(String key) {
        return map.remove(key);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public void putAll(Map<? extends String, ?> m) {
        if (m != null) map.putAll(m);
    }

    /** 내부 Map 반환. MyBatis mapper(parameterType="map")에 통째로 넘길 때 사용 (레거시 패턴) */
    public Map<String, Object> getMap() {
        return map;
    }

    // ===== 편의 getter (신규 작성 시 캐스팅/파싱 없이) =====

    /** 문자열 조회 (없거나 null 이면 "") */
    public String getString(String key) {
        return StringUtil.nvl(map.get(key));
    }

    /** int 조회 (없거나 변환 불가면 0. 콤마 포함 문자열 허용) */
    public int getInt(String key) {
        return NumberUtil.toInt(map.get(key));
    }

    /** long 조회 (없거나 변환 불가면 0) */
    public long getLong(String key) {
        return NumberUtil.toLong(map.get(key));
    }
}
