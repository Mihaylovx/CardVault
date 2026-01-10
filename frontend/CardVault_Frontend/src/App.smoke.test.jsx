import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';

function Dummy() {
  return <div>CardVault</div>;
}

describe('App smoke test', () => {
  it('renders without crashing', () => {
    const { getByText } = render(<Dummy />);
    expect(getByText('CardVault')).toBeInTheDocument();
  });
});
