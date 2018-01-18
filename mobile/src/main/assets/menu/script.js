var title;

function openMenu() {
    Android.openMenu("");
}
function toggleKeyboard() {
    Android.toggleKeyboard("");
}
function toggleURLKeyboard() {
    Android.toggleURLKeyboard($(".urlInput").val());
}
function submitForm() {
    Android.submitForm("");
}
function goBack() {
    Android.goBack("");
}
function showFavorites() {
    Android.showFavorites("");
}
function addToFavorites() {
    Android.addToFavorites(title, $(".urlInput").val());
}
function switchRenderMode(){
    Android.switchRenderMode("");
}
function setURL(url){
    $(".urlInput").val(url);
}
function setTitle(loadedTitle){
    title = loadedTitle;
}