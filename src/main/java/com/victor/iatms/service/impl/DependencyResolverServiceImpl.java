package com.victor.iatms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.dto.PreConditionsDTO;
import com.victor.iatms.entity.dto.TestCaseDependencyDTO;
import com.victor.iatms.entity.dto.VariableDependencyDTO;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.service.DependencyResolverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DependencyResolverServiceImpl implements DependencyResolverService {

    private static final String VARIABLE_PATTERN = "\\{\\{([^}]+)\\}\\}";
    private static final Pattern pattern = Pattern.compile(VARIABLE_PATTERN);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public TestCaseDependencyDTO.DependencyGraph buildDependencyGraph(List<TestCase> testCases) {
        TestCaseDependencyDTO.DependencyGraph graph = new TestCaseDependencyDTO.DependencyGraph();
        
        List<TestCaseDependencyDTO> nodes = new ArrayList<>();
        List<TestCaseDependencyDTO.DependencyEdge> edges = new ArrayList<>();
        
        Map<String, TestCase> variableProviders = new HashMap<>();
        
        for (TestCase testCase : testCases) {
            TestCaseDependencyDTO node = analyzeTestCaseDependency(testCase);
            nodes.add(node);
            
            List<String> providedVars = extractProvidedVariables(testCase.getExtractors());
            for (String var : providedVars) {
                variableProviders.put(var, testCase);
            }
        }
        
        for (TestCaseDependencyDTO node : nodes) {
            if (node.getRequiredVariables() != null) {
                for (String requiredVar : node.getRequiredVariables()) {
                    TestCase provider = variableProviders.get(requiredVar);
                    if (provider != null && !provider.getCaseId().equals(node.getCaseId())) {
                        TestCaseDependencyDTO.DependencyEdge edge = new TestCaseDependencyDTO.DependencyEdge();
                        edge.setFromCaseId(provider.getCaseId());
                        edge.setToCaseId(node.getCaseId());
                        edge.setVariables(Collections.singletonList(requiredVar));
                        edges.add(edge);
                        
                        if (node.getDependsOn() == null) {
                            node.setDependsOn(new ArrayList<>());
                        }
                        if (!node.getDependsOn().contains(provider.getCaseId())) {
                            node.getDependsOn().add(provider.getCaseId());
                        }
                    }
                }
            }
        }
        
        graph.setNodes(nodes);
        graph.setEdges(edges);
        graph.setExecutionLayers(calculateExecutionLayers(nodes, edges));
        
        return graph;
    }

    @Override
    public List<List<TestCase>> getExecutionLayers(List<TestCase> testCases) {
        TestCaseDependencyDTO.DependencyGraph graph = buildDependencyGraph(testCases);
        
        Map<Integer, TestCase> caseMap = testCases.stream()
            .collect(Collectors.toMap(TestCase::getCaseId, tc -> tc));
        
        List<List<TestCase>> layers = new ArrayList<>();
        for (List<Integer> layerIds : graph.getExecutionLayers()) {
            List<TestCase> layer = layerIds.stream()
                .map(caseMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            layers.add(layer);
        }
        
        return layers;
    }

    @Override
    public List<TestCase> sortTestCasesByDependency(List<TestCase> testCases) {
        List<List<TestCase>> layers = getExecutionLayers(testCases);
        List<TestCase> sorted = new ArrayList<>();
        
        for (List<TestCase> layer : layers) {
            sorted.addAll(layer);
        }
        
        return sorted;
    }

    @Override
    public Map<String, Object> parsePreConditions(String preConditionsJson) {
        if (preConditionsJson == null || preConditionsJson.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(preConditionsJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("解析前置条件失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public List<String> extractRequiredVariables(String requestOverride) {
        List<String> variables = new ArrayList<>();
        
        if (requestOverride == null || requestOverride.isEmpty()) {
            return variables;
        }
        
        Matcher matcher = pattern.matcher(requestOverride);
        while (matcher.find()) {
            String varName = matcher.group(1).trim();
            if (!variables.contains(varName)) {
                variables.add(varName);
            }
        }
        
        return variables;
    }

    @Override
    public List<String> extractProvidedVariables(String extractors) {
        List<String> variables = new ArrayList<>();
        
        if (extractors == null || extractors.isEmpty()) {
            return variables;
        }
        
        try {
            Map<String, Object> extractorMap = objectMapper.readValue(extractors, 
                new TypeReference<Map<String, Object>>() {});
            variables.addAll(extractorMap.keySet());
        } catch (JsonProcessingException e) {
            try {
                List<Map<String, Object>> extractorList = objectMapper.readValue(extractors,
                    new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> extractor : extractorList) {
                    Object name = extractor.get("name");
                    if (name != null && !variables.contains(name.toString())) {
                        variables.add(name.toString());
                    }
                }
            } catch (JsonProcessingException ex) {
                log.warn("解析提取器失败: {}", ex.getMessage());
            }
        }
        
        return variables;
    }

    @Override
    public boolean validateDependencies(TestCaseDependencyDTO.DependencyGraph graph) {
        List<TestCaseDependencyDTO.DependencyEdge> circularDeps = findCircularDependencies(graph);
        return circularDeps.isEmpty();
    }

    @Override
    public List<TestCaseDependencyDTO.DependencyEdge> findCircularDependencies(TestCaseDependencyDTO.DependencyGraph graph) {
        List<TestCaseDependencyDTO.DependencyEdge> circularEdges = new ArrayList<>();
        
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (TestCaseDependencyDTO.DependencyEdge edge : graph.getEdges()) {
            adjacencyList.computeIfAbsent(edge.getFromCaseId(), k -> new ArrayList<>())
                .add(edge.getToCaseId());
        }
        
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recursionStack = new HashSet<>();
        
        for (TestCaseDependencyDTO node : graph.getNodes()) {
            if (detectCycle(node.getCaseId(), adjacencyList, visited, recursionStack)) {
                for (TestCaseDependencyDTO.DependencyEdge edge : graph.getEdges()) {
                    if (recursionStack.contains(edge.getFromCaseId()) && 
                        recursionStack.contains(edge.getToCaseId())) {
                        circularEdges.add(edge);
                    }
                }
            }
        }
        
        return circularEdges;
    }

    @Override
    public PreConditionsDTO buildPreConditionsDTO(String description, List<String> requiredVariables, Integer executionOrder) {
        PreConditionsDTO dto = new PreConditionsDTO();
        dto.setDescription(description);
        dto.setExecutionOrder(executionOrder);
        
        if (requiredVariables != null && !requiredVariables.isEmpty()) {
            List<VariableDependencyDTO> varDeps = requiredVariables.stream()
                .map(varName -> {
                    VariableDependencyDTO varDep = new VariableDependencyDTO();
                    varDep.setName(varName);
                    varDep.setRequired(true);
                    return varDep;
                })
                .collect(Collectors.toList());
            dto.setRequiredVariables(varDeps);
        }
        
        return dto;
    }

    @Override
    public TestCaseDependencyDTO analyzeTestCaseDependency(TestCase testCase) {
        TestCaseDependencyDTO dto = new TestCaseDependencyDTO();
        dto.setCaseId(testCase.getCaseId());
        dto.setCaseCode(testCase.getCaseCode());
        dto.setCaseName(testCase.getName());
        
        Map<String, Object> preConditions = parsePreConditions(testCase.getPreConditions());
        if (preConditions.containsKey("executionOrder")) {
            dto.setExecutionOrder((Integer) preConditions.get("executionOrder"));
        }
        
        List<String> requiredVars = extractRequiredVariables(testCase.getRequestOverride());
        dto.setRequiredVariables(requiredVars);
        
        List<String> providedVars = extractProvidedVariables(testCase.getExtractors());
        dto.setProvidedVariables(providedVars);
        
        if (requiredVars.isEmpty()) {
            dto.setDependencyLevel(TestCaseDependencyDTO.DependencyLevel.INDEPENDENT);
        } else if (!providedVars.isEmpty()) {
            dto.setDependencyLevel(TestCaseDependencyDTO.DependencyLevel.PROVIDER);
        } else {
            dto.setDependencyLevel(TestCaseDependencyDTO.DependencyLevel.DEPENDENT);
        }
        
        return dto;
    }

    private List<List<Integer>> calculateExecutionLayers(List<TestCaseDependencyDTO> nodes, 
                                                          List<TestCaseDependencyDTO.DependencyEdge> edges) {
        List<List<Integer>> layers = new ArrayList<>();
        
        Map<Integer, Set<Integer>> dependencies = new HashMap<>();
        Map<Integer, Set<Integer>> dependents = new HashMap<>();
        
        for (TestCaseDependencyDTO node : nodes) {
            dependencies.put(node.getCaseId(), new HashSet<>());
            dependents.put(node.getCaseId(), new HashSet<>());
        }
        
        for (TestCaseDependencyDTO.DependencyEdge edge : edges) {
            dependencies.get(edge.getToCaseId()).add(edge.getFromCaseId());
            dependents.get(edge.getFromCaseId()).add(edge.getToCaseId());
        }
        
        Set<Integer> processed = new HashSet<>();
        
        while (processed.size() < nodes.size()) {
            List<Integer> currentLayer = new ArrayList<>();
            
            for (TestCaseDependencyDTO node : nodes) {
                if (!processed.contains(node.getCaseId())) {
                    Set<Integer> deps = dependencies.get(node.getCaseId());
                    boolean allDepsProcessed = true;
                    for (Integer dep : deps) {
                        if (!processed.contains(dep)) {
                            allDepsProcessed = false;
                            break;
                        }
                    }
                    
                    if (allDepsProcessed) {
                        currentLayer.add(node.getCaseId());
                    }
                }
            }
            
            if (currentLayer.isEmpty()) {
                log.warn("检测到循环依赖，强制添加剩余节点");
                for (TestCaseDependencyDTO node : nodes) {
                    if (!processed.contains(node.getCaseId())) {
                        currentLayer.add(node.getCaseId());
                    }
                }
            }
            
            layers.add(currentLayer);
            processed.addAll(currentLayer);
        }
        
        return layers;
    }

    private boolean detectCycle(Integer node, Map<Integer, List<Integer>> adjacencyList,
                                Set<Integer> visited, Set<Integer> recursionStack) {
        if (recursionStack.contains(node)) {
            return true;
        }
        
        if (visited.contains(node)) {
            return false;
        }
        
        visited.add(node);
        recursionStack.add(node);
        
        List<Integer> neighbors = adjacencyList.getOrDefault(node, Collections.emptyList());
        for (Integer neighbor : neighbors) {
            if (detectCycle(neighbor, adjacencyList, visited, recursionStack)) {
                return true;
            }
        }
        
        recursionStack.remove(node);
        return false;
    }
}
