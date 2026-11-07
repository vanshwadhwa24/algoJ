import { ExecutionStep } from '../../types/execution';

export interface ListNode {
  id: string;
  value: number;
  highlight?: 'default' | 'active' | 'inserted' | 'deleted' | 'new';
}

export interface LinkedListDSState {
  isDoubly: boolean;
  nodes: ListNode[];
  activeNodeId: string | null;
  message: string;
}

export function createInitialLinkedList(
  values: number[],
  isDoubly: boolean = false
): LinkedListDSState {
  return {
    isDoubly,
    nodes: values.map((val, idx) => ({
      id: `node-${idx}-${val}`,
      value: val,
      highlight: 'default',
    })),
    activeNodeId: null,
    message: `${isDoubly ? 'Doubly' : 'Singly'} Linked List initialized.`,
  };
}

export function cloneLinkedListDS(state: LinkedListDSState): LinkedListDSState {
  return {
    isDoubly: state.isDoubly,
    nodes: state.nodes.map((n) => ({ ...n })),
    activeNodeId: state.activeNodeId,
    message: state.message,
  };
}

export function executeLinkedListOperation(
  currentState: LinkedListDSState,
  operation: {
    type:
      | 'insertHead'
      | 'insertTail'
      | 'insertAt'
      | 'deleteHead'
      | 'deleteTail'
      | 'deleteAt'
      | 'search'
      | 'reverse'
      | 'traverse';
    value?: number;
    position?: number;
  }
): ExecutionStep<LinkedListDSState>[] {
  const steps: ExecutionStep<LinkedListDSState>[] = [];
  const state = cloneLinkedListDS(currentState);

  if (operation.type === 'insertHead') {
    const val = operation.value ?? 42;
    const newNode: ListNode = {
      id: `node-${Date.now()}-${val}`,
      value: val,
      highlight: 'new',
    };

    steps.push({
      stepIndex: 0,
      operationType: 'create-node',
      description: `Created new node with value ${val}. Preparing to point its next to current head.`,
      codeLine: 2,
      state: {
        ...cloneLinkedListDS(state),
        nodes: [newNode, ...state.nodes],
        activeNodeId: newNode.id,
      },
    });

    newNode.highlight = 'inserted';
    const finalNodes = [newNode, ...state.nodes.map((n) => ({ ...n, highlight: 'default' as const }))];
    steps.push({
      stepIndex: 1,
      operationType: 'update-head',
      description: `Updated head pointer to new node (${val}). O(1) time complexity.`,
      codeLine: 4,
      state: {
        ...state,
        nodes: finalNodes,
        activeNodeId: newNode.id,
      },
    });
  } else if (operation.type === 'insertTail') {
    const val = operation.value ?? 99;
    const newNode: ListNode = {
      id: `node-${Date.now()}-${val}`,
      value: val,
      highlight: 'new',
    };

    steps.push({
      stepIndex: 0,
      operationType: 'create-node',
      description: `Created new node with value ${val}. Traversed to tail node.`,
      codeLine: 2,
      state: cloneLinkedListDS(state),
    });

    newNode.highlight = 'inserted';
    const finalNodes = [...state.nodes.map((n) => ({ ...n, highlight: 'default' as const })), newNode];
    steps.push({
      stepIndex: 1,
      operationType: 'link-tail',
      description: `Linked previous tail's next pointer to new node. Tail is now ${val}.`,
      codeLine: 5,
      state: {
        ...state,
        nodes: finalNodes,
        activeNodeId: newNode.id,
      },
    });
  } else if (operation.type === 'insertAt') {
    const val = operation.value ?? 50;
    const pos = Math.max(0, Math.min(state.nodes.length, operation.position ?? 1));

    if (pos === 0) {
      return executeLinkedListOperation(currentState, { type: 'insertHead', value: val });
    }
    if (pos >= state.nodes.length) {
      return executeLinkedListOperation(currentState, { type: 'insertTail', value: val });
    }

    const newNode: ListNode = {
      id: `node-${Date.now()}-${val}`,
      value: val,
      highlight: 'new',
    };

    steps.push({
      stepIndex: 0,
      operationType: 'traverse-pos',
      description: `Traversing to position ${pos} (node with value ${state.nodes[pos - 1]?.value}).`,
      codeLine: 3,
      state: {
        ...cloneLinkedListDS(state),
        activeNodeId: state.nodes[pos - 1]?.id ?? null,
      },
    });

    newNode.highlight = 'inserted';
    const newNodesList = [...state.nodes];
    newNodesList.splice(pos, 0, newNode);

    steps.push({
      stepIndex: 1,
      operationType: 'insert-splice',
      description: `Inserted node ${val} at position ${pos}. Re-linked next and prev pointers. O(pos) time complexity.`,
      codeLine: 6,
      state: {
        ...state,
        nodes: newNodesList,
        activeNodeId: newNode.id,
      },
    });
  } else if (operation.type === 'deleteHead') {
    if (state.nodes.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'underflow',
        description: 'Cannot delete from empty list!',
        codeLine: 1,
        state: cloneLinkedListDS(state),
      });
      return steps;
    }

    const removed = state.nodes[0];
    removed.highlight = 'deleted';
    steps.push({
      stepIndex: 0,
      operationType: 'delete-mark',
      description: `Marking head node (${removed.value}) for deletion. Head will advance to next.`,
      codeLine: 2,
      state: cloneLinkedListDS(state),
    });

    const newNodes = state.nodes.slice(1).map((n) => ({ ...n, highlight: 'default' as const }));
    steps.push({
      stepIndex: 1,
      operationType: 'delete-complete',
      description: `Deleted head node (${removed.value}). Head updated in O(1) time.`,
      codeLine: 4,
      state: {
        ...state,
        nodes: newNodes,
        activeNodeId: null,
      },
    });
  } else if (operation.type === 'deleteTail') {
    if (state.nodes.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'underflow',
        description: 'Cannot delete from empty list!',
        codeLine: 1,
        state: cloneLinkedListDS(state),
      });
      return steps;
    }

    const lastIdx = state.nodes.length - 1;
    state.nodes[lastIdx].highlight = 'deleted';
    steps.push({
      stepIndex: 0,
      operationType: 'delete-mark',
      description: `Traversed to tail node (${state.nodes[lastIdx].value}) for deletion.`,
      codeLine: 3,
      state: cloneLinkedListDS(state),
    });

    const newNodes = state.nodes.slice(0, lastIdx).map((n) => ({ ...n, highlight: 'default' as const }));
    steps.push({
      stepIndex: 1,
      operationType: 'delete-complete',
      description: `Removed tail node. In singly list this is O(n), in doubly list with tail pointer this is O(1).`,
      codeLine: 5,
      state: {
        ...state,
        nodes: newNodes,
        activeNodeId: null,
      },
    });
  } else if (operation.type === 'deleteAt') {
    const pos = Math.max(0, Math.min(state.nodes.length - 1, operation.position ?? 0));
    if (state.nodes.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'underflow',
        description: 'List is empty!',
        codeLine: 1,
        state: cloneLinkedListDS(state),
      });
      return steps;
    }

    const removed = state.nodes[pos];
    removed.highlight = 'deleted';
    steps.push({
      stepIndex: 0,
      operationType: 'delete-mark',
      description: `Traversed to position ${pos} (node ${removed.value}) to delete.`,
      codeLine: 3,
      state: cloneLinkedListDS(state),
    });

    const newNodes = state.nodes.filter((_, i) => i !== pos).map((n) => ({ ...n, highlight: 'default' as const }));
    steps.push({
      stepIndex: 1,
      operationType: 'delete-complete',
      description: `Bypassed node ${removed.value} and updated links.`,
      codeLine: 5,
      state: {
        ...state,
        nodes: newNodes,
        activeNodeId: null,
      },
    });
  } else if (operation.type === 'reverse') {
    if (state.nodes.length <= 1) {
      steps.push({
        stepIndex: 0,
        operationType: 'noop',
        description: 'List has 1 or 0 nodes, already reversed.',
        codeLine: 1,
        state: cloneLinkedListDS(state),
      });
      return steps;
    }

    steps.push({
      stepIndex: 0,
      operationType: 'reverse-start',
      description: 'Starting in-place list reversal. Pointers prev, curr, next will be updated node by node.',
      codeLine: 2,
      state: cloneLinkedListDS(state),
    });

    const reversedNodes = [...state.nodes].reverse().map((n) => ({ ...n, highlight: 'active' as const }));
    steps.push({
      stepIndex: 1,
      operationType: 'reversed',
      description: 'All pointers reversed! New head is ' + reversedNodes[0].value + '. O(n) time, O(1) space.',
      codeLine: 6,
      state: {
        ...state,
        nodes: reversedNodes.map((n) => ({ ...n, highlight: 'default' as const })),
        activeNodeId: reversedNodes[0].id,
      },
    });
  } else if (operation.type === 'search') {
    const target = operation.value ?? 0;
    let found = false;

    steps.push({
      stepIndex: 0,
      operationType: 'search-start',
      description: `Searching for value ${target} in linked list. Starting at head.`,
      codeLine: 1,
      state: cloneLinkedListDS(state),
    });

    for (let i = 0; i < state.nodes.length; i++) {
      const stepState = cloneLinkedListDS(state);
      stepState.nodes[i].highlight = 'active';
      stepState.activeNodeId = stepState.nodes[i].id;

      if (stepState.nodes[i].value === target) {
        stepState.nodes[i].highlight = 'inserted';
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-found',
          description: `Found target ${target} at node index ${i}!`,
          codeLine: 4,
          state: stepState,
        });
        found = true;
        break;
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-step',
          description: `Inspected node with value ${stepState.nodes[i].value}. Does not match ${target}. Moving to next pointer.`,
          codeLine: 3,
          state: stepState,
        });
      }
    }

    if (!found) {
      steps.push({
        stepIndex: steps.length,
        operationType: 'search-not-found',
        description: `Reached null (end of list). Value ${target} not found. O(n) scan.`,
        codeLine: 6,
        state: cloneLinkedListDS(state),
      });
    }
  } else if (operation.type === 'traverse') {
    steps.push({
      stepIndex: 0,
      operationType: 'traverse-start',
      description: 'Starting linked list traversal from head to tail.',
      codeLine: 1,
      state: cloneLinkedListDS(state),
    });

    for (let i = 0; i < state.nodes.length; i++) {
      const stepState = cloneLinkedListDS(state);
      stepState.nodes[i].highlight = 'active';
      stepState.activeNodeId = stepState.nodes[i].id;

      steps.push({
        stepIndex: steps.length,
        operationType: 'traverse-step',
        description: `Visiting node ${i + 1}/${state.nodes.length}: value = ${state.nodes[i].value}.`,
        codeLine: 3,
        state: stepState,
      });
    }
  }

  return steps;
}
