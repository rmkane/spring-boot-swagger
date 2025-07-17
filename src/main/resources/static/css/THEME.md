<!-- omit in toc -->
# Theme System Documentation

This document describes the consistent theme system used throughout the Spring Boot Swagger application.

<!-- omit in toc -->
## Table of contents

- [Overview](#overview)
- [Color Palette](#color-palette)
  - [Primary Colors](#primary-colors)
  - [Secondary Colors](#secondary-colors)
  - [Semantic Colors](#semantic-colors)
  - [Neutral Colors](#neutral-colors)
- [Typography](#typography)
  - [Font Families](#font-families)
  - [Font Sizes](#font-sizes)
  - [Font Weights](#font-weights)
- [Spacing](#spacing)
  - [Space Scale](#space-scale)
- [Border Radius](#border-radius)
- [Shadows](#shadows)
- [Transitions](#transitions)
- [Usage Examples](#usage-examples)
  - [Color Examples](#color-examples)
  - [Typography Examples](#typography-examples)
  - [Spacing Examples](#spacing-examples)
  - [Border Radius Examples](#border-radius-examples)
  - [Shadow Examples](#shadow-examples)
  - [Transition Examples](#transition-examples)
- [Accessibility Features](#accessibility-features)
  - [Dark Mode Support](#dark-mode-support)
  - [High Contrast Mode](#high-contrast-mode)
  - [Reduced Motion](#reduced-motion)
- [Benefits](#benefits)
- [Best Practices](#best-practices)

## Overview

The theme system uses CSS Custom Properties (CSS Variables) to maintain consistency across all components. This allows for easy theming, dark mode support, and accessibility features.

## Color Palette

### Primary Colors

- `--primary-color: #2563eb` - Main brand color
- `--primary-dark: #1d4ed8` - Darker variant for hover states
- `--primary-light: #3b82f6` - Lighter variant
- `--primary-lighter: #dbeafe` - Very light variant for backgrounds

### Secondary Colors

- `--secondary-color: #64748b` - Secondary brand color
- `--secondary-dark: #475569` - Darker variant
- `--secondary-light: #94a3b8` - Lighter variant
- `--secondary-lighter: #f1f5f9` - Very light variant

### Semantic Colors

- **Success**: `--success-color: #059669` (green)
- **Warning**: `--warning-color: #d97706` (orange)
- **Danger**: `--danger-color: #dc2626` (red)
- **Info**: `--info-color: #0891b2` (cyan)

### Neutral Colors

- `--gray-50` through `--gray-900` - Gray scale
- `--white` and `--black` - Pure colors

## Typography

### Font Families

- `--font-family-sans` - System font stack
- `--font-family-mono` - Monospace font stack

### Font Sizes

- `--font-size-xs: 0.75rem` (12px)
- `--font-size-sm: 0.875rem` (14px)
- `--font-size-base: 1rem` (16px)
- `--font-size-lg: 1.125rem` (18px)
- `--font-size-xl: 1.25rem` (20px)
- `--font-size-2xl: 1.5rem` (24px)
- `--font-size-3xl: 1.875rem` (30px)
- `--font-size-4xl: 2.25rem` (36px)
- `--font-size-5xl: 3rem` (48px)

### Font Weights

- `--font-weight-light: 300`
- `--font-weight-normal: 400`
- `--font-weight-medium: 500`
- `--font-weight-semibold: 600`
- `--font-weight-bold: 700`

## Spacing

### Space Scale

- `--space-0: 0`
- `--space-1: 0.25rem` (4px)
- `--space-2: 0.5rem` (8px)
- `--space-3: 0.75rem` (12px)
- `--space-4: 1rem` (16px)
- `--space-5: 1.25rem` (20px)
- `--space-6: 1.5rem` (24px)
- `--space-8: 2rem` (32px)
- `--space-10: 2.5rem` (40px)
- `--space-12: 3rem` (48px)
- `--space-16: 4rem` (64px)
- `--space-20: 5rem` (80px)
- `--space-24: 6rem` (96px)

## Border Radius

- `--radius-sm: 0.25rem` (4px)
- `--radius-md: 0.375rem` (6px)
- `--radius-lg: 0.5rem` (8px)
- `--radius-xl: 0.75rem` (12px)
- `--radius-2xl: 1rem` (16px)
- `--radius-full: 9999px`

## Shadows

- `--shadow-sm` - Small shadow
- `--shadow-md` - Medium shadow
- `--shadow-lg` - Large shadow
- `--shadow-xl` - Extra large shadow

## Transitions

- `--transition-fast: 150ms ease-in-out`
- `--transition-normal: 250ms ease-in-out`
- `--transition-slow: 350ms ease-in-out`

## Usage Examples

### Color Examples

```css
.my-element {
  color: var(--text-primary);
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
}
```

### Typography Examples

```css
.my-heading {
  font-family: var(--font-family-sans);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  line-height: var(--line-height-tight);
}
```

### Spacing Examples

```css
.my-component {
  padding: var(--space-4);
  margin-bottom: var(--space-6);
  gap: var(--space-2);
}
```

### Border Radius Examples

```css
.my-button {
  border-radius: var(--radius-lg);
}
```

### Shadow Examples

```css
.my-card {
  box-shadow: var(--shadow-md);
}
```

### Transition Examples

```css
.my-interactive-element {
  transition: var(--transition-normal);
}
```

## Accessibility Features

### Dark Mode Support

The theme automatically adapts to user's dark mode preference:

```css
@media (prefers-color-scheme: dark) {
  :root {
    --bg-primary: var(--gray-900);
    --text-primary: var(--gray-100);
    /* ... other dark mode overrides */
  }
}
```

### High Contrast Mode

Enhanced colors for high contrast mode:
```css
@media (prefers-contrast: high) {
  :root {
    --primary-color: #0000ff;
    --danger-color: #ff0000;
    /* ... other high contrast colors */
  }
}
```

### Reduced Motion

Respects user's motion preferences:

```css
@media (prefers-reduced-motion: reduce) {
  :root {
    --transition-fast: 0ms;
    --transition-normal: 0ms;
    --transition-slow: 0ms;
  }
}
```

## Benefits

1. **Consistency** - All components use the same design tokens
2. **Maintainability** - Change colors/spacing in one place
3. **Accessibility** - Built-in support for user preferences
4. **Flexibility** - Easy to create new themes or variants
5. **Performance** - CSS variables are efficient and well-supported

## Best Practices

1. **Always use theme variables** instead of hardcoded values
2. **Use semantic color names** (e.g., `--text-primary` not `--gray-900`)
3. **Respect user preferences** for dark mode and reduced motion
4. **Test with high contrast mode** for accessibility
5. **Keep the scale consistent** - use the defined spacing and typography scales
