import { formatToINR } from './currencyFormattors';

describe('formatToINR', () => {
  it('formats integer numbers correctly', () => {
    expect(formatToINR(1234567)).toBe('12,34,567.00');
  });

  it('formats decimal numbers correctly', () => {
    expect(formatToINR(1234.5)).toBe('1,234.50');
  });

  it('formats small numbers correctly', () => {
    expect(formatToINR(12)).toBe('12.00');
  });

  it('formats zero correctly', () => {
    expect(formatToINR(0)).toBe('0.00');
  });

  it('formats negative numbers correctly', () => {
    expect(formatToINR(-1234.56)).toBe('-1,234.56');
  });
});
