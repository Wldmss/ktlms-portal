package com.kt.ktedu.sample.controller;

import com.kt.ktedu.common.dto.ResponseDTO;
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

    @PostMapping(value = "/ajaxTest", produces = "application/json; charset=UTF-8")
    public ResponseEntity<ResponseDTO> doLogin(HttpServletResponse response, @RequestBody Map<String, String> data) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .success(true)
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
