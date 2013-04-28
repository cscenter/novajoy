function showDialog(func,old_name) {
    $("#dialog1").dialog({autoOpen: false, width: 500, height: 450,closeOnEscape: false,
        close: function(){
            document.getElementById('myform').reset();
        },
        open:function(){
            if (old_name!=""){
                $.post('/infoAboutCollection/', {oldName:old_name},
                    function (data) {
                        var response = $.parseJSON(data);
                        document.forms[0].elements[0].value = response[0]['fields']['name_collection'];
                        document.forms[0].elements[4].value = response[0]['fields']['subject'];
                        document.forms[0].elements[2].options[sending_time_map[response[0]['fields']['sendingTime']]].selected=true;
                        document.forms[0].elements[3].options[format_map[response[0]['fields']['format']]].selected=true;
                        document.forms[0].elements[1].options[update_time_map[response[0]['fields']['delta_update_time']]].selected=true;
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
                        //alert("Edit");
                        func(old_name,nameOfNewCollection,updateInterval,sendingTime,format,subject);
                    }else{
                        //alert("new");
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

    sending_time_map=[];
    sending_time_map['8:00:00']=0;
    sending_time_map['9:00:00']=1;
    sending_time_map['10:00:00']=2;
    sending_time_map['11:00:00']=3;
    sending_time_map['12:00:00']=4;
    sending_time_map['13:00:00']=5;
    sending_time_map['14:00:00']=6;
    sending_time_map['15:00:00']=7;
    sending_time_map['16:00:00']=8;
    sending_time_map['17:00:00']=9;
    sending_time_map['18:00:00']=10;
    sending_time_map['19:00:00']=11;
    sending_time_map['20:00:00']=12;
    sending_time_map['21:00:00']=13;
    sending_time_map['22:00:00']=14;
    sending_time_map['23:00:00']=15;
    sending_time_map['00:00:00']=16;
    $(".collection span").bind('click',function(evt){
        evt.preventDefault();
        clickCollection($(this).text());
    });
    if ($('.collection span').length == 0) {
        curCol = "You have no collections";
        curObject=null;
        $(".listURL").prepend('<h2>' + curCol + '</h2>');
    } else {
        curCol = $('.collection span:first').text();
        curObject =$('.collection span:first');
        curObject.click();
    }

});

