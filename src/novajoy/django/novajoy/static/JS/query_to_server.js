function send(nameOfNewCollection,updateInterval,sendingTime,format,subject){
    $('.listURL span').remove();
    $('.listURL').innerHTML = "";
    var isAdding = "No";
    curCol = nameOfNewCollection;

    //$('.collection span:last').click();
    $.post('/addCollection/', {newCollection: nameOfNewCollection, updateInterval: updateInterval,sendingTime:sendingTime,format:format, subject:subject},
        function (data) {
            var response = data;
            if (response == "Success") {
                $('.collection').append("<div><p><span>" + nameOfNewCollection + "</span></p></div>");
                $('.collection span:last').on("click", function () {
                    clickCollection($(this).text());
                });
                curCol = nameOfNewCollection;
                $('.collection span:last').click();
                curObject = $('.collection span:last') ;
                $('.collection span:last').click();
            } else {
                alert(response);
            }
        }
    ).always(function(){
            stopLoadingAnimation();
        });
    startLoadingAnimation("addCollection");
}

function sendEditCollection(oldName,nameOfNewCollection,updateInterval,sendingTime,format,subject){
    $.post('/editCollection/', {oldName:oldName,newCollection: nameOfNewCollection, updateInterval: updateInterval,sendingTime:sendingTime,format:format, subject:subject},
        function (data) {
            var response = data;
            if (response == "Success") {
                //<div><p><span title='{{ col }}'>{{ col }}</span></div>
                curObject = $('.collection div:contains('+oldName+') p span').text(nameOfNewCollection);
                curCol=nameOfNewCollection;
                curObject.click();
                // alert($('.collection div:contains('+oldName+')').text());
            }
            if(response=="Error/this name already exist"){
                alert("Error/this name already exist");
            }
        }
    ).always(function(){
            stopLoadingAnimation();
        });
    startLoadingAnimation("editCollection");
}