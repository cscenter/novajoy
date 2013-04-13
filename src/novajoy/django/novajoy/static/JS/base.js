function clickNewCollection() {
    $("#dialog1").dialog({autoOpen: false, width: 400, height: 350, buttons: {
        OK: function () {
            //Data from a form
            nameOfNewCollection = document.forms[0].elements[0].value;
            var updateInterval = document.forms[0].elements[1].value;
            //clean form
            document.getElementById('myform').reset();
            nameOfNewCollection = nameOfNewCollection.trim();
            var isnan = isNaN(parseInt(updateInterval));
            if ((nameOfNewCollection == "")  || (updateInterval.trim()=='')) {
                alert("Empty field");
            } else if (parseInt(updateInterval) <= 0) {
                alert("Negative updateInterval");
            } else if(isnan==true){
                alert("The field a 'Update Interval' has to contain only number");
            }else{
                $('.listURL span').remove();
                $('.listURL').innerHTML = "";
                var isAdding = "No";
                $.post('/addCollection/', {newCollection: nameOfNewCollection, updateInterval: updateInterval},
                    function (data) {
                        var response = data;
                        if (response == "Success") {
                            $('.collection').append("<div><p><span>" + nameOfNewCollection + "</span></p></div>");
                            $('.collection span:last').on("click", function () {
                                clickCollection($(this).text());
                            });
                            curCol = nameOfNewCollection;
                            curObject = $('.collection span:last') ;
                            $('.collection span:last').click();
                        } else {
                            alert(response);
                        }
                    }
                );
            }
            $(this).dialog("close");
            return false;
        },
        Cancel: function () {
            $(this).dialog("close");
            return false;
        }
    }
    });
    $("#dialog1").dialog("open");
}
function clickRemoveCollection(){
    curObject.remove();
    if($('.collection span').length>0){
        curObject= $('.collection span:last');
        curObject.click();
    }else{
        curCol = "You have no collections";
        $('.listURL').empty();
        $(".listURL").prepend('<h2>' + curCol + '</h2>');
    }

}
function clickCollection(text) {
    $('.listURL span').remove();
    var nameCollection = text;
    curCol = nameCollection;
    $.post('/selectURL/', {nameCollection: nameCollection},
        function (data) {
            var tmp = $.parseJSON(data);

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
                if (response == "Success") {
                    $('.listURL').append('<p><span>' + nameOfNewRSS + '</span></p>');
                } else {
                    alert(response);
                }
            }
        );
    }
}

$(document).ready(function () {
    $(".collection span").bind('click',function(evt){
        evt.preventDefault();
        curObject =$(this);
        clickCollection($(this).text());
    });

    if ($('.collection span').length == 0) {
        curCol = "You have no collections";
        $(".listURL").prepend('<h2>' + curCol + '</h2>');
    } else {
        curCol = $('.collection span:first').text()
        $('.collection span:first').click();
    }
});
