var capsEnabled = false;
var shiftEnabled = false;
var currentLayout = "de";

$(".input-row input").focus();

function switchLayout(iso) {
    currentLayout = iso;
    $(".layout").hide();
    $(".layout.layout-" + iso).show();

    $(".layout").remove();
    $.get('iso_' + currentLayout + '.html', function(data) {
        $(data).insertAfter($(".input-row"));
    }, "text");

    setTimeout(function() {
        createClickHandlers();
    }, 50)

}

switchLayout("de");

function setInput(str){
    $(".input-elm").val(str);
}

function swapKeyCols() {
    $(".key-1st").toggleClass("key-1st-active");
    $(".key-2nd").toggleClass("key-2nd-inactive");
}

function swapLetterCase() {
    $(".letter").each(function(i, e) {
        if (capsEnabled || shiftEnabled) {
            $(e).text($(e).text().toUpperCase());
        } else {
            $(e).text($(e).text().toLowerCase());
        }
    });
}

function submitInput(str){
    Android.submitInput(str);
}

function createClickHandlers() {

    $(".key").each(function(i, e) {
        $(e).click(function(evt) {

            var children = $(evt.currentTarget).children().length;
            var text = $(evt.currentTarget).text();
            var oldVal = $(".input-row input").val();


            if (children == 0) {
                if (text.indexOf("[") != -1 && text.indexOf("]") != -1) {
                    switchLayout($(evt.currentTarget).data("nextiso"));
                    return;
                }
                if (text === 'Esc') {
                    console.log("Close Keyboard");
                    return;
                }
                if (text === 'Del') {
                    $(".input-row input").val("");
                    return;
                }
                if (text === 'Enter') {
                    console.log("Submit: " + $(".input-row input").val());
                    submitInput($(".input-row input").val());
                    return;
                }
                if (text === 'Caps') {
                    capsEnabled = !capsEnabled;
                    $(".caps").toggleClass("key-pressed");
                    swapKeyCols();
                    swapLetterCase();
                    return;
                }
                if (text === 'Shift') {
                    shiftEnabled = !shiftEnabled;
                    $(".shift").toggleClass("key-pressed");
                    swapKeyCols();
                    swapLetterCase();
                    return;
                }
                if (text === '⌫') {
                    $(".input-row input").val(oldVal.slice(0, -1));
                    return;
                }
                //single letter key
                $(".input-row input").val(oldVal + text);
                if (shiftEnabled) {
                    shiftEnabled = !shiftEnabled;
                    $(".shift").toggleClass("key-pressed");
                    swapKeyCols();
                    swapLetterCase();
                }

            } else {
                var first = $(evt.currentTarget).children().get(0)
                var second = $(evt.currentTarget).children().get(1)
                if (capsEnabled || shiftEnabled) {
                    if (shiftEnabled) {
                        shiftEnabled = !shiftEnabled;
                        $(".shift").toggleClass("key-pressed");
                        swapKeyCols();
                        swapLetterCase();
                    }
                    $(".input-row input").val(oldVal + $(first).text());
                } else {
                    $(".input-row input").val(oldVal + $(second).text());
                }

            }
        });
    });

}