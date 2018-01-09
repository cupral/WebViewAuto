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
function setURL(url){
    $(".urlInput").val(url);
}