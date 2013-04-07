function clickNewCollection() {
    var nameOfNewCollection = prompt("Enter name new collection:");
    nameOfNewCollection = nameOfNewCollection.trim()
    if (nameOfNewCollection == "") {
        alert("Empty field");
    } else {
        $('.listURL span').remove();
        $('.listURL').innerHTML = "";
        var isAdding = "No";
        $.post('/addCollection/', {newCollection: nameOfNewCollection},
            function (data) {
                var response = data;
                if (response == "Success") {
                    $('.collection').append("<div><p><span>" + nameOfNewCollection + "</span></p></div>");
                    $('.collection span:last').on("click",function(){
                       clickCollection($(this).text());
                    });
                    curCol = nameOfNewCollection;
                    $('.collection span:last').click();
                } else {
                    alert(response);
                }
            }
        );
    }
}

function clickCollection(text) {
    $('.listURL span').remove();
    var nameCollection = text;
    curCol = nameCollection;
    $.post('/selectURL/', {nameCollection: nameCollection},
        function (data) {
            var tmp = $.parseJSON(data);
            $('.listURL').empty();
            $(".listURL").prepend('<h2>' + curCol + '</h2>');
            for (var i = 0; i < tmp.length; i++) {
                $(".listURL").append("<p><span>" + tmp[i]['fields']['url'] + " </span></p> ");
            }
        }
    );
}

function addRSS() {
    var nameOfNewRSS = prompt("Enter name new RSS:");
    nameOfNewRSS = nameOfNewRSS.trim();
    if (nameOfNewRSS == "") {
        alert("Empty Field");
    } else {
        $.post('/addRSS/', {nameOfNewRSS: nameOfNewRSS, nameCollection: curCol},
            function (data) {
                var response = data;
                if(response=="Success"){
                    $('.listURL').append('<p><span>' + nameOfNewRSS + '</span></p>');
                }else{
                    alert(response);
                }
            }
        );
    }
}

$(document).ready(function () {
    $(".collection span").click(function (evt) {
        evt.preventDefault();
        clickCollection($(this).text());

    });
//test 
    if ($('.collection span').length == 0) {
        curCol = "You have no collections";
        $(".listURL").prepend('<h2>' + curCol + '</h2>');
    } else {
        curCol = $('.collection span:first').text()
        $('.collection span:first').click();
    }
});
