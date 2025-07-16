// Sticky Navigation JavaScript
document.addEventListener('DOMContentLoaded', function() {
  const header = document.querySelector('header');
  const nav = document.querySelector('nav');
  const body = document.body;
  let lastScrollTop = 0;
  let isSticky = false;

  // Function to handle scroll events
  function handleScroll() {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    const headerHeight = header.offsetHeight;
    const navHeight = nav.offsetHeight;

    // Check if we've scrolled past the header (which includes the H1)
    if (scrollTop > headerHeight && !isSticky) {
      // Make navigation sticky
      nav.classList.add('sticky');
      body.classList.add('has-sticky-nav');
      isSticky = true;
    } else if (scrollTop <= headerHeight && isSticky) {
      // Remove sticky navigation
      nav.classList.remove('sticky', 'show');
      body.classList.remove('has-sticky-nav');
      isSticky = false;
    }

    // Show/hide sticky navigation based on scroll direction
    if (isSticky) {
      if (scrollTop > lastScrollTop && scrollTop > headerHeight + 50) {
        // Scrolling down - hide navigation
        nav.classList.remove('show');
      } else {
        // Scrolling up - show navigation
        nav.classList.add('show');
      }
    }

    lastScrollTop = scrollTop;
  }

  // Add scroll event listener
  window.addEventListener('scroll', handleScroll, { passive: true });

  // Handle resize events to recalculate heights
  window.addEventListener('resize', function() {
    if (isSticky) {
      body.classList.add('has-sticky-nav');
    }
  }, { passive: true });
});
