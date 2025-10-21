$(document).ready(function() {
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');
    var autoSaveTimers = {}; // Store timers per block

    // Handle edit button click
    $(document).on('click', '.edit-inline-btn', function(e) {
        e.preventDefault();
        var $row = $(this).closest('tr');
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');
        var blockId = $row.data('block-id');

        // Store original values for change detection
        var originalContent = $row.find('.block-content').text().trim();
        var originalPersonId = $row.data('person-id');

        $row.data('original-content', originalContent);
        $row.data('original-person-id', originalPersonId);

        // Set editing flag
        $row.data('is-editing', true);

        // Hide display, show edit form
        $display.hide();
        $edit.show();

        // Hide the edit button while editing
        $(this).hide();

        // Initialize save status
        updateSaveStatus($row, 'ready');

        // Focus on the textarea
        $edit.find('.edit-content-textarea').focus();
    });

    // Function to close edit mode
    function closeEditMode($row) {
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');
        var blockId = $row.data('block-id');

        // Clear any pending auto-save timer
        if (autoSaveTimers[blockId]) {
            clearTimeout(autoSaveTimers[blockId]);
            delete autoSaveTimers[blockId];
        }

        // Show display, hide edit form
        $edit.hide();
        $display.show();

        // Show the edit button again
        $row.find('.edit-inline-btn').show();

        // Remove the editing flag
        $row.removeData('is-editing');
    }

    // Handle clicking outside the edit form
    $(document).on('click', function(e) {
        // Find all rows that are currently being edited
        $('tr[data-block-id]').each(function() {
            var $row = $(this);
            if ($row.data('is-editing')) {
                var $edit = $row.find('.block-edit');

                // Check if click is outside the edit form
                if (!$edit.is(e.target) && $edit.has(e.target).length === 0) {
                    // Also check if not clicking the edit button itself
                    if (!$(e.target).hasClass('edit-inline-btn') && !$(e.target).closest('.edit-inline-btn').length) {
                        closeEditMode($row);
                    }
                }
            }
        });
    });

    // Handle Escape key to close edit mode
    $(document).on('keydown', function(e) {
        if (e.key === 'Escape' || e.keyCode === 27) {
            // Find all rows that are currently being edited
            $('tr[data-block-id]').each(function() {
                var $row = $(this);
                if ($row.data('is-editing')) {
                    closeEditMode($row);
                }
            });
        }
    });

    // Auto-save on content change (debounced)
    $(document).on('input', '.edit-content-textarea', function() {
        var $row = $(this).closest('tr');
        var blockId = $row.data('block-id');

        // Clear existing timer
        if (autoSaveTimers[blockId]) {
            clearTimeout(autoSaveTimers[blockId]);
        }

        // Show "typing..." status
        updateSaveStatus($row, 'typing');

        // Set new timer to auto-save after 1.5 seconds of inactivity
        autoSaveTimers[blockId] = setTimeout(function() {
            autoSaveBlock($row);
        }, 1500);
    });

    // Auto-save on character selection change (immediate)
    $(document).on('change', '.edit-person-select', function() {
        var $row = $(this).closest('tr');
        var blockId = $row.data('block-id');

        // Clear any pending timer since we're saving now
        if (autoSaveTimers[blockId]) {
            clearTimeout(autoSaveTimers[blockId]);
            delete autoSaveTimers[blockId];
        }

        autoSaveBlock($row);
    });

    // Function to auto-save a block
    function autoSaveBlock($row) {
        var blockId = $row.data('block-id');
        var sceneId = $row.data('scene-id');
        var content = $row.find('.edit-content-textarea').val();
        var personId = $row.find('.edit-person-select').val();

        // Validate content
        if (!content || content.trim() === '') {
            updateSaveStatus($row, 'error', 'Content cannot be empty');
            return;
        }

        // Check if anything actually changed
        var originalContent = $row.data('original-content');
        var originalPersonId = $row.data('original-person-id') || '';

        if (content === originalContent && (personId || '') == originalPersonId) {
            updateSaveStatus($row, 'ready');
            return; // No changes to save
        }

        // Show saving status
        updateSaveStatus($row, 'saving');

        // Prepare data
        var data = {
            id: blockId,
            sceneId: sceneId,
            content: content,
            personId: personId ? parseInt(personId) : null
        };

        // Send AJAX request
        $.ajax({
            url: contextPath + '/block/updateInline',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(response) {
                // Update display with new values
                var $display = $row.find('.block-display');
                var personName = $row.find('.edit-person-select option:selected').text();

                if (personId && personId !== '') {
                    var personLink = '<p class="mb-0 text-center"><a href="' + contextPath + '/character/show?id=' + personId + '" class="character-name text-uppercase">' + personName + '</a></p>';
                    var contentDiv = '<div class="text-center block-content">' + escapeHtml(content) + '</div>';
                    $display.html(personLink + contentDiv);
                } else {
                    $display.html('<div class="block-content">' + escapeHtml(content) + '</div>');
                }

                // Update data attributes with new values
                $row.data('person-id', personId || '');
                $row.data('original-content', content);
                $row.data('original-person-id', personId || '');

                // Show saved status
                updateSaveStatus($row, 'saved');

                console.log('Block auto-saved successfully');
            },
            error: function(xhr, status, error) {
                console.error('Error auto-saving block:', error);
                var errorMessage = xhr.responseText || 'Failed to save';
                updateSaveStatus($row, 'error', errorMessage);
            }
        });
    }

    // Function to update save status indicator
    function updateSaveStatus($row, status, message) {
        var $statusIndicator = $row.find('.save-status');

        switch(status) {
            case 'ready':
                $statusIndicator.html('<span class="text-muted"><i>Ready to edit</i></span>');
                break;
            case 'typing':
                $statusIndicator.html('<span class="text-muted"><i>Typing...</i></span>');
                break;
            case 'saving':
                $statusIndicator.html('<span class="text-info"><i>Saving...</i></span>');
                break;
            case 'saved':
                $statusIndicator.html('<span class="text-success"><i>Saved</i></span>');
                // Clear "saved" message after 2 seconds
                setTimeout(function() {
                    if ($statusIndicator.find('.text-success').length > 0) {
                        $statusIndicator.html('<span class="text-muted"><i>Ready to edit</i></span>');
                    }
                }, 2000);
                break;
            case 'error':
                $statusIndicator.html('<span class="text-danger"><i>' + (message || 'Error saving') + '</i></span>');
                break;
        }
    }

    // Helper function to escape HTML
    function escapeHtml(text) {
        var map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }
});
