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

