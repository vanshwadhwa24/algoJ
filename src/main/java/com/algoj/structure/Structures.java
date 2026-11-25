package com.algoj.structure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registry of the interactive data structure engines, looked up by id. */
public final class Structures {

    private static final Map<String, StructureEngine<?>> BY_ID = new LinkedHashMap<>();

    static {
        List.<StructureEngine<?>>of(
                new ArrayStructureEngine(), new LinkedListStructureEngine(), new StackStructureEngine(),
                new QueueStructureEngine(), new HashTableStructureEngine(), new BSTStructureEngine(),
                new AVLStructureEngine(), new HeapStructureEngine(), new TrieStructureEngine()
        ).forEach(s -> BY_ID.put(s.id(), s));
    }

    private Structures() {}

    public static StructureEngine<?> get(String id) {
        StructureEngine<?> s = BY_ID.get(id);
        if (s == null) throw new IllegalArgumentException("Unsupported structure ID: " + id);
        return s;
    }

    public static boolean exists(String id) {
        return BY_ID.containsKey(id);
    }
}
