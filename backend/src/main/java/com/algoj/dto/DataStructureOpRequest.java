package com.algoj.dto;

import java.util.Map;

public record DataStructureOpRequest(
        String structureId,
        String operation, // access, insert, delete, search, traverse, push, pop, peek, etc.
        Object value,
        Integer index,
        Integer position,
        String key,
        String word,
        Map<String, Object> state,
        Map<String, Object> options) {}
