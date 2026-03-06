package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.AITestCaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-testcase")
public class AITestCaseController {

    @Autowired
    private AITestCaseService aiTestCaseService;

    @PostMapping("/generate")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<AITestCaseResultDTO> generateTestCases(
            @RequestBody AITestCaseGenerateDTO dto,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        AITestCaseResultDTO result = aiTestCaseService.generateTestCases(dto, userId);
        return ResponseVO.success("生成成功", result);
    }

    @GetMapping("/result/{generationId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<AITestCaseResultDTO> getGenerationResult(
            @PathVariable Long generationId) {
        AITestCaseResultDTO result = aiTestCaseService.getGenerationResult(generationId);
        return ResponseVO.success(result);
    }

    @PostMapping("/confirm")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<List<Integer>> confirmSaveTestCases(
            @RequestBody ConfirmTestCaseDTO dto,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<Integer> caseIds = aiTestCaseService.confirmSaveTestCases(dto, userId);
        return ResponseVO.success("保存成功", caseIds);
    }
}
