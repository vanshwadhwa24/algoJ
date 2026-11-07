package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.TreeStructureModel.TrieDSState;
import com.algoj.model.TreeStructureModel.TrieNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrieStructureEngine {

    public TrieDSState createInitial(List<String> words) {
        TrieDSState state = new TrieDSState();
        TrieNode root = new TrieNode("trie-root", "•");
        state.setRoot(root);
        state.setWordsList(new ArrayList<>());

        for (String w : words) {
            insertWord(state, w, null);
        }
        layoutTrie(state.getRoot(), 350.0, 50.0, 160.0);
        state.setMessage("Trie (Prefix Tree) initialized with " + words.size() + " words.");
        return state;
    }

    private void layoutTrie(TrieNode node, double x, double y, double spread) {
        if (node == null) return;
        node.setX(x);
        node.setY(y);

        List<String> keys = new ArrayList<>(node.getChildren().keySet());
        Collections.sort(keys);
        int count = keys.size();
        if (count == 0) return;

        double startX = x - (spread * (count - 1)) / 2.0;
        for (int i = 0; i < count; i++) {
            TrieNode child = node.getChildren().get(keys.get(i));
            double childX = startX + i * spread;
            double childY = y + 70.0;
            layoutTrie(child, childX, childY, spread * 0.55);
        }
    }

    private void insertWord(TrieDSState state, String word, List<ExecutionStep> steps) {
        TrieNode curr = state.getRoot();
        String cleanWord = word.trim().toLowerCase();

        for (int i = 0; i < cleanWord.length(); i++) {
            String ch = String.valueOf(cleanWord.charAt(i));
            if (!curr.getChildren().containsKey(ch)) {
                TrieNode newNode = new TrieNode("trie-" + ch + "-" + System.nanoTime(), ch);
                curr.getChildren().put(ch, newNode);
                if (steps != null) {
                    newNode.setHighlight("inserted");
                    layoutTrie(state.getRoot(), 350.0, 50.0, 160.0);
                    steps.add(new ExecutionStep(
                            steps.size(), "insert-char",
                            String.format("Added edge for character '%s' in word '%s'.", ch, cleanWord),
                            3, state.copy()
                    ));
                    newNode.setHighlight("default");
                }
            }
            curr = curr.getChildren().get(ch);
            if (steps != null) {
                curr.setHighlight("active");
                steps.add(new ExecutionStep(
                        steps.size(), "traverse",
                        String.format("Traversed to character '%s'. Prefix: '%s'.", ch, cleanWord.substring(0, i + 1)),
                        4, state.copy()
                ));
                curr.setHighlight("default");
            }
        }

        curr.setEndOfWord(true);
        if (!state.getWordsList().contains(cleanWord)) {
            state.getWordsList().add(cleanWord);
            state.setWordCount(state.getWordsList().size());
        }

        if (steps != null) {
            curr.setHighlight("found");
            state.setMessage(String.format("Marked node '%s' as terminal (EndOfWord) for '%s'.", curr.getChar(), cleanWord));
            steps.add(new ExecutionStep(steps.size(), "insert-complete", state.getMessage(), 6, state.copy()));
            curr.setHighlight("default");
        }
    }

    public List<ExecutionStep> executeOperation(TrieDSState currentState, String type, String word) {
        List<ExecutionStep> steps = new ArrayList<>();
        TrieDSState state = currentState.copy();
        String w = (word != null && !word.trim().isEmpty()) ? word.trim().toLowerCase() : "cat";

        if ("insert".equalsIgnoreCase(type)) {
            insertWord(state, w, steps);
            layoutTrie(state.getRoot(), 350.0, 50.0, 160.0);
            state.setMessage(String.format("Word '%s' successfully inserted into Trie.", w));
            steps.add(new ExecutionStep(steps.size(), "complete", state.getMessage(), 7, state.copy()));

        } else if ("searchWord".equalsIgnoreCase(type) || "search".equalsIgnoreCase(type)) {
            TrieNode curr = state.getRoot();
            boolean found = true;

            for (int i = 0; i < w.length(); i++) {
                String ch = String.valueOf(w.charAt(i));
                if (!curr.getChildren().containsKey(ch)) {
                    found = false;
                    state.setMessage(String.format("Character '%s' not found under current node. Word '%s' DOES NOT exist.", ch, w));
                    steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 3, state.copy()));
                    break;
                }
                curr = curr.getChildren().get(ch);
                curr.setHighlight("active");
                steps.add(new ExecutionStep(
                        steps.size(), "match-char",
                        String.format("Matched character '%s'. Prefix: '%s'.", ch, w.substring(0, i + 1)),
                        4, state.copy()
                ));
                curr.setHighlight("default");
            }

            if (found) {
                if (curr.isEndOfWord()) {
                    curr.setHighlight("found");
                    state.setMessage(String.format("Word '%s' EXACT MATCH FOUND in Trie (terminal marker = true)!", w));
                    steps.add(new ExecutionStep(steps.size(), "found", state.getMessage(), 6, state.copy()));
                } else {
                    state.setMessage(String.format("Prefix '%s' exists, but is NOT marked as an end of word.", w));
                    steps.add(new ExecutionStep(steps.size(), "prefix-only", state.getMessage(), 7, state.copy()));
                }
            }

        } else if ("startsWith".equalsIgnoreCase(type)) {
            TrieNode curr = state.getRoot();
            boolean exists = true;

            for (int i = 0; i < w.length(); i++) {
                String ch = String.valueOf(w.charAt(i));
                if (!curr.getChildren().containsKey(ch)) {
                    exists = false;
                    break;
                }
                curr = curr.getChildren().get(ch);
            }

            if (exists) {
                List<String> matches = new ArrayList<>();
                collectWords(curr, w, matches);
                state.setSearchResults(matches);
                state.setActivePrefix(w);
                state.setMessage(String.format("Prefix '%s' matches %d words: %s.", w, matches.size(), matches));
                steps.add(new ExecutionStep(steps.size(), "prefix-matches", state.getMessage(), 5, state.copy()));
            } else {
                state.setMessage(String.format("No words with prefix '%s' found.", w));
                steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 6, state.copy()));
            }

        } else if ("delete".equalsIgnoreCase(type)) {
            if (state.getWordsList().contains(w)) {
                state.getWordsList().remove(w);
                state.setWordCount(state.getWordsList().size());
                deleteWordHelper(state.getRoot(), w, 0);
                layoutTrie(state.getRoot(), 350.0, 50.0, 160.0);
                state.setMessage(String.format("Word '%s' deleted from Trie.", w));
                steps.add(new ExecutionStep(steps.size(), "delete-complete", state.getMessage(), 5, state.copy()));
            } else {
                state.setMessage(String.format("Word '%s' does not exist in Trie to delete.", w));
                steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 2, state.copy()));
            }
        }

        clearHighlights(state.getRoot());
        return steps;
    }

    private void collectWords(TrieNode node, String prefix, List<String> result) {
        if (node == null) return;
        if (node.isEndOfWord()) result.add(prefix);
        for (Map.Entry<String, TrieNode> entry : node.getChildren().entrySet()) {
            collectWords(entry.getValue(), prefix + entry.getKey(), result);
        }
    }

    private boolean deleteWordHelper(TrieNode curr, String word, int idx) {
        if (idx == word.length()) {
            if (!curr.isEndOfWord()) return false;
            curr.setEndOfWord(false);
            return curr.getChildren().isEmpty();
        }
        String ch = String.valueOf(word.charAt(idx));
        TrieNode child = curr.getChildren().get(ch);
        if (child == null) return false;

        boolean shouldDeleteChild = deleteWordHelper(child, word, idx + 1);
        if (shouldDeleteChild) {
            curr.getChildren().remove(ch);
            return curr.getChildren().isEmpty() && !curr.isEndOfWord();
        }
        return false;
    }

    private void clearHighlights(TrieNode node) {
        if (node == null) return;
        node.setHighlight("default");
        for (TrieNode child : node.getChildren().values()) {
            clearHighlights(child);
        }
    }
}
