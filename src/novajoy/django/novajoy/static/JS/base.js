function send(nameOfNewCollection,updateInterval,sendingTime,format,subject){
    $('.listURL span').remove();
    $('.listURL').innerHTML = "";
    var isAdding = "No";
    curCol = nameOfNewCollection;

    //alert("Hello");
    $('.collection span:last').click();
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
            } else {
                alert(response);
            }
        }
    );
    curObject = $('.collection span:last') ;
//    alert($('.collection span:last').text());
    //$('.collection span:last').click();
}

function sendEditCollection(oldName,nameOfNewCollection,updateInterval,sendingTime,format,subject){
    $.post('/editCollection/', {oldName:oldName,newCollection: nameOfNewCollection, updateInterval: updateInterval,sendingTime:sendingTime,format:format, subject:subject},
        function (data) {
            var response = data;
  //          alert("hello "+response);
            if (response == "Success") {
                //<div><p><span title='{{ col }}'>{{ col }}</span></div>
                curObject = $('.collection div:contains('+oldName+') p span').text(nameOfNewCollection);
                curCol=nameOfNewCollection;
                curObject.click();
               // alert($('.collection div:contains('+oldName+')').text());
                //curCol = nameCollection;
            }
            if(response=="Error/this name already exist"){
                alert("Error/this name already exist");
            }
        }
    );
}
function editCollection(){
    showDialog(sendEditCollection,curCol);
}

function clickNewCollection(){
    showDialog(send,"");
}
function showDialog(func,old_name) {
    $("#dialog1").dialog({autoOpen: false, width: 500, height: 650,closeOnEscape: false,
        close: function(){
            document.getElementById('myform').reset();
        },
        open:function(){
          if (old_name!=""){
//              document.forms[0].elements[0].value=curCol;

              $.post('/infoAboutCollection/', {oldName:old_name},
                  function (data) {
                      var response = $.parseJSON(data);
                      document.forms[0].elements[0].value = response[0]['fields']['name_collection'];
                      document.forms[0].elements[4].value = response[0]['fields']['subject'];
                      //alert(response[0]['fields']['delta_update_time']);
                      document.forms[0].elements[3].options[format_map[response[0]['fields']['format']]].selected=true;
                      document.forms[0].elements[1].options[update_time_map[response[0]['fields']['delta_update_time']]].selected=true;
                      //alert(response[0]['fields']['subject']);
                  }
              );
          }
        },
        buttons: {
        OK: function () {
            //Data from a form
            var nameOfNewCollection = document.forms[0].elements[0].value;
            var updateInterval = document.forms[0].elements[1].value;
            var sendingTime = document.forms[0].elements[2].value;
            var format = document.forms[0].elements[3].value;
            var subject = document.forms[0].elements[4].value;
            //clean form
            document.getElementById('myform').reset();
            nameOfNewCollection = nameOfNewCollection.trim();
            var isnan = isNaN(parseInt(updateInterval));
            if (nameOfNewCollection == "" || subject.trim()=="" ) {
                alert("Empty field");
            } else{
                if(old_name!=''){
                    alert("Edit");
                    func(old_name,nameOfNewCollection,updateInterval,sendingTime,format,subject);
                }else{
                    alert("new");
                    func(nameOfNewCollection,updateInterval,sendingTime,format,subject);
                }
            }
            $(this).dialog("close");
            return false;
        },
        Cancel: function () {
            document.getElementById('myform').reset();
            $(this).dialog("close");
            return false;
        }
    }
    });
    $("#dialog1").dialog("open");
}

function deleteCollection(nameCollection){
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
}

function clickRemoveCollection(){
    var success = deleteCollection(curCol);
}

function clickCollection(text) {
    $('.listURL span').remove();
    var nameCollection = text;

    curObject = $('.collection div:contains('+text+')');
    curCol = nameCollection;
    $.post('/selectURL/', {nameCollection: nameCollection},
        function (data) {
            var tmp = $.parseJSON(data);
            $(".listURL h2").remove();
            $(".link").remove();
            $(".listURL").prepend('<h2>' + curCol + '</h2>');
            for (var i = 0; i < tmp.length; i++) {
                var url = tmp[i]['fields']['url'];
                var tt = "<div class='link'><p><span>" + url +
                    "<div class='h'><a href='"+url+"'><img src='/static/JS/deleteIcon.jpg'/> </a></div>" +
                    "</span></p></div> ";
                $(".listURL").append(tt);
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
    format_map = [];
    format_map['pdf'] = 0;
    format_map['doc'] = 1;
    format_map['html'] = 2;
    format_map['fb2'] = 3;

    update_time_map = []
    update_time_map['900']=0;
    update_time_map['1800']=1;
    update_time_map['3600']=2;
    update_time_map['7200']=3;
    update_time_map['14400']=4;

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
