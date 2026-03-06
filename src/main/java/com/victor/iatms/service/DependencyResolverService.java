package com.victor.iatms.service;

import com.victor.iatms.entity.dto.PreConditionsDTO;
import com.victor.iatms.entity.dto.TestCaseDependencyDTO;
import com.victor.iatms.entity.po.TestCase;

import java.util.List;
import java.util.Map;

public interface DependencyResolverService {

    TestCaseDependencyDTO.DependencyGraph buildDependencyGraph(List<TestCase> testCases);

    List<List<TestCase>> getExecutionLayers(List<TestCase> testCases);

    List<TestCase> sortTestCasesByDependency(List<TestCase> testCases);

    Map<String, Object> parsePreConditions(String preConditionsJson);

    List<String> extractRequiredVariables(String requestOverride);

    List<String> extractProvidedVariables(String extractors);

    boolean validateDependencies(TestCaseDependencyDTO.DependencyGraph graph);

    List<TestCaseDependencyDTO.DependencyEdge> findCircularDependencies(TestCaseDependencyDTO.DependencyGraph graph);

    PreConditionsDTO buildPreConditionsDTO(String description, List<String> requiredVariables, Integer executionOrder);

    TestCaseDependencyDTO analyzeTestCaseDependency(TestCase testCase);
}
