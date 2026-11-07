import { ExecutionStep } from '../../types/execution';

export interface AVLNode {
  id: string;
  value: number;
  height: number;
  balanceFactor: number;
  left: AVLNode | null;
  right: AVLNode | null;
  highlight?: 'default' | 'active' | 'imbalanced' | 'rotating' | 'inserted' | 'deleted';
  x?: number;
  y?: number;
}

export interface AVLDSState {
  root: AVLNode | null;
  message: string;
  isBalanced: boolean;
}

function getNodeHeight(node: AVLNode | null): number {
  return node ? node.height : 0;
}

function updateHeightAndBF(node: AVLNode): void {
  const leftH = getNodeHeight(node.left);
  const rightH = getNodeHeight(node.right);
  node.height = 1 + Math.max(leftH, rightH);
  node.balanceFactor = leftH - rightH;
}

export function cloneAVLNode(node: AVLNode | null): AVLNode | null {
  if (!node) return null;
  return {
    id: node.id,
    value: node.value,
    height: node.height,
    balanceFactor: node.balanceFactor,
    highlight: node.highlight,
    x: node.x,
    y: node.y,
    left: cloneAVLNode(node.left),
    right: cloneAVLNode(node.right),
  };
}

export function cloneAVLDS(state: AVLDSState): AVLDSState {
  return {
    root: cloneAVLNode(state.root),
    message: state.message,
    isBalanced: state.isBalanced,
  };
}

export function layoutAVLTree(root: AVLNode | null, x = 400, y = 60, spread = 180): void {
  if (!root) return;
  root.x = x;
  root.y = y;
  if (root.left) layoutAVLTree(root.left, x - spread, y + 80, spread * 0.55);
  if (root.right) layoutAVLTree(root.right, x + spread, y + 80, spread * 0.55);
}

// Right rotation (LL case)
function rightRotate(y: AVLNode): AVLNode {
  const x = y.left!;
  const T2 = x.right;
  x.right = y;
  y.left = T2;
  updateHeightAndBF(y);
  updateHeightAndBF(x);
  return x;
}

// Left rotation (RR case)
function leftRotate(x: AVLNode): AVLNode {
  const y = x.right!;
  const T2 = y.left;
  y.left = x;
  x.right = T2;
  updateHeightAndBF(x);
  updateHeightAndBF(y);
  return y;
}

export function createInitialAVL(values: number[] = [30, 20, 40, 10, 25]): AVLDSState {
  let root: AVLNode | null = null;

  function insert(node: AVLNode | null, val: number): AVLNode {
    if (!node) {
      const newNode: AVLNode = {
        id: `avl-${val}-${Math.random()}`,
        value: val,
        height: 1,
        balanceFactor: 0,
        left: null,
        right: null,
      };
      return newNode;
    }

    if (val < node.value) node.left = insert(node.left, val);
    else if (val > node.value) node.right = insert(node.right, val);
    else return node;

    updateHeightAndBF(node);
    const bf = node.balanceFactor;

    // LL
    if (bf > 1 && val < node.left!.value) return rightRotate(node);
    // RR
    if (bf < -1 && val > node.right!.value) return leftRotate(node);
    // LR
    if (bf > 1 && val > node.left!.value) {
      node.left = leftRotate(node.left!);
      return rightRotate(node);
    }
    // RL
    if (bf < -1 && val < node.right!.value) {
      node.right = rightRotate(node.right!);
      return leftRotate(node);
    }

    return node;
  }

  values.forEach((v) => {
    root = insert(root, v);
  });

  layoutAVLTree(root);
  return {
    root,
    message: 'AVL Tree initialized. All balance factors satisfy -1 <= BF <= 1.',
    isBalanced: true,
  };
}

