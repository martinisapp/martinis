$(document).ready(function() {
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    // Handle edit button click
    $(document).on('click', '.edit-inline-btn', function(e) {
        e.preventDefault();
        var $row = $(this).closest('tr');
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');

        // Hide display, show edit form
        $display.hide();
        $edit.show();

        // Hide the edit button while editing
        $(this).hide();
    });

    // Handle cancel button click
    $(document).on('click', '.cancel-edit-btn', function(e) {
        e.preventDefault();
        var $row = $(this).closest('tr');
        var $display = $row.find('.block-display');
        var $edit = $row.find('.block-edit');

        // Reset form values to original
        var originalContent = $row.find('.block-content').text().trim();
        var originalPersonId = $row.data('person-id');

        $edit.find('.edit-content-textarea').val(originalContent);
        $edit.find('.edit-person-select').val(originalPersonId || '');

        // Show display, hide edit form
        $edit.hide();
        $display.show();

        // Show the edit button again
        $row.find('.edit-inline-btn').show();
    });

    // Handle save button click
    $(document).on('click', '.save-block-btn', function(e) {
        e.preventDefault();
        var $button = $(this);
        var $row = $button.closest('tr');
        var blockId = $row.data('block-id');
        var sceneId = $row.data('scene-id');
        var content = $row.find('.edit-content-textarea').val();
        var personId = $row.find('.edit-person-select').val();

        // Validate content
        if (!content || content.trim() === '') {
            alert('Content cannot be empty');
            return;
        }

        // Disable button while saving
        $button.prop('disabled', true).text('Saving...');

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

                // Update data attributes
                $row.data('person-id', personId || '');

                // Hide edit form, show display
                $row.find('.block-edit').hide();
                $display.show();

                // Re-enable button and show edit button
                $button.prop('disabled', false).text('Save');
                $row.find('.edit-inline-btn').show();

                console.log('Block updated successfully');
            },
            error: function(xhr, status, error) {
                console.error('Error updating block:', error);
                var errorMessage = xhr.responseText || 'Failed to update block. Please try again.';
                alert(errorMessage);

                // Re-enable button
                $button.prop('disabled', false).text('Save');
            }
        });
    });

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
