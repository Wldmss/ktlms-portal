package com.kt.ktedu.common.layout.controller;

import com.kt.ktedu.common.web.DeviceResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * [임시 MOCK] tiles → include 레이아웃 이관 검증용.
 *
 * <p>genius 레이아웃 프래그먼트(GNB/subGNB/모바일 헤더)가 호출하는 AJAX 엔드포인트를
 * DB 없이 정적 데이터로 응답해, 레이아웃 골격을 실제로 렌더/검증하기 위한 목이다.
 * 실제 메뉴 컨트롤러 + 메뉴 DB 이관 시 이 클래스는 삭제한다.</p>
 *
 * <p>레거시 JS 계약(응답 형태)을 그대로 흉내내므로 반환 구조는 ResponseDTO 를 쓰지 않는다.</p>
 *
 * <p>참고 문서: /migration/genius-tiles-to-include.md</p>
 */
@Controller
@RequiredArgsConstructor
public class LayoutMockController {

    private final DeviceResolver deviceResolver;

    /**
     * GNB 목록. 웹(gnbLayout)은 HTML, 모바일(KT_GNB_Mobile_Main)은 JSON menuList 를 기대하므로
     * 접속 디바이스로 분기한다. (레거시가 같은 URL 을 웹/모바일 공용으로 사용)
     */
    @RequestMapping({"/a/layout/gnbListAjax", "/mobile/m/a/layout/gnbListAjax"})
    @ResponseBody
    public ResponseEntity<?> gnbListAjax(HttpServletRequest request) {
        if (deviceResolver.resolve(request).isMobile()) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("menuList", mockMobileMenuList());
            body.put("tutorYn", "N");
            body.put("appCheck", "N");
            body.put("hrdCheck", "N");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        }
        // 웹: .l-gnb 영역에 그대로 삽입되는 HTML 조각
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8))
                .body(mockWebGnbHtml());
    }

    /** 웹 subGNB(defaultSubGNBLayout) — 현재 메뉴 타이틀/설명. */
    @RequestMapping("/a/getMenuInfoAjax2")
    @ResponseBody
    public Map<String, Object> getMenuInfoAjax2() {
        Map<String, Object> menu = new LinkedHashMap<>();
        menu.put("menuNm", "[MOCK] 메뉴 타이틀");
        menu.put("menuText", "레이아웃 검증용 목 데이터입니다.");
        menu.put("linkUrl", "");
        menu.put("imgFileNm", "");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("menu", menu);
        body.put("serverType", "local");
        return body;
    }

    /** 모바일 서브 헤더(KT_GNB_Mobile) — 현재 페이지 타이틀(.gnb-category). */
    @RequestMapping({"/mobile/m/a/getMenuInfoAjax", "/a/getMenuInfoAjax"})
    @ResponseBody
    public Map<String, Object> getMenuInfoAjax() {
        Map<String, Object> menu = new LinkedHashMap<>();
        menu.put("menuNm", "[MOCK] 페이지 타이틀");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("menu", menu);
        return body;
    }

    /** 모바일 메인 헤더 알림 카운트. */
    @RequestMapping({"/a/alarm/alarmCntAjax", "/mobile/m/a/alarm/alarmCntAjax"})
    @ResponseBody
    public Map<String, Object> alarmCntAjax() {
        Map<String, Object> cnt = new LinkedHashMap<>();
        cnt.put("unreadCnt", 0);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("alarmCnt", cnt);
        return body;
    }

    /** 메뉴 이동 로그 적재(no-op). */
    @RequestMapping({"/a/insertMenuLogAjax", "/mobile/m/a/insertMenuLogAjax"})
    @ResponseBody
    public Map<String, Object> insertMenuLogAjax() {
        return new LinkedHashMap<>();
    }

    /** 통합검색 팝업(KT_SearchPop) — 최근/인기 검색어 목록. */
    @RequestMapping({"/a/search/swordListAjax", "/mobile/m/a/search/swordListAjax"})
    @ResponseBody
    public Map<String, Object> swordListAjax() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("swordList", List.of());
        body.put("htagList", List.of());
        return body;
    }

    /** 통합검색 팝업 — 최근 검색어 삭제(no-op). */
    @RequestMapping({"/a/search/swordResetAjax", "/mobile/m/a/search/swordResetAjax"})
    @ResponseBody
    public Map<String, Object> swordResetAjax() {
        return new LinkedHashMap<>();
    }

    // ---- mock data ----

    private List<Map<String, Object>> mockMobileMenuList() {
        return List.of(
                menu("1", "M.LEARNING", "학습참여", null, "javascript:void(0);", "N"),
                menu("2", "M.LEARNING.LIST", "전체 학습", "M.LEARNING", "/mobile/m/education/list/courseList.do", "Y"),
                menu("1", "M.MY_PAGE", "마이페이지", null, "javascript:void(0);", "N"),
                menu("2", "M.MY_PAGE.MYCLASS", "나의 강의실", "M.MY_PAGE", "/mobile/m/myclass/course/myCourse/myCourseList.do", "Y"),
                menu("1", "M.SUPPORT.HELP", "학습지원센터", null, "javascript:void(0);", "N")
        );
    }

    private Map<String, Object> menu(String level, String menuId, String menuNm,
                                     String pMenuId, String linkUrl, String leafYn) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("menuLevel", level);
        m.put("menuId", menuId);
        m.put("menuNm", menuNm);
        m.put("pMenuId", pMenuId);
        m.put("linkUrl", linkUrl);
        m.put("leafYn", leafYn);
        return m;
    }

    private String mockWebGnbHtml() {
        return """
                <div class="gnb-mock" style="display:flex;gap:24px;align-items:center;padding:16px 24px;border-bottom:1px solid #e9ecef;">
                    <strong style="color:#ff6b71;">KT LMS <span style="color:#999;font-size:13px;">[MOCK GNB]</span></strong>
                    <a href="/education/list/courseList.do">학습참여</a>
                    <a href="/myclass/course/myCourse/myCourseList.do">나의 강의실</a>
                    <a href="/support/notice/noticeList.do">학습지원센터</a>
                </div>
                """;
    }
}
