package com.victor.iatms.controller;

import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public ResponseVO<Integer> test(){
        testService.test();
        return ResponseVO.success("",null);
    }
}
