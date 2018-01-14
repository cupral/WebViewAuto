function openFavorite(e){
    var url = $(e).data("url");
    var desktop = $(e).data("desktop");
    Android.openFavorite(url, desktop);
}
function loadFavorites(){
    Android.loadFavorites("");
}
function resetFavorites(){
    Android.resetFavorites("");
}

function parseFavorites(favoritesJson){
    var favObjects = JSON.parse(favoritesJson);
    $(".favorite-container").empty();
    $.each(favObjects, function(i,e){
        var line1 = "<div class='favorite-item' data-url='"+e.url+"' data-desktop='false' onclick='openFavorite(this);'>";
        var line2 = "<div>"+e.title+"</div>";
        var line3 = "<img class='favorite-img' src='"+e.url+"/favicon.ico'/>";
        var line4 = "</div>";
        var html = line1+line2+line3+line4;
        $(".favorite-container").append(html);
    });
    window.scrollTo(0, 0);
}

$(document).ready(function() {
    loadFavorites();
});
