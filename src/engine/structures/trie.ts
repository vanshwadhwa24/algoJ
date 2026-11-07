import { ExecutionStep } from '../../types/execution';

export interface TrieNode {
  id: string;
  char: string;
  isEndOfWord: boolean;
  children: Record<string, TrieNode>;
  highlight?: 'default' | 'active' | 'found' | 'inserted' | 'deleted';
  x?: number;
  y?: number;
}

export interface TrieDSState {
  root: TrieNode;
  wordCount: number;
  wordsList: string[];
  activePrefix: string;
  searchResults: string[];
  message: string;
}

export function cloneTrieNode(node: TrieNode): TrieNode {
  const clonedChildren: Record<string, TrieNode> = {};
  for (const key of Object.keys(node.children)) {
    clonedChildren[key] = cloneTrieNode(node.children[key]);
  }
  return {
    id: node.id,
    char: node.char,
    isEndOfWord: node.isEndOfWord,
    highlight: node.highlight,
    x: node.x,
    y: node.y,
    children: clonedChildren,
  };
}

export function cloneTrieDS(state: TrieDSState): TrieDSState {
  return {
    root: cloneTrieNode(state.root),
    wordCount: state.wordCount,
    wordsList: [...state.wordsList],
    activePrefix: state.activePrefix,
    searchResults: [...state.searchResults],
    message: state.message,
  };
}

export function createInitialTrie(initialWords: string[] = ['cat', 'car', 'cart', 'dog']): TrieDSState {
  const root: TrieNode = {
    id: 'root',
    char: 'ROOT',
    isEndOfWord: false,
    children: {},
    highlight: 'default',
  };

  const state: TrieDSState = {
    root,
    wordCount: 0,
    wordsList: [],
    activePrefix: '',
    searchResults: [],
    message: 'Trie initialized.',
  };

  function insertRaw(word: string) {
    let curr = root;
    for (const ch of word.toLowerCase()) {
      if (!curr.children[ch]) {
        curr.children[ch] = {
          id: `trie-${ch}-${Math.random()}`,
          char: ch,
          isEndOfWord: false,
          children: {},
          highlight: 'default',
        };
      }
      curr = curr.children[ch];
    }
    curr.isEndOfWord = true;
    if (!state.wordsList.includes(word)) {
      state.wordsList.push(word);
      state.wordCount++;
    }
  }

  initialWords.forEach(insertRaw);
  return state;
}

export function getAllWordsWithPrefix(node: TrieNode, prefix: string): string[] {
  const results: string[] = [];
  function dfs(curr: TrieNode, currentWord: string) {
    if (curr.isEndOfWord) results.push(currentWord);
    for (const ch of Object.keys(curr.children).sort()) {
      dfs(curr.children[ch], currentWord + ch);
    }
  }
  dfs(node, prefix);
  return results;
}