export function executeAVLOperation(
  currentState: AVLDSState,
  operation: {
    type: 'insert' | 'search' | 'delete';
    value?: number;
  }
): ExecutionStep<AVLDSState>[] {
  const steps: ExecutionStep<AVLDSState>[] = [];
  const state = cloneAVLDS(currentState);

  if (operation.type === 'insert') {
    const val = operation.value ?? 50;

    steps.push({
      stepIndex: 0,
      operationType: 'insert-start',
      description: `Inserting ${val} into AVL tree. Will verify balance factors on backtrack.`,
      codeLine: 1,
      state: cloneAVLDS(state),
    });

    function insertWithSteps(node: AVLNode | null): AVLNode {
      if (!node) {
        const newNode: AVLNode = {
          id: `avl-${val}-${Date.now()}`,
          value: val,
          height: 1,
          balanceFactor: 0,
          left: null,
          right: null,
          highlight: 'inserted',
        };
        steps.push({
          stepIndex: steps.length,
          operationType: 'node-created',
          description: `Placed new leaf node ${val} (height = 1, BF = 0).`,
          codeLine: 3,
          state: cloneAVLDS(state),
        });
        return newNode;
      }

      node.highlight = 'active';
      if (val < node.value) {
        node.left = insertWithSteps(node.left);
      } else if (val > node.value) {
        node.right = insertWithSteps(node.right);
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'duplicate',
          description: `Value ${val} already exists in AVL tree.`,
          codeLine: 4,
          state: cloneAVLDS(state),
        });
        return node;
      }

      updateHeightAndBF(node);
      const bf = node.balanceFactor;

      // Check balance
      if (Math.abs(bf) > 1) {
        node.highlight = 'imbalanced';
        steps.push({
          stepIndex: steps.length,
          operationType: 'imbalance-detected',
          description: `Imbalance detected at node ${node.value}! Balance Factor = ${bf} (|BF| > 1). Rebalancing required.`,
          codeLine: 6,
          state: cloneAVLDS(state),
        });

        // LL Rotation
        if (bf > 1 && val < node.left!.value) {
          steps.push({
            stepIndex: steps.length,
            operationType: 'rotate-right',
            description: `Left-Left (LL) case: Performing single RIGHT ROTATION on node ${node.value}.`,
            codeLine: 8,
            state: cloneAVLDS(state),
          });
          const rotated = rightRotate(node);
          layoutAVLTree(rotated);
          return rotated;
        }

        // RR Rotation
        if (bf < -1 && val > node.right!.value) {
          steps.push({
            stepIndex: steps.length,
            operationType: 'rotate-left',
            description: `Right-Right (RR) case: Performing single LEFT ROTATION on node ${node.value}.`,
            codeLine: 10,
            state: cloneAVLDS(state),
          });
          const rotated = leftRotate(node);
          layoutAVLTree(rotated);
          return rotated;
        }

        // LR Rotation
        if (bf > 1 && val > node.left!.value) {
          steps.push({
            stepIndex: steps.length,
            operationType: 'rotate-left-right',
            description: `Left-Right (LR) case: Left rotate child ${node.left!.value}, then Right rotate node ${node.value}.`,
            codeLine: 12,
            state: cloneAVLDS(state),
          });
          node.left = leftRotate(node.left!);
          const rotated = rightRotate(node);
          layoutAVLTree(rotated);
          return rotated;
        }

        // RL Rotation
        if (bf < -1 && val < node.right!.value) {
          steps.push({
            stepIndex: steps.length,
            operationType: 'rotate-right-left',
            description: `Right-Left (RL) case: Right rotate child ${node.right!.value}, then Left rotate node ${node.value}.`,
            codeLine: 14,
            state: cloneAVLDS(state),
          });
          node.right = rightRotate(node.right!);
          const rotated = leftRotate(node);
          layoutAVLTree(rotated);
          return rotated;
        }
      }

      node.highlight = 'default';
      return node;
    }

    state.root = insertWithSteps(state.root);
    layoutAVLTree(state.root);

    steps.push({
      stepIndex: steps.length,
      operationType: 'insert-complete',
      description: `AVL insertion and rebalancing complete. Height is ${getNodeHeight(state.root)}. All nodes have |BF| <= 1.`,
      codeLine: 16,
      state: cloneAVLDS(state),
    });
  } else if (operation.type === 'search') {
    const val = operation.value ?? 25;
    steps.push({
      stepIndex: 0,
      operationType: 'search-start',
      description: `Searching for ${val} in AVL tree. Guaranteed O(log n) worst-case time due to strict balance.`,
      codeLine: 1,
      state: cloneAVLDS(state),
    });

    let curr = state.root;
    let found = false;

    while (curr) {
      curr.highlight = 'active';
      if (curr.value === val) {
        curr.highlight = 'inserted';
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-found',
          description: `Found node ${val} (height: ${curr.height}, BF: ${curr.balanceFactor})!`,
          codeLine: 3,
          state: cloneAVLDS(state),
        });
        found = true;
        break;
      } else if (val < curr.value) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'go-left',
          description: `${val} < ${curr.value}: branch left.`,
          codeLine: 5,
          state: cloneAVLDS(state),
        });
        curr.highlight = 'default';
        curr = curr.left;
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'go-right',
          description: `${val} > ${curr.value}: branch right.`,
          codeLine: 7,
          state: cloneAVLDS(state),
        });
        curr.highlight = 'default';
        curr = curr.right;
      }
    }

    if (!found) {
      steps.push({
        stepIndex: steps.length,
        operationType: 'search-fail',
        description: `Value ${val} not found in AVL tree.`,
        codeLine: 9,
        state: cloneAVLDS(state),
      });
    }
  }

  return steps;
}
