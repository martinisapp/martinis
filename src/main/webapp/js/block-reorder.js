/**
 * Block Drag and Drop Reordering
 */
$(document).ready(function() {
    var tableBody = document.getElementById('table-blocks');
    if (tableBody) {
        // Initialize SortableJS on the table body
        var sortable = new Sortable(tableBody.querySelector('tbody'), {
            animation: 150,
            handle: '.drag-handle', // Drag handle
            ghostClass: 'sortable-ghost',
            dragClass: 'sortable-drag',
            onEnd: function(evt) {
                // Get the new order of block IDs
                var blockIds = [];
                var rows = tableBody.querySelectorAll('tbody tr');

                rows.forEach(function(row) {
                    var blockId = row.getAttribute('data-block-id');
                    if (blockId) {
                        blockIds.push(parseInt(blockId));
                    }
                });

                // Send the new order to the server
                $.ajax({
                    url: contextPath + '/block/reorder',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(blockIds),
                    success: function(response) {
                        console.log('Block order updated successfully');
                    },
                    error: function(xhr, status, error) {
                        console.error('Error updating block order:', error);
                        alert('Failed to update block order. Please refresh the page and try again.');
                        // Reload the page to restore original order
                        location.reload();
                    }
                });
            }
        });
    }
});
