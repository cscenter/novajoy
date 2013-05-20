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

function changedPass(){

    var oldPass;var newPass1='';var newPass2;
    $("#changePassword").dialog({autoOpen: false, width: 400, height: 350,closeOnEscape: false,title:"Changed Password",
        close: function(){
        },
        open:function(){
        },
        buttons: {
            OK: function () {
                var oldPass = document.getElementById('chngPass').elements[0].value
                var newPass1 = document.getElementById('chngPass').elements[1].value;
                var newPass2 = document.getElementById('chngPass').elements[2].value;
                document.getElementById('chngPass').reset();
                //alert(oldPass+ " "+newPass1+" "+newPass2);
                if(newPass1==newPass2 && newPass1!=''){
                    $.post('/changedPassword/', {oldPassword:oldPass,newPassword:newPass1},
                        function (data) {
                            if(data=="Success"){
                                alert("The password is changed");
                            }else{
                                alert("Incorrect old password, try again.");
                            }
                        }
                    )
                }else{
                    alert("Password confirmation doesn't match.");
                }
                $(this).dialog("close");
                return false;
            },
            Cancel: function () {
                document.getElementById('chngPass').reset();
                $(this).dialog("close");
                return false;
            }
        }
    });

    $("#changePassword").dialog("open");

}
function showDialog(func,old_name) {
    $("#dialog1").dialog({autoOpen: false, width: 500, height: 450,closeOnEscape: false,title:"New Collection",
        close: function(){
            document.getElementById('myform').reset();
        },
        open:function(){
            if (old_name.trim()!=""){
                document.getElementById('myform').elements[0].value = name_collection;
                document.getElementById('myform').elements[3].value = subject
                document.getElementById('myform').elements[1].options[delta_sending_time].selected=true;
                document.getElementById('myform').elements[2].options[format].selected=true;
            }else{
                document.getElementById('myform').reset();
            }
        },
        buttons: {
            OK: function () {
                //Data from a form
                var nameOfNewCollection = document.getElementById('myform').elements[0].value;
                var delta_sending_time = document.getElementById('myform').elements[1].value;
                var format = document.getElementById('myform').elements[2].value;
                var subject = document.getElementById('myform').elements[3].value;
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
    format_map['epub'] = 3;

    delta_sending_time_map=[];
    delta_sending_time_map[3600]=0;
    delta_sending_time_map[7200]=1;
    delta_sending_time_map[14400]=2;
    delta_sending_time_map[21600]=3;
    delta_sending_time_map[43200]=4;
    delta_sending_time_map[86400]=5;
    delta_sending_time_map[172800]=6;
    $(".collection a").bind('click',function(evt){
        evt.preventDefault();
        clickCollection($(this).text());
    });
    if($("#newCollection").length!=0){
        if ($('.collection a').length == 0) {
            document.getElementById("addURL").disabled = true;
            document.getElementById("editCollection").disabled = true;
            document.getElementById("removeCollection").disabled = true;
            curCol = "You have no collections";
            curObject=null;
            $(".place_for_name_collection").prepend('<h2>' + curCol + '</h2>');
        } else {
            document.getElementById("addURL").disabled = false;
            document.getElementById("editCollection").disabled = false;
            document.getElementById("removeCollection").disabled = false;
            curCol = $('.collection a:first').text();
            curObject =$('.collection a:first');
            curObject.click();
        }
    }

});

