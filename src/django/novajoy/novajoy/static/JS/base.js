$(document).ready(function () {
    $(".collection span").click(function (evt) {
        evt.preventDefault();
        $('.listURL span').remove();
        var nameCollection = $(this).text();
        curCol = nameCollection;
        $.post('/selectURL/', {nameCollection: nameCollection},
            function (data) {
                var tmp = $.parseJSON(data);
                $('.listURL').removeData();
                $('.listURL').innerHTML = "";
                for (var i = 0; i < tmp.length; i++) {
                    $(".listURL").prepend("<p><span div='url'>" + tmp[i]['fields']['url'] + " </span></p> ");
                }
            }
        );
    });
    curCol = $('.collection span:first').text()
    $('.collection span:first').click();

    $("#newCollection").click(function (evt) {
        evt.preventDefault();
        var nameOfNewCollection = prompt("Enter name new collection:");
        nameOfNewCollection = nameOfNewCollection.trim()
        if (nameOfNewCollection == "") {
            alert("Empty field");
        } else {

            $('.listURL span').remove();
            $('.listURL').innerHTML = "";
            $.post('/addCollection/', {newCollection: nameOfNewCollection},
                function (data) {
                    var isAdding = data;
                    if (isAdding == "Success") {
                        $('.collection').append("<p><span>" + nameOfNewCollection + "</span></p>");
                    } else {
                        alert("Error");
                    }
                    curCol = nameOfNewCollection;
                    $('.collection span:last').click();
                });

        }
    });
    $("#addURL").click(function (evt) {
        evt.preventDefault();
        var nameOfNewRSS = prompt("Enter name new RSS:");
        nameOfNewRSS = nameOfNewRSS.trim();
        if (nameOfNewRSS == "") {
            alert("Empty Field");
        } else {
            $.post('/addRSS/', {nameOfNewRSS: nameOfNewRSS, nameCollection: curCol},
                function (data) {
                    var isAdding = data;
                    $('.listURL').append('<p><span>' + nameOfNewRSS + '</span></p>');
                }
            );
        }
    });
});