export function executeTrieOperation(
  currentState: TrieDSState,
  operation: {
    type: 'insert' | 'searchWord' | 'searchPrefix' | 'delete' | 'autocomplete';
    word?: string;
  }
): ExecutionStep<TrieDSState>[] {
  const steps: ExecutionStep<TrieDSState>[] = [];
  const state = cloneTrieDS(currentState);
  const word = (operation.word || '').toLowerCase().trim();

  if (!word) {
    steps.push({
      stepIndex: 0,
      operationType: 'error',
      description: 'Word or prefix cannot be empty!',
      codeLine: 1,
      state: cloneTrieDS(state),
    });
    return steps;
  }

  if (operation.type === 'insert') {
    steps.push({
      stepIndex: 0,
      operationType: 'insert-start',
      description: `Starting insertion of word "${word}" into Trie.`,
      codeLine: 1,
      state: cloneTrieDS(state),
    });

    let curr = state.root;
    for (let i = 0; i < word.length; i++) {
      const ch = word[i];
      const isNew = !curr.children[ch];

      if (isNew) {
        curr.children[ch] = {
          id: `trie-${ch}-${Date.now()}-${i}`,
          char: ch,
          isEndOfWord: false,
          children: {},
          highlight: 'inserted',
        };
      } else {
        curr.children[ch].highlight = 'active';
      }

      steps.push({
        stepIndex: steps.length,
        operationType: isNew ? 'created-node' : 'reused-node',
        description: isNew
          ? `Created new branch node for char '${ch}' at depth ${i + 1}.`
          : `Reused shared prefix character '${ch}' at depth ${i + 1}.`,
        codeLine: 3,
        state: cloneTrieDS(state),
      });

      curr = curr.children[ch];
    }

    curr.isEndOfWord = true;
    curr.highlight = 'found';
    if (!state.wordsList.includes(word)) {
      state.wordsList.push(word);
      state.wordCount++;
    }

    steps.push({
      stepIndex: steps.length,
      operationType: 'mark-terminal',
      description: `Marked end-of-word on '${word[word.length - 1]}'. Word "${word}" successfully stored in O(length) time.`,
      codeLine: 5,
      state: cloneTrieDS(state),
    });
  } else if (operation.type === 'searchWord') {
    steps.push({
      stepIndex: 0,
      operationType: 'search-start',
      description: `Searching for complete word "${word}".`,
      codeLine: 1,
      state: cloneTrieDS(state),
    });

    let curr: TrieNode | null = state.root;
    let found = true;

    for (let i = 0; i < word.length; i++) {
      const ch = word[i];
      if (!curr?.children[ch]) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'mismatch',
          description: `Character '${ch}' not found at level ${i + 1}. Word "${word}" does not exist in Trie.`,
          codeLine: 3,
          state: cloneTrieDS(state),
        });
        found = false;
        break;
      }

      curr = curr.children[ch];
      curr.highlight = 'active';
      steps.push({
        stepIndex: steps.length,
        operationType: 'match-char',
        description: `Matched '${ch}' at depth ${i + 1}. Continuing path...`,
        codeLine: 2,
        state: cloneTrieDS(state),
      });
    }

    if (found && curr?.isEndOfWord) {
      curr.highlight = 'found';
      steps.push({
        stepIndex: steps.length,
        operationType: 'word-found',
        description: `Complete word "${word}" found in Trie! (terminal flag is TRUE).`,
        codeLine: 4,
        state: cloneTrieDS(state),
      });
    } else if (found && !curr?.isEndOfWord) {
      steps.push({
        stepIndex: steps.length,
        operationType: 'prefix-only',
        description: `Prefix "${word}" exists, but it is NOT marked as a complete word.`,
        codeLine: 5,
        state: cloneTrieDS(state),
      });
    }
  } else if (operation.type === 'searchPrefix') {
    steps.push({
      stepIndex: 0,
      operationType: 'prefix-start',
      description: `Checking if any words start with prefix "${word}".`,
      codeLine: 1,
      state: cloneTrieDS(state),
    });

    let curr: TrieNode | null = state.root;
    let valid = true;

    for (let i = 0; i < word.length; i++) {
      const ch = word[i];
      if (!curr?.children[ch]) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'prefix-missing',
          description: `Prefix broken at '${ch}'. No words with prefix "${word}" exist.`,
          codeLine: 3,
          state: cloneTrieDS(state),
        });
        valid = false;
        break;
      }
      curr = curr.children[ch];
      curr.highlight = 'active';
      steps.push({
        stepIndex: steps.length,
        operationType: 'prefix-match-step',
        description: `Matched prefix character '${ch}'.`,
        codeLine: 2,
        state: cloneTrieDS(state),
      });
    }

    if (valid && curr) {
      curr.highlight = 'found';
      steps.push({
        stepIndex: steps.length,
        operationType: 'prefix-success',
        description: `Prefix "${word}" exists in Trie!`,
        codeLine: 4,
        state: cloneTrieDS(state),
      });
    }
  } else if (operation.type === 'autocomplete') {
    steps.push({
      stepIndex: 0,
      operationType: 'autocomplete-start',
      description: `Finding all completions for prefix "${word}".`,
      codeLine: 1,
      state: cloneTrieDS(state),
    });

    let curr: TrieNode | null = state.root;
    let found = true;

    for (let i = 0; i < word.length; i++) {
      const ch = word[i];
      if (!curr?.children[ch]) {
        found = false;
        break;
      }
      curr = curr.children[ch];
    }

    if (!found || !curr) {
      state.searchResults = [];
      steps.push({
        stepIndex: 1,
        operationType: 'no-completions',
        description: `No words found starting with "${word}".`,
        codeLine: 3,
        state: cloneTrieDS(state),
      });
    } else {
      const matches = getAllWordsWithPrefix(curr, word);
      state.searchResults = matches;
      steps.push({
        stepIndex: 1,
        operationType: 'completions-found',
        description: `Found ${matches.length} completion(s): [${matches.join(', ')}].`,
        codeLine: 4,
        state: cloneTrieDS(state),
      });
    }
  } else if (operation.type === 'delete') {
    steps.push({
      stepIndex: 0,
      operationType: 'delete-start',
      description: `Deleting word "${word}" from Trie. Shared prefixes will be preserved.`,
      codeLine: 1,
      state: cloneTrieDS(state),
    });

    function deleteRec(curr: TrieNode, depth: number): boolean {
      if (depth === word.length) {
        if (!curr.isEndOfWord) return false;
        curr.isEndOfWord = false;
        return Object.keys(curr.children).length === 0;
      }

      const ch = word[depth];
      if (!curr.children[ch]) return false;

      const shouldPrune = deleteRec(curr.children[ch], depth + 1);
      if (shouldPrune) {
        delete curr.children[ch];
        return !curr.isEndOfWord && Object.keys(curr.children).length === 0;
      }
      return false;
    }

    deleteRec(state.root, 0);
    state.wordsList = state.wordsList.filter((w) => w !== word);
    state.wordCount = state.wordsList.length;

    steps.push({
      stepIndex: 1,
      operationType: 'delete-complete',
      description: `Word "${word}" deleted. Pruned unneeded nodes while preserving other words.`,
      codeLine: 5,
      state: cloneTrieDS(state),
    });
  }

  return steps;
}
