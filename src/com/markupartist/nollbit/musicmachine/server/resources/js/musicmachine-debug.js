$(document).ready(function() {
    $("#playlist-vote").click(function() {
        musicmachine.vote();
    });

    $("#playlist-add").click(function() {
        musicmachine.add();
    });

    // Set up the drop zone.
    $('#uris')

        // Update the drop zone class on drag enter/leave
        .bind('dragenter', function(ev) {
            $(ev.target).addClass('dragover');
            return false;
        })
        .bind('dragleave', function(ev) {
            $(ev.target).removeClass('dragover');
            return false;
        })

        // Allow drops of any kind into the zone.
        .bind('dragover', function(ev) {
            return false;
        })

        // Handle the final drop...
        .bind('drop', function(ev) {
            var dt = ev.originalEvent.dataTransfer;

            if (dt.types.contains('x-star-trek/tribble')) {
                // Filter out this particular data type.
                $.log('#data_transfer .messages',
                    'This data type is denied for drop.');
                return true;
            }

            var textData = dt.getData('Text');

            //alert(textData);

            ev.stopPropagation();
            return false;
        });


});