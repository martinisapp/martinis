/**
 * Block interactions using htmx
 * Handles drag-and-drop reordering only
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('Block htmx JavaScript loaded');

    var blocksList = document.getElementById('blocks-list');
    if (blocksList) {
        // Toggle 'editing' class on block-row when block-edit appears/disappears
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                mutation.addedNodes.forEach(function(node) {
                    if (node.nodeType === 1) { // Element node
                        if (node.classList && node.classList.contains('block-edit')) {
                            var blockRow = node.closest('.block-row');
                            if (blockRow) blockRow.classList.add('editing');
                        }
                        // Also check children
                        var editForms = node.querySelectorAll && node.querySelectorAll('.block-edit');
                        if (editForms) {
                            editForms.forEach(function(form) {
                                var blockRow = form.closest('.block-row');
                                if (blockRow) blockRow.classList.add('editing');
                            });
                        }
                    }
                });
                mutation.removedNodes.forEach(function(node) {
                    if (node.nodeType === 1) { // Element node
                        if (node.classList && node.classList.contains('block-edit')) {
                            var blockRow = node.closest('.block-row');
                            if (blockRow) blockRow.classList.remove('editing');
                        }
                    }
                });
            });
        });

        observer.observe(blocksList, { childList: true, subtree: true });

        // Set initial state for any existing edit forms
        blocksList.querySelectorAll('.block-edit').forEach(function(form) {
            var blockRow = form.closest('.block-row');
            if (blockRow) blockRow.classList.add('editing');
        });
    }

    if (blocksList) {
        // Get CSRF token from meta tags
        var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        // Initialize SortableJS on the blocks list
        var sortable = new Sortable(blocksList, {
            animation: 150,
            handle: '.drag-handle',
            ghostClass: 'sortable-ghost',
            dragClass: 'sortable-drag',
            onEnd: function(evt) {
                // Get the new order of block IDs
                var blockIds = [];
                var rows = blocksList.querySelectorAll('.block-row');

                rows.forEach(function(row) {
                    var blockId = row.getAttribute('data-block-id');
                    if (blockId && !blockId.startsWith('new-')) {
                        blockIds.push(parseInt(blockId));
                    }
                });

                // Send the new order to the server using fetch
                fetch(contextPath + '/block/reorder', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify(blockIds)
                })
                .then(function(response) {
                    if (response.ok) {
                        console.log('Block order updated successfully');
                    } else {
                        throw new Error('Failed to update block order');
                    }
                })
                .catch(function(error) {
                    console.error('Error updating block order:', error);
                    alert('Failed to update block order. Please refresh the page and try again.');
                    location.reload();
                });
            }
        });
    }

    // Handle clicking outside edit forms to cancel editing
    document.addEventListener('click', function(e) {
        // Check if click is inside a block-edit div or block-display div
        var isInsideEdit = e.target.closest('.block-edit');
        var isInsideDisplay = e.target.closest('.block-display');

        // If clicked outside both, close any open edit forms
        if (!isInsideEdit && !isInsideDisplay) {
            var editForms = document.querySelectorAll('.block-edit');
            editForms.forEach(function(form) {
                var formElement = form.querySelector('form');
                if (!formElement) return;

                // Check if this is a new block form (has action ending with createInline or createBelowInline)
                var action = formElement.getAttribute('hx-post') || '';
                if (action.includes('createInline') || action.includes('createBelowInline')) {
                    // For new block forms, do nothing - let auto-save handle it via blur event
                    return;
                } else {
                    // For existing block forms, reload the display view
                    var blockId = form.querySelector('input[name="id"]').value;
                    htmx.ajax('GET', contextPath + '/block/displayView?id=' + blockId, {
                        target: form.closest('.block-column-content'),
                        swap: 'innerHTML'
                    });
                }
            });
        }
    });

    // Handle Escape key to close edit forms
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' || e.keyCode === 27) {
            var editForms = document.querySelectorAll('.block-edit');
            editForms.forEach(function(form) {
                var formElement = form.querySelector('form');
                if (!formElement) return;

                // Check if this is a new block form (has action ending with createInline or createBelowInline)
                var action = formElement.getAttribute('hx-post') || '';
                if (action.includes('createInline') || action.includes('createBelowInline')) {
                    // For new block forms, do nothing - must be saved via auto-save
                    return;
                } else {
                    // For existing block forms, reload the display view
                    var blockId = form.querySelector('input[name="id"]').value;
                    htmx.ajax('GET', contextPath + '/block/displayView?id=' + blockId, {
                        target: form.closest('.block-column-content'),
                        swap: 'innerHTML'
                    });
                }
            });
        }
    });
});

/**
 * Toggle bookmark status for a block
 */
function toggleBookmark(blockId, buttonElement) {
    // Get CSRF token from meta tags
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Send request to toggle bookmark
    fetch(contextPath + '/block/toggleBookmark?id=' + blockId, {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(function(response) {
        if (response.ok) {
            // Toggle the UI
            var icon = buttonElement.querySelector('.bookmark-icon');
            var isBookmarked = buttonElement.classList.contains('bookmarked');

            if (isBookmarked) {
                // Remove bookmark
                buttonElement.classList.remove('bookmarked');
                icon.textContent = '☆';
                buttonElement.setAttribute('title', 'Add bookmark');
            } else {
                // Add bookmark
                buttonElement.classList.add('bookmarked');
                icon.textContent = '★';
                buttonElement.setAttribute('title', 'Remove bookmark');
            }
        } else {
            throw new Error('Failed to toggle bookmark');
        }
    })
    .catch(function(error) {
        console.error('Error toggling bookmark:', error);
        alert('Failed to toggle bookmark. Please try again.');
    });
}
