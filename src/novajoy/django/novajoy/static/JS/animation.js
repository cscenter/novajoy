function startLoadingAnimation(operation) {
    var imgObj = $("#loadImg");
    //alert(operation);
    imgObj.show();
    var centerX=0;
    var centerY=0;
    if (operation == "infoCollection") {
        centerX = $("#editCollection").offset().left + 165;
        centerY = $("#editCollection").offset().top + 10;
    } else if (operation == "removeRSS") {

    } else if (operation == "removeCollection") {
        centerX = $("#removeCollection").offset().left + 165;
        centerY = $("#removeCollection").offset().top + 10;
    } else if (operation == "addCollection") {
      //alert("asd");
        centerX = $("#newCollection").offset().left + 165;
        centerY = $("#newCollection").offset().top + 10;
    } else if (operation == "addRSS") {
        centerX = $("#addURL").offset().left + 165;
        centerY = $("#addURL").offset().top + 10;
    } else if (operation == "selectRSS") {

    } else if (operation == "editCollection") {
        centerX = $("#editCollection").offset().left + 165;
        centerY = $("#editCollection").offset().top + 10;
    }else if (operation == "addCollection") {
        centerX = $("#newCollection").offset().left + 165;
        centerY = $("#newCollection").offset().top + 10;
    }

    imgObj.offset(({ top: centerY, left: centerX }));
}

function stopLoadingAnimation() {
    $("#loadImg").hide();
}