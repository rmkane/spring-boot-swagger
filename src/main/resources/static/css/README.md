# Global CSS Styles

This file contains global styles for the Spring Boot Swagger application.

## Usage

The global CSS is automatically included in all pages via the header fragment. No additional imports needed.

## Available Classes

### Layout

- `.container` - Centered container with max-width and padding

### Buttons

- `.btn` - Base button styles
- `.btn-primary` - Primary blue button
- `.btn-secondary` - Secondary gray button
- `.btn-success` - Success green button
- `.btn-danger` - Danger red button

### Cards

- `.card` - Card container with shadow and rounded corners
- `.card-header` - Card header section
- `.card-title` - Card title styling

### Tables

- `.table` - Modern table with rounded corners and shadows
- Automatically styles `th` and `td` elements

### Forms

- `.form-group` - Form field container
- `.form-label` - Form label styling
- `.form-control` - Form input styling

### Utility Classes

- `.text-center` - Center align text
- `.text-right` - Right align text
- `.mb-0` through `.mb-5` - Margin bottom utilities
- `.mt-0` through `.mt-5` - Margin top utilities
- `.p-0` through `.p-5` - Padding utilities

## Example Usage

```html
<!-- Button -->
<a href="/books" class="btn btn-primary">View Books</a>

<!-- Card -->
<div class="card">
  <div class="card-header">
    <h3 class="card-title">Book Details</h3>
  </div>
  <p>Content here...</p>
</div>

<!-- Table -->
<table class="table">
  <thead>
    <tr><th>Header</th></tr>
  </thead>
  <tbody>
    <tr><td>Data</td></tr>
  </tbody>
</table>

<!-- Form -->
<div class="form-group">
  <label class="form-label">Title</label>
  <input type="text" class="form-control">
</div>

<!-- Container -->
<div class="container">
  <h2 class="text-center mb-4">Page Content</h2>
</div>
```

## Benefits

1. **Consistency** - All pages use the same styling
2. **Maintainability** - Changes in one place affect all pages
3. **Performance** - CSS is cached by the browser
4. **Reusability** - Common components can be reused
5. **Responsive** - Built-in mobile-friendly design
