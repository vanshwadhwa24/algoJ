import { ExecutionStep } from '../../types/execution';

export interface BSTNode {
  id: string;
  value: number;
  left: BSTNode | null;
  right: BSTNode | null;
  highlight?: 'default' | 'active' | 'found' | 'inserted' | 'deleted' | 'traversed';
  x?: number;
  y?: number;
}

export interface BSTDSState {
  root: BSTNode | null;
  activeNodeId: string | null;
  traversalOutput?: number[];
  height: number;
  nodeCount: number;
  leafCount: number;
  message: string;
}

export function cloneBSTNode(node: BSTNode | null): BSTNode | null {
  if (!node) return null;
  return {
    id: node.id,
    value: node.value,
    highlight: node.highlight,
    x: node.x,
    y: node.y,
    left: cloneBSTNode(node.left),
    right: cloneBSTNode(node.right),
  };
}

export function cloneBSTDS(state: BSTDSState): BSTDSState {
  return {
    root: cloneBSTNode(state.root),
    activeNodeId: state.activeNodeId,
    traversalOutput: state.traversalOutput ? [...state.traversalOutput] : undefined,
    height: state.height,
    nodeCount: state.nodeCount,
    leafCount: state.leafCount,
    message: state.message,
  };
}

export function calculateTreeStats(root: BSTNode | null): { height: number; nodeCount: number; leafCount: number } {
  if (!root) return { height: 0, nodeCount: 0, leafCount: 0 };

  const leftStats = calculateTreeStats(root.left);
  const rightStats = calculateTreeStats(root.right);

  const isLeaf = !root.left && !root.right;
  return {
    height: 1 + Math.max(leftStats.height, rightStats.height),
    nodeCount: 1 + leftStats.nodeCount + rightStats.nodeCount,
    leafCount: (isLeaf ? 1 : 0) + leftStats.leafCount + rightStats.leafCount,
  };
}

// Compute tree layout coordinates (x, y)
export function layoutTree(root: BSTNode | null, x = 400, y = 60, spread = 180): void {
  if (!root) return;
  root.x = x;
  root.y = y;
  if (root.left) layoutTree(root.left, x - spread, y + 80, spread * 0.55);
  if (root.right) layoutTree(root.right, x + spread, y + 80, spread * 0.55);
}

export function createInitialBST(initialValues: number[] = [50, 30, 70, 20, 40, 60, 80]): BSTDSState {
  let root: BSTNode | null = null;

  function insertRaw(node: BSTNode | null, val: number): BSTNode {
    if (!node) return { id: `node-${val}-${Math.random()}`, value: val, left: null, right: null };
    if (val < node.value) node.left = insertRaw(node.left, val);
    else if (val > node.value) node.right = insertRaw(node.right, val);
    return node;
  }

  initialValues.forEach((val) => {
    root = insertRaw(root, val);
  });

  layoutTree(root);
  const stats = calculateTreeStats(root);

  return {
    root,
    activeNodeId: null,
    height: stats.height,
    nodeCount: stats.nodeCount,
    leafCount: stats.leafCount,
    message: 'Binary Search Tree initialized.',
  };
}

