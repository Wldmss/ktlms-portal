package com.kt.ktedu.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * [403 권한 에러] 스프링 시큐리티나 인터셉터에서 권한 거부 시 작동
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ModelAndView handle403(Exception e, HttpServletRequest request) {
        log.warn("[403] 권한 없는 사용자의 접근 시도 | URL: {}", request.getRequestURI());
        return createErrorModelAndView("403", "이 페이지를 열어볼 수 있는 권한이 없습니다.");
    }

    /**
     * [404 에러]
     * (※ 주의: servlet-context.xml 이나 가동 설정에 throwExceptionIfNoHandlerFound 옵션이 켜져있어야 자바단까지 넘어옵니다)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handle404(Exception e, HttpServletRequest request) {
        log.warn("[404] 존재하지 않는 주소 요청 | URL: {}", request.getRequestURI());
        return createErrorModelAndView("404", "방문하시려는 주소가 잘못 입력되었거나 삭제된 페이지입니다.");
    }

    /**
     * [405 에러] 허용되지 않은 HTTP Method (GET/POST) 마크
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handle405(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("[405] 잘못된 메서드 접근 | URL: {} | 메시지: {}", request.getRequestURI(), e.getMessage());
        return createErrorModelAndView("405", "잘못된 접근 방식입니다.");
    }

    /**
     * [파라미터/데이터 에러] 밸리데이션 실패나 형변환 오류 등 (400 계열)
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ModelAndView handle400(Exception e, HttpServletRequest request) {
        log.error("[400] 잘못된 요청 인자 입력 | URL: {}", request.getRequestURI(), e);
        return createErrorModelAndView("400", "요청 값이 올바르지 않습니다. 입력 데이터를 확인해 주세요.");
    }

    /**
     * [500 에러] 누락된 자바 NullPointerException, DB 에러 등 모든 서버 장애
     */
    @ExceptionHandler({DataAccessException.class, NullPointerException.class})
    public ModelAndView handleAllException(Exception e, HttpServletRequest request) {
        // 백엔드 콘솔 및 파일 로그에 에러 원인을 상세히 기록 (유지보수의 핵심 기둥)
        log.error("[500] 서버 내부 심각한 시스템 장애 발생 | URL: {}", request.getRequestURI(), e);

        return createErrorModelAndView("500", "시스템에 일시적인 오류가 발생했습니다. 관리자에게 문의하세요.");
    }

    // 공통 ModelAndView 생성 헬퍼 메서드
    private ModelAndView createErrorModelAndView(String code, String message) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("common/pages/error");
        mav.addObject("errorCode", code);
        mav.addObject("errorMessage", message);
        return mav;
    }
}