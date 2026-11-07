export type ElementVisualState =
  | 'default'
  | 'comparing'
  | 'swapping'
  | 'sorted'
  | 'pivot'
  | 'partition'
  | 'auxiliary';

export interface SortElement {
  id: string;
  value: number;
  state: ElementVisualState;
}

export interface SortState {
  array: SortElement[];
  comparingIndices: number[];
  swappingIndices: number[];
  pivotIndex: number | null;
  sortedIndices: number[];
  auxiliaryArray?: SortElement[];
  comparisons: number;
  swaps: number;
  writes: number;
}

export function createSortElements(arr: number[]): SortElement[] {
  return arr.map((val, idx) => ({
    id: `item-${idx}-${val}`,
    value: val,
    state: 'default',
  }));
}

export function cloneSortState(state: SortState): SortState {
  return {
    array: state.array.map((el) => ({ ...el })),
    comparingIndices: [...state.comparingIndices],
    swappingIndices: [...state.swappingIndices],
    pivotIndex: state.pivotIndex,
    sortedIndices: [...state.sortedIndices],
    auxiliaryArray: state.auxiliaryArray ? state.auxiliaryArray.map((el) => ({ ...el })) : undefined,
    comparisons: state.comparisons,
    swaps: state.swaps,
    writes: state.writes,
  };
}
