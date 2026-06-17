package com.kt.ktedu.core.view;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * tiles 삭제로 인한 임시 처리 TODO tiles-define.xml 내용을 여기에 구현하거나 tiles 미적용 시켜야 함
 */
public class CustomViewResolver extends InternalResourceViewResolver {

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        AbstractUrlBasedView view = super.buildView(viewName);

        // 기존 tiles.xml의 support/*/* 패턴을 자바로 자동화
        if (viewName.startsWith("support/")) {
            // 알맹이가 될 진짜 JSP 풀 경로를 request 영역에 강제로 심어버립니다.
            view.addStaticAttribute("bodyPage", "/WEB-INF/views/" + viewName + ".jsp");

            // 서브 GNB가 필요한 구역이므로 서브 GNB 경로도 강제 주입
            view.addStaticAttribute("subGNB", "/WEB-INF/views/common/tiles/GNBLayout.jsp");
            view.addStaticAttribute("title", "지니어스");

            // 자바 컨트롤러 소스는 가만히 놔두고, 화면만 공통 틀(GNBLayout)로 강제 변조합니다!
            view.setUrl("/WEB-INF/views/common/tiles/GNBLayout.jsp");
        }

        // tiles 적용 sample
        else if (viewName.startsWith("pages/sample/tiles")) {
            view.addStaticAttribute("bodyPage", "/WEB-INF/views/" + viewName + ".jsp");
            view.setUrl("/WEB-INF/views/common/tiles/GNBLayout.jsp");
        }

        return view;
    }
}