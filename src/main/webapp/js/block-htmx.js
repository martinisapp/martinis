/**
 * Block interactions using htmx
 * Handles drag-and-drop reordering only
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('Block htmx JavaScript loaded');

    var tableBody = document.getElementById('table-blocks');
    if (tableBody) {
        // Get CSRF token from meta tags
        var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        // Initialize SortableJS on the table body
        var sortable = new Sortable(tableBody.querySelector('tbody'), {
            animation: 150,
            handle: '.drag-handle',
            ghostClass: 'sortable-ghost',
            dragClass: 'sortable-drag',
            onEnd: function(evt) {
                // Get the new order of block IDs
                var blockIds = [];
                var rows = tableBody.querySelectorAll('tbody tr');

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
                    // For new block forms, remove the entire row
                    form.closest('tr').remove();
                } else {
                    // For existing block forms, reload the display view
                    var blockId = form.querySelector('input[name="id"]').value;
                    htmx.ajax('GET', contextPath + '/block/displayView?id=' + blockId, {
                        target: form.closest('td'),
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
                    // For new block forms, remove the entire row
                    form.closest('tr').remove();
                } else {
                    // For existing block forms, reload the display view
                    var blockId = form.querySelector('input[name="id"]').value;
                    htmx.ajax('GET', contextPath + '/block/displayView?id=' + blockId, {
                        target: form.closest('td'),
                        swap: 'innerHTML'
                    });
                }
            });
        }
    });
});
