export type SearchElementState =
  | 'default'
  | 'active'
  | 'mid'
  | 'eliminated'
  | 'found'
  | 'not-found';

export interface SearchElement {
  id: string;
  value: number;
  state: SearchElementState;
}

export interface SearchState {
  array: SearchElement[];
  target: number;
  currentIndex: number | null;
  lowIndex?: number | null;
  highIndex?: number | null;
  midIndex?: number | null;
  comparisons: number;
  foundIndex: number | null;
  isSorted: boolean;
}

export function createSearchElements(arr: number[]): SearchElement[] {
  return arr.map((val, idx) => ({
    id: `search-${idx}-${val}`,
    value: val,
    state: 'default',
  }));
}

export function cloneSearchState(state: SearchState): SearchState {
  return {
    array: state.array.map((el) => ({ ...el })),
    target: state.target,
    currentIndex: state.currentIndex,
    lowIndex: state.lowIndex,
    highIndex: state.highIndex,
    midIndex: state.midIndex,
    comparisons: state.comparisons,
    foundIndex: state.foundIndex,
    isSorted: state.isSorted,
  };
}
