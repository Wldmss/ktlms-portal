package com.kt.ktedu.sample.controller;

import com.kt.ktedu.common.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class SampleController {

    @GetMapping(value = {"/sample", "/mobile/m/sample"})
    public String mainComp(ModelAndView model) {
        model.addObject("test", System.getProperty("java.version"));

        return "sample/sample";
    }

    @GetMapping(value = {"/sampleTiles", "/mobile/m/sampleTiles"})
    public String sampleTiles(ModelAndView model) {
        model.addObject("test", System.getProperty("java.version"));

        return "sample/tiles/sampleTiles";
    }

    @PostMapping(value = "/popup/sampleDetail")
    public String getSamplePopup() {
        return "sample/popup/sample-popup";
    }

    // ---- [검증용] tiles → include 레이아웃 샘플 ----

    /** 웹 tiles-top / tiles-bottom */
    @GetMapping(value = {"/sample/tiles/web", "/mobile/m/sample/tiles/web"})
    public String sampleWebTiles() {
        return "sample/tiles/webTiles";
    }

    /** 모바일 일반 m-top / m-bottom */
    @GetMapping(value = {"/sample/tiles/mobile", "/mobile/m/sample/tiles/mobile"})
    public String sampleMobileTiles() {
        return "sample/tiles/mobileTiles";
    }

    /** 모바일 메인 m-main-top / m-main-bottom */
    @GetMapping(value = {"/sample/tiles/mobile-main", "/mobile/m/sample/tiles/mobile-main"})
    public String sampleMobileMainTiles() {
        return "sample/tiles/mobileMainTiles";
    }

    @PostMapping(value = "/ajaxTest", produces = "application/json; charset=UTF-8")
    public ResponseEntity<ResponseDTO> doLogin(HttpServletResponse response, @RequestBody Map<String, String> data) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .success(true)
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
