let hoverTimeouts = {};
let hoverProgressIntervals = {};
let mutedItems = {};
let currentModalIndex = 0;
let modalVideos = [];
let modalVideoLimitTimeout = null;

function createShortItem(short_item, index) {
    let isHovered = false;

    const item = document.createElement('div');
    const isSelfType = short_item.type === 'self';
    const isExternalType = short_item.type === 'external';

    item.className = 'short-item' + (isExternalType ? ' external-only' : '');
    item.dataset.index = index;

    if (isSelfType) {
        mutedItems[index] = true;
    }

    const videoUrl = short_item.videoUrl;
    const videoHTML = isSelfType ? '<video controls muted playsinline preload="metadata"><source src="' + videoUrl + '" type="video/mp4"></video>' : '';
    const muteButtonHTML = isSelfType
        ? '<button class="mute-btn" data-index="' + index + '" type="button">'
        + '<svg viewBox="0 0 24 24" class="volume-off">'
        + '<path d="M11 5L6 9H2v6h4l5 4V5z"/>'
        + '<line x1="23" y1="9" x2="17" y2="15" stroke="#fff" stroke-width="2"/>'
        + '<line x1="17" y1="9" x2="23" y2="15" stroke="#fff" stroke-width="2"/>'
        + '</svg>'
        + '<svg viewBox="0 0 24 24" class="volume-on" style="display:none;">'
        + '<path d="M11 5L6 9H2v6h4l5 4V5z"/>'
        + '<path d="M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07" fill="none" stroke="#fff" stroke-width="2"/>'
        + '</svg>'
        + '</button>'
        : '';

    const progressHTML = isSelfType
        ? '<div class="hover-progress">'
        + '<div class="hover-progress-bar"></div>'
        + '</div>'
        : '';

    const externalMessageHTML = isExternalType
        ? '<div class="external-hover-message">' + TEXT.hoverMsg + '</div>' : '';

    const thumbnail = short_item.thumbnail;
    item.innerHTML = '<img class="thumbnail" src="' + thumbnail + '">'
        + videoHTML
        + muteButtonHTML
        + externalMessageHTML
        + '<div class="gradient-overlay"></div>'
        + progressHTML;

    if (isSelfType) {
        const video = item.querySelector('video');
        const muteBtn = item.querySelector('.mute-btn');
        const progressBar = item.querySelector('.hover-progress-bar');

        if (muteBtn && video) {
            muteBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                toggleMute(index, video, muteBtn);
            });
        }

        item.addEventListener('mouseenter', function () {
            isHovered = true;
            let progress = 0;

            if (progressBar) {
                progressBar.style.width = '0%';
            }

            if (isSelfType && video) {
                hoverTimeouts[index] = setTimeout(function () {
                    if (!isHovered) {
                        return;
                    }

                    if (!video.paused) {
                        return;
                    }

                    item.classList.add('playing');

                    try {

                        video.muted = true;
                        video.playsInline = true;
                        video.play();

                    } catch (err) {
                        console.log("재생 실패:", err);

                    }
                }, 500);
            }

            if (progressBar) {
                hoverProgressIntervals[index] = setInterval(function () {
                    progress += 1;

                    if (progress > 100) {
                        progress = 100;
                        clearInterval(hoverProgressIntervals[index]);
                    }

                    progressBar.style.width = progress + '%';
                }, 300);
            }
        });

        item.addEventListener('mouseleave', function () {
            isHovered = false;
            clearTimeout(hoverTimeouts[index]);
            clearInterval(hoverProgressIntervals[index]);

            item.classList.remove('playing');

            if (isSelfType && video) {
                video.pause();
                video.currentTime = 0;
            }

            if (progressBar) {
                progressBar.style.width = '0%';
            }
        });
    }

    let startX = 0;
    let startY = 0;
    let moved = false;

    item.addEventListener('touchstart', function (e) {
        if (!e.touches || !e.touches.length) return;

        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
        moved = false;
    }, {
        passive: true
    });

    item.addEventListener('touchmove', function (e) {
        if (!e.touches || !e.touches.length) return;

        const diffX = Math.abs(e.touches[0].clientX - startX);
        const diffY = Math.abs(e.touches[0].clientY - startY);

        if (diffX > 10 || diffY > 10) {
            moved = true;
        }
    }, {
        passive: true
    });

    item.addEventListener('touchend', function (e) {
        if (moved) return;

        e.preventDefault();

        if (isSelfType) {
            openModal(index);
            return;
        }

        if (isExternalType && short_item.externalUrl) {
            moveUrl(short_item.externalUrl);
        }
    }, {
        passive: false
    });

    item.addEventListener('click', function () {
        if (isSelfType) {
            openModal(index);
            return;
        }

        if (isExternalType && short_item.externalUrl) {
            moveUrl(short_item.externalUrl);
        }
    });

    return item;
}

// 음소거 토글
function toggleMute(index, video, btn) {
    if (!video || !btn) return;

    mutedItems[index] = !mutedItems[index];
    video.muted = mutedItems[index];

    const volumeOff = btn.querySelector('.volume-off');
    const volumeOn = btn.querySelector('.volume-on');

    if (mutedItems[index]) {
        volumeOff.style.display = 'block';
        volumeOn.style.display = 'none';
    } else {
        volumeOff.style.display = 'none';
        volumeOn.style.display = 'block';
    }
}

