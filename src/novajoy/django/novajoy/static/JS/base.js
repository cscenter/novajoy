function showDialogRemoveRSS(url){
    $("#dialogRemoveRSS").dialog({autoOpen: false, width: 300, height: 200,closeOnEscape: false,title:"Remove RSS",
        close: function(){
        },
        open:function(){
        },
        buttons: {
            OK: function () {
                deleteRSS(url);
                $(this).dialog("close");
                return false;
            },
            Cancel: function () {
                $(this).dialog("close");
                return false;
            }
        }
    });

    $("#dialogRemoveRSS").dialog("open");
}
function showDialog(func,old_name) {
    $("#dialog1").dialog({autoOpen: false, width: 500, height: 450,closeOnEscape: false,title:"New Collection",
        close: function(){
            document.getElementById('myform').reset();
        },
        open:function(){
            if (old_name.trim()!=""){
                document.forms[0].elements[0].value = name_collection;
                document.forms[0].elements[3].value = subject
                document.forms[0].elements[1].options[delta_sending_time].selected=true;
                document.forms[0].elements[2].options[format].selected=true;
            }else{
                document.getElementById('myform').reset();
            }
        },
        buttons: {
            OK: function () {
                //Data from a form
                var nameOfNewCollection = document.forms[0].elements[0].value;
                var delta_sending_time = document.forms[0].elements[1].value;
                var format = document.forms[0].elements[2].value;
                var subject = document.forms[0].elements[3].value;
                //clean form
                document.getElementById('myform').reset();
                nameOfNewCollection = nameOfNewCollection.trim();
                var isnan = isNaN(parseInt(delta_sending_time));
                if (nameOfNewCollection == "" || subject.trim()=="" ) {
                    alert("Empty field");
                } else{
                    if(old_name!=''){
                        document.getElementById('myform').reset();
                        func(old_name,nameOfNewCollection,delta_sending_time,format,subject);
                    }else{
                        document.getElementById('myform').reset();
                        func(nameOfNewCollection,delta_sending_time,format,subject);
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
    if(old_name!=''){
        var response="";
        var q=$.post('/infoAboutCollection/', {oldName:old_name},
            function (data) {
                response = $.parseJSON(data);

            }
        ).always(function() {
                name_collection = response[0]['fields']['name_collection'];
                subject=response[0]['fields']['subject'];
                delta_sending_time=delta_sending_time_map[response[0]['fields']['delta_sending_time']];
                format=format_map[response[0]['fields']['format']];
                stopLoadingAnimation();$("#dialog1").dialog("open"); }
        );
        startLoadingAnimation("infoCollection");
    }else{
        $("#dialog1").dialog("open");
    }
}

$(document).ready(function () {
    format_map = [];
    format_map['pdf'] = 0;
    format_map['doc'] = 1;
    format_map['html'] = 2;
    format_map['fb2'] = 3;

    delta_sending_time_map=[];
    delta_sending_time_map[3600]=0;
    delta_sending_time_map[7200]=1;
    delta_sending_time_map[14400]=2;
    delta_sending_time_map[21600]=3;
    delta_sending_time_map[43200]=4;
    delta_sending_time_map[86400]=5;
    delta_sending_time_map[172800]=6;
    $(".collection span").bind('click',function(evt){
        evt.preventDefault();
        clickCollection($(this).text());
    });
    if ($('.collection span').length == 0) {
        document.getElementById("addURL").disabled = true;
        document.getElementById("editCollection").disabled = true;
        document.getElementById("removeCollection").disabled = true;
        curCol = "You have no collections";
        curObject=null;
        $(".listURL").prepend('<h2>' + curCol + '</h2>');
    } else {
        document.getElementById("addURL").disabled = false;
        document.getElementById("editCollection").disabled = false;
        document.getElementById("removeCollection").disabled = false;
        curCol = $('.collection span:first').text();
        curObject =$('.collection span:first');
        curObject.click();
    }

});

