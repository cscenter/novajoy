function send(nameOfNewCollection,delta_sending_time,format,subject){
    $('.listURL span').remove();
    $('.listURL').innerHTML = "";
    var isAdding = "No";
    curCol = nameOfNewCollection;
    document.getElementById("addURL").disabled = false;
    document.getElementById("editCollection").disabled = false;
    document.getElementById("removeCollection").disabled = false;
    //alert(nameOfNewCollection+"|"+delta_sending_time+"|"+format+"|"+subject+"|");
    $.post('/addCollection/', {newCollection: nameOfNewCollection,delta_sending_time:delta_sending_time,format:format, subject:subject},
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

function sendEditCollection(oldName,nameOfNewCollection,delta_sending_time,format,subject){
    $.post('/editCollection/', {oldName:oldName,newCollection: nameOfNewCollection,delta_sending_time:delta_sending_time,format:format, subject:subject},
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