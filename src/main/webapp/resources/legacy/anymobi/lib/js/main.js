$(function () {

    /* 추가_20180131 */
    // 교육과정별 학습현황 팝업
    $(".pie-chart-info").click(function () {
        $(".bar-chartdiv").fadeIn(300);
    });
    $(".btn-pop-close").click(function () {
        $(".bar-chartdiv").fadeOut(200);
    });
    $(".btn-dropdown").click(function () {
        $(this).toggleClass("active");
        $(".dropdown-con").slideToggle(0);
    });
});
