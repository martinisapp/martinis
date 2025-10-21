$(document).ready(function() {
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');
    var autoSaveTimers = {}; // Store timers per block

    // Handle block-display click to enter edit mode
    $(document).on('click', '.block-display', function(e) {
        // Don't trigger edit mode if clicking on a link (character name)
        if ($(e.target).is('a') || $(e.target).closest('a').length > 0) {
            return;
        }

        e.stopPropagation(); // Prevent event from bubbling to document click handler
        var $row = $(this).closest('tr');

        enterEditMode($row);
    });

    // Function to enter edit mode for a block
    function enterEditMode($row) {
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');
        var blockId = $row.data('block-id');

        // Don't enter edit mode if already editing
        if ($row.data('is-editing')) {
            return;
        }

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

        // Add visual indicator for active block
        $row.addClass('editing-active');

        // Initialize save status
        updateSaveStatus($row, 'ready');

        // Focus on the textarea and auto-expand
        var $textarea = $edit.find('.edit-content-textarea');
        $textarea.focus();
        autoExpandTextarea($textarea[0]);
    }

    // Prevent clicks inside edit form from bubbling to document handler
    $(document).on('click', '.block-edit', function(e) {
        e.stopPropagation();
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

        // Remove visual indicator
        $row.removeClass('editing-active');

        // Remove the editing flag
        $row.removeData('is-editing');
    }

    // Function to get next/previous block row
    function getNextBlockRow($currentRow) {
        var $next = $currentRow.next('tr[data-block-id]');
        return $next.length > 0 ? $next : null;
    }

    function getPreviousBlockRow($currentRow) {
        var $prev = $currentRow.prev('tr[data-block-id]');
        return $prev.length > 0 ? $prev : null;
    }

    // Function to navigate to next block
    function navigateToNextBlock($currentRow) {
        var $nextRow = getNextBlockRow($currentRow);
        if ($nextRow) {
            closeEditMode($currentRow);
            enterEditMode($nextRow);
        }
    }

    // Function to navigate to previous block
    function navigateToPreviousBlock($currentRow) {
        var $prevRow = getPreviousBlockRow($currentRow);
        if ($prevRow) {
            closeEditMode($currentRow);
            enterEditMode($prevRow);
        }
    }

    // Auto-expand textarea based on content
    function autoExpandTextarea(textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = (textarea.scrollHeight) + 'px';
    }

    // Handle clicking outside the edit form
    $(document).on('click', function(e) {
        // Find all rows that are currently being edited and close them
        // (clicks inside .block-edit and .block-display are stopped from propagating)
        $('tr[data-block-id]').each(function() {
            var $row = $(this);
            if ($row.data('is-editing')) {
                closeEditMode($row);
            }
        });
    });

    // Global keyboard shortcuts (not in textarea)
    $(document).on('keydown', function(e) {
        var $editingRow = $('tr[data-block-id].editing-active');

        // Handle Escape key to close edit mode
        if (e.key === 'Escape' || e.keyCode === 27) {
            if ($editingRow.length > 0) {
                closeEditMode($editingRow);
                e.preventDefault();
            }
        }

        // Ctrl+N: Create new block below current
        if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
            e.preventDefault();
            if ($editingRow.length > 0) {
                var $createBtn = $editingRow.find('.create-below');
                if ($createBtn.length > 0) {
                    window.location.href = $createBtn.attr('href');
                }
            }
        }

        // Ctrl+D: Delete current block
        if ((e.ctrlKey || e.metaKey) && e.key === 'd') {
            e.preventDefault();
            if ($editingRow.length > 0) {
                if (confirm('Delete this block?')) {
                    var $deleteBtn = $editingRow.find('a[href*="block/delete"]');
                    if ($deleteBtn.length > 0) {
                        window.location.href = $deleteBtn.attr('href');
                    }
                }
            }
        }
    });

    // Keyboard shortcuts within textarea
    $(document).on('keydown', '.edit-content-textarea', function(e) {
        var $textarea = $(this);
        var $row = $textarea.closest('tr[data-block-id]');

        // Ctrl+S: Save immediately
        if ((e.ctrlKey || e.metaKey) && e.key === 's') {
            e.preventDefault();
            var blockId = $row.data('block-id');
            if (autoSaveTimers[blockId]) {
                clearTimeout(autoSaveTimers[blockId]);
                delete autoSaveTimers[blockId];
            }
            autoSaveBlock($row);
        }

        // Ctrl+Enter: Save and create new block below
        if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
            e.preventDefault();
            var blockId = $row.data('block-id');
            if (autoSaveTimers[blockId]) {
                clearTimeout(autoSaveTimers[blockId]);
                delete autoSaveTimers[blockId];
            }
            autoSaveBlock($row);
            // Trigger create new block after a short delay to allow save to complete
            setTimeout(function() {
                var $createBtn = $row.find('.create-below');
                if ($createBtn.length > 0) {
                    window.location.href = $createBtn.attr('href');
                }
            }, 500);
        }

        // Arrow Down: Navigate to next block (when at end of textarea)
        if (e.key === 'ArrowDown' && !e.ctrlKey && !e.shiftKey && !e.altKey) {
            var cursorPos = $textarea[0].selectionStart;
            var textLength = $textarea.val().length;
            var textBeforeCursor = $textarea.val().substring(0, cursorPos);
            var lines = textBeforeCursor.split('\n');
            var currentLine = lines.length - 1;
            var totalLines = $textarea.val().split('\n').length - 1;

            // If we're on the last line, navigate to next block
            if (currentLine === totalLines) {
                e.preventDefault();
                navigateToNextBlock($row);
            }
        }

        // Arrow Up: Navigate to previous block (when at start of textarea)
        if (e.key === 'ArrowUp' && !e.ctrlKey && !e.shiftKey && !e.altKey) {
            var cursorPos = $textarea[0].selectionStart;
            var textBeforeCursor = $textarea.val().substring(0, cursorPos);
            var lines = textBeforeCursor.split('\n');
            var currentLine = lines.length - 1;

            // If we're on the first line, navigate to previous block
            if (currentLine === 0) {
                e.preventDefault();
                navigateToPreviousBlock($row);
            }
        }
    });

    // Auto-save on content change (debounced)
    $(document).on('input', '.edit-content-textarea', function() {
        var $textarea = $(this);
        var $row = $textarea.closest('tr');
        var blockId = $row.data('block-id');

        // Auto-expand textarea
        autoExpandTextarea($textarea[0]);

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
