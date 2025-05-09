import { pathsToModuleNameMapper } from 'ts-jest';
import tsconfig from './tsconfig.json' assert { type: 'json' };

export default {
  preset: 'ts-jest/presets/default-esm',
  setupFiles: ['<rootDir>/jest.env.ts'],
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'],
  testEnvironment: 'jsdom',
  extensionsToTreatAsEsm: ['.ts', '.tsx'],
  transform: {
    '^.+\\.(ts|tsx)$': ['ts-jest', { useESM: true }],
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    ...pathsToModuleNameMapper(tsconfig.compilerOptions.paths || {}, {
      prefix: '<rootDir>/',
    }),
  },
  collectCoverage: true,
  collectCoverageFrom: [
    'src/**/*.{ts,tsx}',
    '!src/**/*.d.ts',
    '!src/**/index.ts',
    '!src/**/types.ts',
  ],
  coverageReporters: ['lcov', 'text'],
  coverageDirectory: 'coverage',
};
