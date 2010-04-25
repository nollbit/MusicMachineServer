musicmachine = function() {

    var currentSongPlaytime = 0;
    return {

        loadPlaylist : function() {
            jQuery.getJSON('/playlist', function(data, textStatus) {
                if (data[0] != undefined) {
                    $("#title").text(data[0].title);
                    $("#artist").text(data[0].artist);
                    $("#length").text(musicmachine.textifyLength(data[0].length));
                    musicmachine.currentSongPlaytime = data[0].length;

                    $("#playlist").empty();
                    jQuery.each(data, function(i, item) {
                        $("#playlist").append($("<li>").text(item.artist + " - " + item.title + " (" + musicmachine.textifyLength(item.length)+ ")"));

                    });
                }
            });
        },
        loadStatus : function() {
            jQuery.getJSON('/status', function(data, textStatus) {
                $("#played").text(musicmachine.textifyLength(data.playtime));
                $("#votecount").text(data.numVotes);

                if (musicmachine.currentSongPlaytime > 0) {
                    var progress = (data.playtime / musicmachine.currentSongPlaytime) * 100;
                    $("#progressbar").progressbar({
                        value: progress 
	    			});
                }
            });
        },
        textifyLength : function(length) {
            minutes = Math.floor(length/60/1000);
            seconds = Math.floor((length / 1000) % 60);

            if (seconds < 10) {
                seconds = "0" + seconds;
            }

            return minutes + ":" + seconds;
        },
        update : function() {
            musicmachine.loadPlaylist();
            musicmachine.loadStatus();
        },
        geturis : function() {
            var uris = $("#uris").val();
            var result = "";

            while(uris.indexOf("http://open.spotify.com/track/") > -1) {
                uris = uris.replace("http://open.spotify.com/track/", "spotify:track:");
            }
            return uris.split("\n");
        },
        randomstring : function(size) {
            var text = "";
            var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            for( var i=0; i < size; i++ )
                text += possible.charAt(Math.floor(Math.random() * possible.length));

            return text;
        },
        vote : function() {
            var uris = musicmachine.geturis();
            jQuery.each(uris, function(i, item) {
                var userId = musicmachine.randomstring(10);
                var trackUri = item;

                jQuery.post("/vote", '{"track": "'+trackUri+'", "user": "'+userId+'"}', function(data, resultString) {
                    //alert("Voted: " + data);
                });

            });
        },
        add : function() {
            var uris = musicmachine.geturis();
            jQuery.each(uris, function(i, item) {
                var trackUri = item;

                jQuery.post("/playlist", '{"track": "'+trackUri+'"}', function(data, resultString) {
                    //alert("Added: " + data);
                });

            });
        },
    }
}();

$(document).ready(function() {
    $(document).everyTime(1000, function(i) {
        musicmachine.update();
    });

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