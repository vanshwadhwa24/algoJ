import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RotateCcw } from 'lucide-react';

interface ErrorBoundaryProps {
  children: ReactNode;
  fallbackTitle?: string;
  onReset?: () => void;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('[AlgoJ ErrorBoundary caught an error]:', error, errorInfo);
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
    if (this.props.onReset) {
      this.props.onReset();
    }
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="bg-white border-2 border-[#2B2620] rounded-[24px] p-6 shadow-[0_12px_28px_-8px_rgba(43,38,32,0.25)] text-center max-w-xl mx-auto my-6 space-y-4">
          <div className="w-12 h-12 rounded-full bg-[#D97B54] border-2 border-[#2B2620] flex items-center justify-center mx-auto shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
            <AlertTriangle className="w-6 h-6 text-[#2B2620]" />
          </div>

          <div className="space-y-1">
            <h3 className="font-black text-lg text-[#2B2620]">
              {this.props.fallbackTitle || 'Rendering Error Encountered'}
            </h3>
            <p className="text-xs font-semibold text-gray-600 max-w-md mx-auto">
              AlgoJ intercepted a rendering fault for this view. You can recover immediately by resetting the visualizer state.
            </p>
          </div>

          {this.state.error && (
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-3 text-left font-mono text-[11px] text-red-700 overflow-x-auto max-h-32">
              {this.state.error.message}
            </div>
          )}

          <button
            onClick={this.handleReset}
            className="px-4 py-2 bg-[#9CCBA2] text-[#2B2620] rounded-xl border-2 border-[#2B2620] font-black text-xs shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:bg-[#7bc69e] transition-all cursor-pointer inline-flex items-center gap-2"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            <span>Reset Visualizer</span>
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
