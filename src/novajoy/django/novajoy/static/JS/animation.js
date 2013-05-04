function startLoadingAnimation(operation) {
    var imgObj = $("#loadImg");
    imgObj.show();
    var centerX=0;
    var centerY=0;
    switch (operation) {
        case "addCollection":
            centerX = $("#newCollection").offset().left + 165;
            centerY = $("#newCollection").offset().top + 10;
            break;
        case "selectRSS":
            centerX=$(".listURL").offset().left;
            centerY=$(".listURL").offset().top -20;
            break;
        case "removeCollection":
            centerX = $("#removeCollection").offset().left + 180;
            centerY = $("#removeCollection").offset().top + 10;
            break;
        case "addRSS":
            centerX = $("#addURL").offset().left + 165;
            centerY = $("#addURL").offset().top + 10;
            break;
        case "editCollection":
            centerX = $("#editCollection").offset().left + 170;
            centerY = $("#editCollection").offset().top + 10;
            break;
        case "infoCollection":
            centerX=$("#editCollection").offset().left+165;
            centerY=$("#editCollection").offset().top+10;
            break;
        default :
            //alert(operation);
            break;
    }
    imgObj.offset(({ top: centerY, left: centerX }));
}

function stopLoadingAnimation() {
    $("#loadImg").hide();
}