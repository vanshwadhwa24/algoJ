package com.algoj.dto;

import java.util.List;
import java.util.Map;

public record AlgorithmRunRequest(
        String algorithmId,
        List<Integer> array,
        Integer target,
        Integer n,
        String mode, // e.g. recursive, memoized, tabulated
        Integer disks,
        String text1,
        String text2,
        Map<String, Object> graph,
        Map<String, Object> options) {}
