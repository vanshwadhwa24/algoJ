import { ExecutionStep } from '../types/execution';

const BACKEND_BASE_URL = 'http://localhost:8080/api';

export interface BackendHealthResponse {
  status: string;
  service: string;
  version: string;
  javaVersion?: string;
  timestamp: number;
}

export interface AlgorithmRunPayload {
  algorithmId: string;
  array?: number[];
  target?: number;
  n?: number;
  mode?: string;
  disks?: number;
  text1?: string;
  text2?: string;
  graph?: any;
  options?: Record<string, any>;
}

export interface DataStructureOpPayload {
  structureId: string;
  operation: string;
  value?: any;
  index?: number;
  position?: number;
  key?: string;
  word?: string;
  state?: any;
  options?: Record<string, any>;
}

export interface ExecutionTraceApiResponse {
  algorithmId: string;
  totalSteps: number;
  steps: ExecutionStep[];
  summary?: Record<string, any>;
}

/**
 * Check if the Java Spring Boot backend is reachable and healthy.
 */
export async function checkBackendHealth(): Promise<BackendHealthResponse | null> {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/health`, {
      method: 'GET',
      headers: { Accept: 'application/json' },
      signal: AbortSignal.timeout(1500),
    });
    if (res.ok) {
      return await res.json();
    }
  } catch {
    // Backend offline or unreachable
  }
  return null;
}

/**
 * Authoritative execution of algorithms via the Java backend.
 */
export async function runAlgorithmOnJavaBackend(
  payload: AlgorithmRunPayload
): Promise<ExecutionStep[] | null> {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/algorithms/run`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
      signal: AbortSignal.timeout(6000),
    });

    if (res.ok) {
      const data: ExecutionTraceApiResponse = await res.json();
      return data.steps;
    } else {
      const err = await res.json().catch(() => ({}));
      console.warn('[AlgoJ Java Backend Error]', err);
    }
  } catch (error) {
    console.warn('[AlgoJ Java Backend Unreachable]', error);
  }
  return null;
}

/**
 * Authoritative execution of interactive data structure operations via Java backend.
 */
export async function operateStructureOnJavaBackend(
  payload: DataStructureOpPayload
): Promise<ExecutionStep[] | null> {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/structures/operate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
      signal: AbortSignal.timeout(6000),
    });

    if (res.ok) {
      const data: ExecutionTraceApiResponse = await res.json();
      return data.steps;
    } else {
      const err = await res.json().catch(() => ({}));
      console.warn('[AlgoJ Java Structure Error]', err);
    }
  } catch (error) {
    console.warn('[AlgoJ Java Structure Unreachable]', error);
  }
  return null;
}
