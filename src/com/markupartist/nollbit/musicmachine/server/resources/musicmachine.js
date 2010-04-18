musicmachine = function() {
    return {

        loadPlaylist : function() {
            jQuery.getJSON('/playlist', function(data, textStatus) {
                if (data.length > 0) {
                    $("#title").text(data[0].title);
                    $("#artist").text(data[0].artist);
                    $("#length").text(musicmachine.textifyLength(data[0].length));

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
    }
}();

$(document).ready(function() {
    $(document).everyTime(1000, function(i) {
        musicmachine.update();
    });
});