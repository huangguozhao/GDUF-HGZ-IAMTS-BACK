package com.victor.iatms.service;

import com.victor.iatms.entity.dto.AITestCaseGenerateDTO;
import com.victor.iatms.entity.dto.AITestCaseResultDTO;
import com.victor.iatms.entity.dto.ConfirmTestCaseDTO;

import java.util.List;

public interface AITestCaseService {

    AITestCaseResultDTO generateTestCases(AITestCaseGenerateDTO dto, Integer userId);

    AITestCaseResultDTO getGenerationResult(Long generationId);

    List<Integer> confirmSaveTestCases(ConfirmTestCaseDTO dto, Integer userId);
}
