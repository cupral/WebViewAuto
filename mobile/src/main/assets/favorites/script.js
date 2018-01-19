function openFavorite(e){
    var url = $(e).data("url");
    var desktop = $(e).data("desktop");
    Android.openFavorite(url, desktop);
}
function loadFavorites(){
    Android.loadFavorites("");
}
function loadVersion(){
    Android.loadVersion("");
}
function resetFavorites(){
    Android.resetFavorites("");
}
function removeFavorite(fav){
    Android.removeFavorite(fav);
}
function hideMenu(){
    Android.hideMenu("");
}
function showMenu(){
    Android.showMenu("");
}
function clearLocalStorage(){
    Android.clearLocalStorage("");
}
function clearCookies(){
    Android.clearCookies("");
}

function parseFavorites(favoritesJson){
    var favObjects = JSON.parse(favoritesJson);
    $(".favorite-container").empty();
    $.each(favObjects, function(i,e){
        var line1 = "<div class='favorite-item'>";
        var line2 = "<div style='text-align: left;'><span>"+truncate(e.title,10)+"</span><span onclick=\"removeFavorite('"+e.id+"');\" class='favorite-remove-icon'></span></div>";
        var line3 = "<img class='favorite-img' data-id='"+e.id+"' data-url='"+e.url+"' data-desktop='"+e.desktop+"' onclick='openFavorite(this);' src='"+e.favicon+"'/>";
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

function setVersion(version){
    $(".footer-item-version").text(version);
}

function restoreSettings(){
    var savedSettings = JSON.parse(localStorage.getItem("settings"));
    if(savedSettings === null){
        saveSettings();
    }
    settings = JSON.parse(localStorage.getItem("settings"));
}

function hideDisclaimer(){
    showMenu();
    $(".warning-box").hide();
    $(".footer").show();
    $(".favorite-container").show();
    settings.disclaimerHidden = new Date().getTime();
    saveSettings();
}
function showDisclaimer(){
    var lastAcceptTime = new Date().getTime() - settings.disclaimerHidden;
    if(lastAcceptTime > 300000){
        hideMenu();
        $(".warning-box").show();
        $(".footer").hide();
        $(".favorite-container").hide();
    }
}

function truncate(str, len){
    if(str.length <= len){
        return str;
    } else {
        var trimmedString = str.substring(0, len);
        return trimmedString + "…";
    }
}

$(document).ready(function() {
    restoreSettings();
    showDisclaimer();
    loadFavorites();
    loadVersion();
});
