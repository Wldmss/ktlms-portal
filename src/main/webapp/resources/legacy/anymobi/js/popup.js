$(document).ready(function () {

    // mCustomScrollbar plugin (popup)
    /*$(".j-scrollbar-pop").mCustomScrollbar({
        theme: "minimal-dark",
        scrollInertia: 300,
        mouseWheel:{scrollAmount:250}
    });*/


    // input type style change plugin
    $(".cj-iCheck input").iCheck({
        checkboxClass: "cj-input-chkbox",
        radioClass: "cj-input-radio"
    });


    //
    $(".j-survey-btn").click(function () {
        $(".survey-warp").show();
        $(".j-con-hide").hide();
    });


});