// 모달용 숏츠 생성
function createModalShort(short_item, index) {
    const item = document.createElement('div');
    item.className = 'modal-short';
    item.dataset.index = index;

    const thumbnail = short_item.thumbnail;
    const description = short_item.description;
    const videoUrl = short_item.videoUrl;
    const detailUrl = short_item.detailUrl;

    if (short_item.type !== 'self') {
        item.innerHTML = '<img src="' + thumbnail + '"alt="" style="width:100%; height:100%; object-fit:contain; background:#000;">'
            + '<div class="modal-gradient"></div>'
            + '<div class="modal-info">'
            + '<p>' + description + '</p>'
            + '</div>';
        return item;
    }

    item.innerHTML = '<video controls muted playsinline preload="metadata"><source src="' + videoUrl + '" type="video/mp4"></video>'
        + '<div class="modal-gradient"></div>'
        + '<button class="detail-btn" data-url="' + detailUrl + '">'
        + TEXT.detail + '</button>';
    return item;
}

// 초기화
function init() {
    const grid = document.getElementById('shortsGrid');

    if (!grid) {
        console.log('shortsGrid 없음 (JSP 조건 확인 필요)');
    }

    grid.innerHTML = '';

    SHORTS_DATA.forEach(function (short_item, index) {
        const item = createShortItem(short_item, index);
        grid.appendChild(item);
    });
}

// 모달 열기
function openModal(index) {
    const selfShorts = SHORTS_DATA.filter(function (item) {
        return item.type === 'self';
    });

    const clickedShort = SHORTS_DATA[index];
    if (!clickedShort || clickedShort.type !== 'self') return;

    const selfIndex = selfShorts.findIndex(function (item) {
        return item.id === clickedShort.id;
    });

    currentModalIndex = selfIndex;

    const overlay = document.getElementById('modalOverlay');
    const modal = document.getElementById('modal');
    const modalContent = document.getElementById('modalContent');
    const modalIndicator = document.getElementById('modalIndicator');

    overlay.classList.add('active');
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';

    modalContent.innerHTML = '';
    modalIndicator.innerHTML = '';
    modalVideos = [];

    selfShorts.forEach(function (short_item, idx) {
        const item = createModalShort(short_item, idx);
        modalContent.appendChild(item);

        const video = item.querySelector('video');
        modalVideos.push(video || null);

        const dot = document.createElement('div');
        dot.className = 'indicator-dot';
        if (idx === selfIndex) {
            dot.classList.add('active');
        }
        modalIndicator.appendChild(dot);

        const detailBtn = item.querySelector('.detail-btn');
        if (detailBtn) {
            detailBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                const url = this.getAttribute('data-url');
                if (url) {
                    moveUrl(url);
                }
            });
        }
    });

    const targetScroll = selfIndex * modalContent.clientHeight;
    modalContent.scrollTop = targetScroll;

    setTimeout(function () {
        if (modalVideos[selfIndex]) {
            playModalVideoFor30Seconds(modalVideos[selfIndex]);
        }
    }, 50);

    modalContent.removeEventListener('scroll', handleModalScroll);
    modalContent.addEventListener('scroll', handleModalScroll);
}

function playModalVideoFor30Seconds(video) {
    if (!video) return;

    if (modalVideoLimitTimeout) {
        clearTimeout(modalVideoLimitTimeout);
        modalVideoLimitTimeout = null;
    }

    try {
        video.muted = true;
        video.playsInline = true;

        video.currentTime = 0;

        video.play();

    } catch (e) {
        console.log('재생 실패:', e);
    }

    modalVideoLimitTimeout = setTimeout(function () {
        try {
            video.pause();
        } catch (e) {
            console.log('pause 실패:', e);
        }
    }, 30000);
}

// 모달 닫기
function closeModal() {
    const overlay = document.getElementById('modalOverlay');
    const modal = document.getElementById('modal');

    overlay.classList.remove('active');
    modal.classList.remove('active');
    document.body.style.overflow = '';

    if (modalVideoLimitTimeout) {
        clearTimeout(modalVideoLimitTimeout);
        modalVideoLimitTimeout = null;
    }


    modalVideos.forEach(function (video) {
        if (video && !video.paused) {
            video.pause();
            video.currentTime = 0;
        }
    });
}

// 모달 스크롤 처리
function handleModalScroll() {
    const modalContent = document.getElementById('modalContent');
    const scrollTop = modalContent.scrollTop;
    const itemHeight = modalContent.clientHeight;
    const newIndex = Math.round(scrollTop / itemHeight);

    if (newIndex !== currentModalIndex) {
        if (modalVideos[currentModalIndex]) {
            modalVideos[currentModalIndex].pause();
        }

        currentModalIndex = newIndex;

        if (modalVideos[newIndex]) {
            playModalVideoFor30Seconds(modalVideos[newIndex]);
        }

        const dots = document.querySelectorAll('.indicator-dot');
        dots.forEach(function (dot, idx) {
            if (idx === newIndex) {
                dot.classList.add('active');
            } else {
                dot.classList.remove('active');
            }
        });
    }
}

//url 이동
function moveUrl(url) {
    if (!url) {
        return;
    }

    // 과정 콘텐츠
    if (url.indexOf('/education/courseContents') > -1) {
        location.href = url;
        return;
    }

    // 그 외는 앱으로 전달하여 외부링크거나 모바일웹일경우 외부링크
    if (typeof window.ReactNativeWebView !== 'undefined') {
        window.ReactNativeWebView.postMessage(
            JSON.stringify({
                type: 'openUrl',
                url: url
            })
        );
    } else {
        window.open(url, '_blank');
    }
}

// 이벤트 리스너
document.getElementById('closeBtn').addEventListener('click', closeModal);
document.getElementById('modalOverlay').addEventListener('click', closeModal);

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// 페이지 로드 시 초기화
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}