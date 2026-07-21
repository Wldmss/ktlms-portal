function funLoad() {
    var topH = $(".fix-con").height();
    $(".con-wrap").css("margin-top", topH);
}

window.onload = funLoad;
window.onresize = funLoad;
