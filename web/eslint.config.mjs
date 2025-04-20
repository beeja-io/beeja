import eslintRecommended from '@eslint/js';
import tseslint from 'typescript-eslint';
import pluginReactHooks from 'eslint-plugin-react-hooks';
import pluginReactRefresh from 'eslint-plugin-react-refresh';

const tsconfigPath = './tsconfig.json';

export default [
  {
    ignores: ['dist', '.eslintrc.cjs'],
  },
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        project: tsconfigPath,
        ecmaVersion: 2020,
        sourceType: 'module',
        ecmaFeatures: {
          jsx: true,
        },
      },
      globals: {
        window: 'readonly',
        document: 'readonly',
        console: 'readonly',
      },
    },
    plugins: {
      '@typescript-eslint': tseslint.plugin,
      'react-hooks': pluginReactHooks,
      'react-refresh': pluginReactRefresh,
    },
    rules: {
      ...eslintRecommended.configs.recommended.rules,
      ...tseslint.configs.recommended.rules,
      ...pluginReactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
      'no-multiple-empty-lines': ['error', { max: 1 }],
      quotes: ['error', 'single', { avoidEscape: true }],
      'no-unused-vars': [
        'error',
        { vars: 'all', args: 'none', ignoreRestSiblings: false },
      ],
      'no-console': 'error',
    },
  },
  {
    files: ['**/*.{ts,tsx}'],
    rules: {
      'no-undef': 'off',
    },
  },
];
