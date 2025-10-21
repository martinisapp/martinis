$(document).ready(function() {
    console.log('Block inline edit JavaScript loaded');
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

        // Initialize save status
        updateSaveStatus($row, 'ready');

        // Focus on the textarea
        $edit.find('.edit-content-textarea').focus();
    });

    // Prevent clicks inside edit form from bubbling to document handler
    $(document).on('click', '.block-edit', function(e) {
        e.stopPropagation();
    });

    // Function to close edit mode
    function closeEditMode($row, skipSave) {
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');
        var blockId = $row.data('block-id');

        // Clear any pending auto-save timer
        if (autoSaveTimers[blockId]) {
            clearTimeout(autoSaveTimers[blockId]);
            delete autoSaveTimers[blockId];
        }

        // Check if we should auto-save before closing (unless it's a new block)
        if (!skipSave && !$row.data('is-new')) {
            var content = $row.find('.edit-content-textarea').val();
            var personId = $row.find('.edit-person-select').val();
            var originalContent = $row.data('original-content');
            var originalPersonId = $row.data('original-person-id') || '';

            // If there are unsaved changes, save them before closing
            if (content && content !== originalContent || (personId || '') != originalPersonId) {
                // Trigger auto-save and close after it completes
                autoSaveBlock($row, function() {
                    // Close after save completes
                    actuallyCloseEditMode($row);
                });
                return; // Don't close yet, wait for save to complete
            }
        }

        // No unsaved changes or skip requested, close immediately
        actuallyCloseEditMode($row);
    }

    // Helper function that actually closes the edit mode
    function actuallyCloseEditMode($row) {
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');

        // Show display, hide edit form
        $edit.hide();
        $display.show();

        // Remove the editing flag
        $row.removeData('is-editing');
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

    // Return/Enter: Save and create new block below (without modifier keys)
    $(document).on('keydown', '.edit-content-textarea', function(e) {
        if (e.key === 'Enter' && !e.ctrlKey && !e.metaKey && !e.shiftKey && !e.altKey) {
            e.preventDefault();
            var $row = $(this).closest('tr[data-block-id]');
            var blockId = $row.data('block-id');

            // Clear any pending auto-save timer
            if (autoSaveTimers[blockId]) {
                clearTimeout(autoSaveTimers[blockId]);
                delete autoSaveTimers[blockId];
            }

            // Only save and create new block if this is NOT a new block being created
            if (!$row.data('is-new')) {
                autoSaveBlock($row, function() {
                    // After save completes, close and create new block
                    closeEditMode($row, true); // Skip save since we just saved
                    createNewBlockRow(blockId);
                });
            }
        }
    });

    // Auto-save on content change (debounced)
    $(document).on('input', '.edit-content-textarea', function() {
        var $row = $(this).closest('tr');
        var blockId = $row.data('block-id');

        // Don't auto-save for new blocks being created
        if ($row.data('is-new')) {
            return;
        }

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

        // Don't auto-save for new blocks being created
        if ($row.data('is-new')) {
            return;
        }

        // Clear any pending timer since we're saving now
        if (autoSaveTimers[blockId]) {
            clearTimeout(autoSaveTimers[blockId]);
            delete autoSaveTimers[blockId];
        }

        autoSaveBlock($row);
    });

    // Function to auto-save a block
    function autoSaveBlock($row, callback) {
        var blockId = $row.data('block-id');
        var sceneId = $row.data('scene-id');
        var content = $row.find('.edit-content-textarea').val();
        var personId = $row.find('.edit-person-select').val();

        // Validate content
        if (!content || content.trim() === '') {
            updateSaveStatus($row, 'error', 'Content cannot be empty');
            if (callback) callback();
            return;
        }

        // Check if anything actually changed
        var originalContent = $row.data('original-content');
        var originalPersonId = $row.data('original-person-id') || '';

        if (content === originalContent && (personId || '') == originalPersonId) {
            updateSaveStatus($row, 'ready');
            if (callback) callback();
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

                // Call callback if provided
                if (callback) callback();
            },
            error: function(xhr, status, error) {
                console.error('Error auto-saving block:', error);
                var errorMessage = xhr.responseText || 'Failed to save';
                updateSaveStatus($row, 'error', errorMessage);

                // Call callback even on error
                if (callback) callback();
            }
        });
    }

    // Function to update save status indicator
    function updateSaveStatus($row, status, message) {
        var $statusIndicator = $row.find('.save-status');

        switch(status) {
            case 'ready':
                $statusIndicator.html('');
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
                        $statusIndicator.html('');
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

    // Function to create person select options HTML
    function createPersonSelectOptions(selectedPersonId) {
        var options = '<option value="">-- No Character --</option>';
        if (typeof scenePersons !== 'undefined') {
            scenePersons.forEach(function(person) {
                var selected = person.id == selectedPersonId ? 'selected' : '';
                options += '<option value="' + person.id + '" ' + selected + '>' + escapeHtml(person.name) + '</option>';
            });
        }
        return options;
    }

    // Function to create a new block row in edit mode
    function createNewBlockRow(insertAfterBlockId) {
        var tempId = 'new-' + Date.now();
        var $newRow = $('<tr data-block-id="' + tempId + '" data-person-id="" data-scene-id="' + sceneId + '" data-is-new="true">');

        // Store whether this is create-below or create-at-end
        if (insertAfterBlockId) {
            $newRow.data('insert-after-block-id', insertAfterBlockId);
        }

        // Drag handle cell
        $newRow.append('<td><span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span></td>');

        // Content cell with edit form (no display div needed for new blocks)
        var contentCell = '<td>' +
            '<div class="block-display" style="display: none;"></div>' +
            '<div class="block-edit" style="display: block;">' +
                '<div class="form-group">' +
                    '<label>Character:</label>' +
                    '<select class="form-control edit-person-select">' + createPersonSelectOptions('') + '</select>' +
                '</div>' +
                '<div class="form-group">' +
                    '<label>Content:</label>' +
                    '<textarea class="form-control edit-content-textarea" rows="8"></textarea>' +
                '</div>' +
                '<div class="form-group">' +
                    '<div class="save-status"></div>' +
                    '<button class="btn btn-primary btn-sm save-new-block-btn">Save</button>' +
                    '<button class="btn btn-default btn-sm cancel-new-block-btn">Cancel</button>' +
                '</div>' +
            '</div>' +
            '</td>';
        $newRow.append(contentCell);

        // Actions cell (empty for new blocks)
        $newRow.append('<td></td>');

        // Insert the row
        if (insertAfterBlockId) {
            var $afterRow = $('tr[data-block-id="' + insertAfterBlockId + '"]');
            $afterRow.after($newRow);
        } else {
            $('#table-blocks tbody').append($newRow);
        }

        // Set editing flag
        $newRow.data('is-editing', true);

        // Focus on textarea
        $newRow.find('.edit-content-textarea').focus();

        // Scroll to the new block
        $('html, body').animate({
            scrollTop: $newRow.offset().top - 100
        }, 300);
    }

    // Handle "Create New Block" button click
    $(document).on('click', '#create-new-block-btn', function(e) {
        e.preventDefault();
        createNewBlockRow(null);
    });

    // Handle "+ block" (create below) button click
    // DISABLED - now handled by HTMX in block-row.jsp
    // $(document).on('click', '.create-below', function(e) {
    //     e.preventDefault();
    //     console.log('Create below button clicked - creating inline');
    //     var $tr = $(this).closest('tr');
    //     var blockId = $tr.data('block-id');
    //     console.log('Block ID:', blockId);
    //     createNewBlockRow(blockId);
    //     return false; // Extra prevention of default behavior
    // });

    // Handle save new block button
    // DISABLED - now handled by HTMX form submission in block-new-form.jsp
    // $(document).on('click', '.save-new-block-btn', function(e) {
    //     e.preventDefault();
    //     var $row = $(this).closest('tr');
    //     var content = $row.find('.edit-content-textarea').val();
    //     var personId = $row.find('.edit-person-select').val();
    //
    //     console.log('Saving new block - content:', content, 'personId:', personId);
    //
    //     // Validate content
    //     if (!content || content.trim() === '') {
    //         updateSaveStatus($row, 'error', 'Content cannot be empty');
    //         return;
    //     }
    //
    //     // Show saving status
    //     updateSaveStatus($row, 'saving');
    //
    //     // Determine if this is create or createBelow based on stored data
    //     var insertAfterBlockId = $row.data('insert-after-block-id');
    //
    //     console.log('insertAfterBlockId:', insertAfterBlockId);
    //
    //     // Prepare data
    //     var data = {
    //         content: content,
    //         personId: personId ? parseInt(personId) : null
    //     };
    //
    //     // Choose endpoint based on whether we're inserting after a specific block
    //     var endpoint;
    //     if (insertAfterBlockId) {
    //         endpoint = '/block/createBelowInline';
    //         data.id = insertAfterBlockId;
    //         console.log('Using createBelowInline with id:', insertAfterBlockId);
    //     } else {
    //         endpoint = '/block/createInline';
    //         data.sceneId = sceneId;
    //         console.log('Using createInline with sceneId:', sceneId);
    //     }
    //
    //     console.log('Sending AJAX request to', endpoint, 'with data:', data);
    //
    //     // Send AJAX request
    //     $.ajax({
    //         url: contextPath + endpoint,
    //         method: 'POST',
    //         contentType: 'application/json',
    //         data: JSON.stringify(data),
    //         beforeSend: function(xhr) {
    //             xhr.setRequestHeader(csrfHeader, csrfToken);
    //         },
    //         success: function(response) {
    //             console.log('Block created successfully');
    //             // Reload the page to show the new block in the correct position
    //             window.location.reload();
    //         },
    //         error: function(xhr, status, error) {
    //             console.error('Error creating block:', error);
    //             console.error('Response status:', xhr.status);
    //             console.error('Response text:', xhr.responseText);
    //             var errorMessage = xhr.responseText || 'Failed to create block';
    //             updateSaveStatus($row, 'error', errorMessage);
    //         }
    //     });
    // });

    // Handle cancel new block button
    // DISABLED - now handled by HTMX in block-new-form.jsp
    // $(document).on('click', '.cancel-new-block-btn', function(e) {
    //     e.preventDefault();
    //     var $row = $(this).closest('tr');
    //     $row.remove();
    // });

    // Log that event handlers are registered
    console.log('All block inline edit event handlers registered');
    console.log('+ block buttons found:', $('.create-below').length);
});
