function addRSS() {
    var nameOfNewRSS = prompt("Enter new rssfeed address");
    nameOfNewRSS = nameOfNewRSS.trim();
    if (nameOfNewRSS == "") {
        alert("Empty Field");
    } else {
        $.post('/addRSS/', {nameOfNewRSS: nameOfNewRSS, nameCollection: curCol},
            function (data) {
                var response = data;
                if (response == "Success") {
                    var tt = "<div class='link'><p><span>" +
                        "<div class='h'><a href='"+nameOfNewRSS+"'>"+nameOfNewRSS+"</a><a href='"+nameOfNewRSS+"'><img src='/static/removeFeed1.png'/></a></div>" +
                        "</span></p></div> ";

//                    "<div class='link'><p><span>"+
//                        "<div class='h'><a href='"+url+"'>"+url+" </a><a href='"+url+"'><img src='/static/removeFeed1.png'/></a></div>" +
//                    "</span></p></div> "
                    $('.listURL').append(tt);
                    $('.listURL img').bind('click',function(evt){
                        evt.preventDefault();
                        showDialogRemoveRSS($(this).parent().attr('href'));
                    });
//                    $('.listURL a').bind('click',function(evt){
//                        //evt.preventDefault();
//                        //alert($(this).attr('href'));
//                        showDialogRemoveRSS($(this).attr('href'));
//                        //deleteRSS($(this).attr('href'));
//                        //$('.listURL div:contains('+$(this).attr('href')+')').remove();
//
//                    });
                } else {
                    alert(response);
                }
            }
        ).always(function(){
                stopLoadingAnimation();
            });
        startLoadingAnimation("addRSS");
    }
}

function deleteRSS(url){
    $.post('/deleteRSS/', {URL: url, nameCollection: curCol},
        function (data) {
            var response = data;
            if (response == "Success") {
                $('.listURL div:contains('+url+')').remove();
            } else {
                alert(response);
            }
        }
    ).always(function(){
            stopLoadingAnimation();
        });
    startLoadingAnimation("deleteRSS");
}