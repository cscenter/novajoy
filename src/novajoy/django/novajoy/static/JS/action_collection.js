function clickCollection(text) {
    $('.listURL span').remove();
    var nameCollection = text;
    if(curObject!=null){
        curObject.removeAttr('id');
    }
    curObject = $('.collection div:contains('+text+')');
    curCol = nameCollection;
    curObject.attr('id','currentCollection');
    $(".place_for_name_collection h2").remove();
    $(".link").remove();
    $(".place_for_name_collection").prepend('<h2>' + curCol + '</h2>');
    $.post('/selectURL/', {nameCollection: nameCollection},
        function (data) {
            var tmp = $.parseJSON(data);
            for (var i = 0; i < tmp.length; i++) {
                var url = tmp[i]['fields']['url'];
                var tt = "<div class='link'><p><span>"+
                    "<div class='h'><a href='"+url+"'>"+url+" </a></div>" +
                    "</span></p></div> ";
                $(".listURL").append(tt);
            }
            $('.listURL a').bind('click',function(evt){
                evt.preventDefault();
                //alert($(this).attr('href'));
                showDialogRemoveRSS($(this).attr('href'));
                //deleteRSS($(this).attr('href'));
            });
        }
    ).always(function(){
            stopLoadingAnimation();
        });
    startLoadingAnimation("selectRSS");
}

function clickRemoveCollection(){
    var success = deleteCollection(curCol);
}

function deleteCollection(nameCollection){
    if ($('.collection span').length == 1) {
        document.getElementById("addURL").disabled = true;
        document.getElementById("editCollection").disabled = true;
        document.getElementById("removeCollection").disabled = true;
    }
    $.post('/deleteCollection/', {nameCollection: curCol},
        function (data) {
            var response = data;
            if (response == "Success") {
                curObject.remove();
                if($('.collection span').length>0){
                    curObject= $('.collection span:last');
                    curCol = $('.collection span:last').text();
                    curObject.click();
                }else{
                    curCol = "You have no collections";
                    $('.listURL').empty();
                    $('.place_for_name_collection').empty();
                    $(".place_for_name_collection").prepend('<h2>' + curCol + '</h2>');
                }
                return true;
            } else {
                alert(response);
                return false;
            }
        }
    ).always(function(){
            stopLoadingAnimation();
        });
    startLoadingAnimation("removeCollection");
}

function editCollection(){
    showDialog(sendEditCollection,curCol);
}

function clickNewCollection(){
    showDialog(send,"");
}