$(document).ready(function () {
    var swiper = new Swiper('.swiper-container1 .swiper-wrap', {
        slidesPerView: 'auto',
        spaceBetween: 10,
        observer: true,
        observeParents: true,
        navigation: {
            nextEl: '.swiper-container1 .swiper-button-next',
            prevEl: '.swiper-container1 .swiper-button-prev',
        },
    });
    var swiper = new Swiper('.swiper-container2 .swiper-wrap', {
        slidesPerView: 'auto',
        spaceBetween: 20,
        observer: true,
        observeParents: true,
        navigation: {
            nextEl: '.swiper-container2 .swiper-button-next',
            prevEl: '.swiper-container2 .swiper-button-prev',
        },
    });
    var swiper = new Swiper('.swiper-container3 .swiper-wrap', {
        slidesPerView: 'auto',
        spaceBetween: 10,
        observer: true,
        observeParents: true,
        navigation: {
            nextEl: '.swiper-container3 .swiper-button-next',
            prevEl: '.swiper-container3 .swiper-button-prev',
        },
    });

})