export function executeBSTOperation(
  currentState: BSTDSState,
  operation: {
    type:
      | 'insert'
      | 'search'
      | 'delete'
      | 'findMin'
      | 'findMax'
      | 'preorder'
      | 'inorder'
      | 'postorder'
      | 'levelorder';
    value?: number;
  }
): ExecutionStep<BSTDSState>[] {
  const steps: ExecutionStep<BSTDSState>[] = [];
  const state = cloneBSTDS(currentState);

  if (operation.type === 'insert') {
    const val = operation.value ?? 45;
    steps.push({
      stepIndex: 0,
      operationType: 'insert-start',
      description: `Starting insertion of ${val} into BST.`,
      codeLine: 1,
      state: cloneBSTDS(state),
    });

    if (!state.root) {
      state.root = { id: `node-${val}-${Date.now()}`, value: val, left: null, right: null, highlight: 'inserted' };
      layoutTree(state.root);
      const stats = calculateTreeStats(state.root);
      state.height = stats.height;
      state.nodeCount = stats.nodeCount;
      state.leafCount = stats.leafCount;

      steps.push({
        stepIndex: 1,
        operationType: 'inserted-root',
        description: `Tree was empty. Inserted ${val} as new root.`,
        codeLine: 2,
        state: cloneBSTDS(state),
      });
      return steps;
    }

    let curr: BSTNode | null = state.root;
    while (curr) {
      curr.highlight = 'active';
      const stepState = cloneBSTDS(state);

      if (val === curr.value) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'duplicate',
          description: `Value ${val} already exists in BST! BST does not insert duplicates.`,
          codeLine: 3,
          state: stepState,
        });
        return steps;
      } else if (val < curr.value) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'go-left',
          description: `${val} < ${curr.value}: moving to LEFT child.`,
          codeLine: 4,
          state: stepState,
        });

        if (!curr.left) {
          curr.left = { id: `node-${val}-${Date.now()}`, value: val, left: null, right: null, highlight: 'inserted' };
          break;
        }
        curr.highlight = 'default';
        curr = curr.left;
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'go-right',
          description: `${val} > ${curr.value}: moving to RIGHT child.`,
          codeLine: 6,
          state: stepState,
        });

        if (!curr.right) {
          curr.right = { id: `node-${val}-${Date.now()}`, value: val, left: null, right: null, highlight: 'inserted' };
          break;
        }
        curr.highlight = 'default';
        curr = curr.right;
      }
    }

    layoutTree(state.root);
    const stats = calculateTreeStats(state.root);
    state.height = stats.height;
    state.nodeCount = stats.nodeCount;
    state.leafCount = stats.leafCount;

    steps.push({
      stepIndex: steps.length,
      operationType: 'inserted',
      description: `Successfully inserted ${val}. Tree height is now ${stats.height}.`,
      codeLine: 8,
      state: cloneBSTDS(state),
    });
  } else if (operation.type === 'search') {
    const val = operation.value ?? 40;
    steps.push({
      stepIndex: 0,
      operationType: 'search-start',
      description: `Searching for value ${val} in BST. Starting at root (${state.root?.value}).`,
      codeLine: 1,
      state: cloneBSTDS(state),
    });

    let curr = state.root;
    let found = false;

    while (curr) {
      curr.highlight = 'active';
      const stepState = cloneBSTDS(state);

      if (curr.value === val) {
        curr.highlight = 'found';
        steps.push({
          stepIndex: steps.length,
          operationType: 'found',
          description: `Found node with value ${val}! O(h) search succeeded.`,
          codeLine: 3,
          state: cloneBSTDS(state),
        });
        found = true;
        break;
      } else if (val < curr.value) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-left',
          description: `${val} < ${curr.value}: target must be in LEFT subtree.`,
          codeLine: 5,
          state: stepState,
        });
        curr.highlight = 'default';
        curr = curr.left;
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-right',
          description: `${val} > ${curr.value}: target must be in RIGHT subtree.`,
          codeLine: 7,
          state: stepState,
        });
        curr.highlight = 'default';
        curr = curr.right;
      }
    }

    if (!found) {
      steps.push({
        stepIndex: steps.length,
        operationType: 'not-found',
        description: `Value ${val} not found in BST (reached null pointer).`,
        codeLine: 9,
        state: cloneBSTDS(state),
      });
    }
  } else if (operation.type === 'delete') {
    const val = operation.value ?? 30;
    steps.push({
      stepIndex: 0,
      operationType: 'delete-start',
      description: `Deleting value ${val} from BST.`,
      codeLine: 1,
      state: cloneBSTDS(state),
    });

    function deleteNode(node: BSTNode | null, v: number): BSTNode | null {
      if (!node) return null;
      if (v < node.value) {
        node.left = deleteNode(node.left, v);
        return node;
      } else if (v > node.value) {
        node.right = deleteNode(node.right, v);
        return node;
      }

      // Found node to delete
      // Case 1: Leaf node
      if (!node.left && !node.right) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'delete-leaf',
          description: `Node ${v} is a LEAF node. Directly removed without child adjustments.`,
          codeLine: 4,
          state: cloneBSTDS(state),
        });
        return null;
      }
      // Case 2: One child
      if (!node.left) {
        const rightChild = node.right!;
        steps.push({
          stepIndex: steps.length,
          operationType: 'delete-one-child',
          description: `Node ${v} has only RIGHT child (${rightChild.value}). Replacing node with its right child.`,
          codeLine: 6,
          state: cloneBSTDS(state),
        });
        return rightChild;
      }
      if (!node.right) {
        const leftChild = node.left!;
        steps.push({
          stepIndex: steps.length,
          operationType: 'delete-one-child',
          description: `Node ${v} has only LEFT child (${leftChild.value}). Replacing node with its left child.`,
          codeLine: 8,
          state: cloneBSTDS(state),
        });
        return leftChild;
      }

      // Case 3: Two children
      // Find inorder successor (minimum in right subtree)
      let succ: BSTNode = node.right;
      while (succ.left) succ = succ.left;

      steps.push({
        stepIndex: steps.length,
        operationType: 'delete-two-children',
        description: `Node ${v} has TWO children! Found inorder successor ${succ.value}. Replacing ${v} with ${succ.value} and deleting successor from right subtree.`,
        codeLine: 10,
        state: cloneBSTDS(state),
      });

      node.value = succ.value;
      node.right = deleteNode(node.right, succ.value);
      return node;
    }

    state.root = deleteNode(state.root, val);
    layoutTree(state.root);
    const stats = calculateTreeStats(state.root);
    state.height = stats.height;
    state.nodeCount = stats.nodeCount;
    state.leafCount = stats.leafCount;

    steps.push({
      stepIndex: steps.length,
      operationType: 'delete-complete',
      description: `BST deletion finished. Invariant properties preserved.`,
      codeLine: 12,
      state: cloneBSTDS(state),
    });
  } else if (operation.type === 'findMin' || operation.type === 'findMax') {
    const isMin = operation.type === 'findMin';
    if (!state.root) {
      steps.push({
        stepIndex: 0,
        operationType: 'empty',
        description: 'Tree is empty!',
        codeLine: 1,
        state: cloneBSTDS(state),
      });
      return steps;
    }

    let curr = state.root;
    while (isMin ? curr.left : curr.right) {
      curr.highlight = 'active';
      steps.push({
        stepIndex: steps.length,
        operationType: isMin ? 'go-left' : 'go-right',
        description: `Following ${isMin ? 'left' : 'right'} pointer: current node ${curr.value}.`,
        codeLine: 2,
        state: cloneBSTDS(state),
      });
      curr.highlight = 'default';
      curr = isMin ? curr.left! : curr.right!;
    }

    curr.highlight = 'found';
    steps.push({
      stepIndex: steps.length,
      operationType: 'extremum-found',
      description: `Found ${isMin ? 'Minimum' : 'Maximum'} value: ${curr.value}!`,
      codeLine: 4,
      state: cloneBSTDS(state),
    });
  } else if (
    operation.type === 'preorder' ||
    operation.type === 'inorder' ||
    operation.type === 'postorder' ||
    operation.type === 'levelorder'
  ) {
    const traversal: number[] = [];
    state.traversalOutput = traversal;

    steps.push({
      stepIndex: 0,
      operationType: 'traversal-start',
      description: `Starting ${operation.type.toUpperCase()} traversal.`,
      codeLine: 1,
      state: cloneBSTDS(state),
    });

    if (operation.type === 'levelorder') {
      const q: BSTNode[] = state.root ? [state.root] : [];
      while (q.length > 0) {
        const n = q.shift()!;
        n.highlight = 'traversed';
        traversal.push(n.value);
        steps.push({
          stepIndex: steps.length,
          operationType: 'visit',
          description: `Visited ${n.value}. Output: [${traversal.join(', ')}].`,
          codeLine: 3,
          state: cloneBSTDS(state),
        });
        if (n.left) q.push(n.left);
        if (n.right) q.push(n.right);
      }
    } else {
      function traverseRec(node: BSTNode | null) {
        if (!node) return;
        if (operation.type === 'preorder') {
          node.highlight = 'traversed';
          traversal.push(node.value);
          steps.push({
            stepIndex: steps.length,
            operationType: 'visit',
            description: `Preorder visit node ${node.value} (Root). Output: [${traversal.join(', ')}].`,
            codeLine: 3,
            state: cloneBSTDS(state),
          });
          traverseRec(node.left);
          traverseRec(node.right);
        } else if (operation.type === 'inorder') {
          traverseRec(node.left);
          node.highlight = 'traversed';
          traversal.push(node.value);
          steps.push({
            stepIndex: steps.length,
            operationType: 'visit',
            description: `Inorder visit node ${node.value} (Sorted BST order). Output: [${traversal.join(', ')}].`,
            codeLine: 5,
            state: cloneBSTDS(state),
          });
          traverseRec(node.right);
        } else {
          traverseRec(node.left);
          traverseRec(node.right);
          node.highlight = 'traversed';
          traversal.push(node.value);
          steps.push({
            stepIndex: steps.length,
            operationType: 'visit',
            description: `Postorder visit node ${node.value} (After children). Output: [${traversal.join(', ')}].`,
            codeLine: 7,
            state: cloneBSTDS(state),
          });
        }
      }
      traverseRec(state.root);
    }
  }

  return steps;
}
