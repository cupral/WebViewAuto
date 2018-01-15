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

function getFaviconUrl(url){
    var lastChar = url.slice(-1);
    if(lastChar === "/"){
        return url + "favicon.ico";
    } else {
        return url + "/favicon.ico";
    }
}

function parseFavorites(favoritesJson){
    var favObjects = JSON.parse(favoritesJson);
    $(".favorite-container").empty();
    $.each(favObjects, function(i,e){
        var line1 = "<div class='favorite-item' data-url='"+e.url+"' data-desktop='"+e.desktop+"' onclick='openFavorite(this);'>";
        var line2 = "<div>"+e.title+"</div>";
        var line3 = "<img class='favorite-img' src='"+getFaviconUrl(e.url)+"'/>";
        var line4 = "</div>";
        var html = line1+line2+line3+line4;
        $(".favorite-container").append(html);
    });
    window.scrollTo(0, 0);
}

var settings = {};
settings.disclaimerHidden = 0;

function saveSettings(){
    localStorage.setItem("settings", JSON.stringify(settings));
}

function restoreSettings(){
    var savedSettings = JSON.parse(localStorage.getItem("settings"));
    if(savedSettings === null){
        saveSettings();
    }
    settings = JSON.parse(localStorage.getItem("settings"));
}

function hideDisclaimer(){
    $(".warning-box").hide();
    $(".favorite-footer").show();
    $(".favorite-container").show();
    settings.disclaimerHidden = new Date().getTime();
    saveSettings();
}
function showDisclaimer(){
    if(new Date().getTime() - settings.disclaimerHidden > 10000){
        $(".warning-box").show();
        $(".favorite-footer").hide();
        $(".favorite-container").hide();
    }
}

$(document).ready(function() {
    loadFavorites();
    showDisclaimer();
});
