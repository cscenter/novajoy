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

function deleteCollection(nameCollection){
    //alert("delete "+nameCollection);
    var responce="";
    $.post('/deleteCollection/', {nameCollection: curCol},
        function (data) {
            response = data;
            if (response == "Success") {
                curObject.remove();
                if($('.collection span').length>0){
                    curObject= $('.collection span:last');
                    curCol = $('.collection span:last').text();
                    curObject.click();
                }else{
                    curCol = "You have no collections";
                    $('.listURL').empty();
                    $(".listURL").prepend('<h2>' + curCol + '</h2>');
                }
                return true;
            } else {
                alert(response);
                return false;
            }
        }
    );
    //alert("response="+responce);


}

function clickRemoveCollection(){
    var success = deleteCollection(curCol);
}
function clickCollection(text) {
    $('.listURL span').remove();
    var nameCollection = text;
    curCol = nameCollection;
    $.post('/selectURL/', {nameCollection: nameCollection},
        function (data) {
            var tmp = $.parseJSON(data);
            //alert("data = "+data);
            $(".listURL h2").remove();
            $(".link").remove();
            $(".listURL").prepend('<h2>' + curCol + '</h2>');
            //alert(tmp.length);
            for (var i = 0; i < tmp.length; i++) {
                var url = tmp[i]['fields']['url'];
                //alert(url);
                var tt = "<div class='link'><p><span>" + url +
                    "<div class='h'><a href='"+url+"'><img src='/static/JS/deleteIcon.jpg'/> </a></div>" +
                    "</span></p></div> ";
                //alert(tt);
                $(".listURL").append(tt);
//                $('.listURL').append("<div class='link'><p>"+tmp[i]['fields']['url']+
//                "<a href='#'><div class='h'><img src='/static/JS/deleteIcon.jpg' /></div></div>"+
//                "</p></div>");
            }
            $('.listURL a').bind('click',function(evt){
                evt.preventDefault();
                deleteRSS($(this).attr('href'));
                //$('.listURL div:contains('+$(this).attr('href')+')').remove();

            });


        }
    );
}

function deleteRSS(url){
    $.post('/deleteRSS/', {URL: url, nameCollection: curCol},
        function (data) {
            var response = data;

            if (response == "Success") {
               // alert($('.listURL div:contains('+$(this).attr('href')+')').length);
                //alert($('.link div:contains('+$(this).attr('href')+')').length);
                $('.listURL div:contains('+url+')').remove();
            } else {
                alert("respdd:"+response);
                alert(response);
            }
        }
    );
//    alert("link="+$('.link div:contains()').length);
//    alert($('.listURL div:contains('+url+')').length);
//    $('.listURL div:contains('+url+')').remove();
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
                    var tt = "<div class='link'><p><span>" + nameOfNewRSS +
                        "<div class='h'><a href='"+nameOfNewRSS+"'><img src='/static/JS/deleteIcon.jpg'/> </a></div>" +
                        "</span></p></div> ";
                    $('.listURL').append(tt);
                    $('.listURL a').bind('click',function(evt){
                        evt.preventDefault();
                        deleteRSS($(this).attr('href'));
                        //$('.listURL div:contains('+$(this).attr('href')+')').remove();

                    });
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