/*
 * searchDetail.jsp 전용 Shorts Player
 * 통합검색 페이지 전용.
 */

window.SearchDetailShortsPlayer = (function () {
    'use strict';

    var hoverTimeouts = {};
    var hoverProgressIntervals = {};
    var mutedItems = {};
    var modalVideos = [];
    var currentModalIndex = 0;
    var modalVideoLimitTimeout = null;
    var shortsData = [];
    var rafId = null;

    function init(data) {
        shortsData = data || window.SHORTS_DATA || [];

        resetState();
        bindBaseEvents();
        renderGrid();
    }

    function resetState() {
        Object.keys(hoverTimeouts).forEach(function (key) {
            clearTimeout(hoverTimeouts[key]);
        });

        Object.keys(hoverProgressIntervals).forEach(function (key) {
            clearInterval(hoverProgressIntervals[key]);
        });

        hoverTimeouts = {};
        hoverProgressIntervals = {};
        mutedItems = {};
        modalVideos = [];
        currentModalIndex = 0;

        if (modalVideoLimitTimeout) {
            clearTimeout(modalVideoLimitTimeout);
            modalVideoLimitTimeout = null;
        }

        if (rafId) {
            cancelAnimationFrame(rafId);
            rafId = null;
        }
    }

    function bindBaseEvents() {
        var overlay = document.getElementById('modalOverlay');
        var closeBtn = document.getElementById('closeBtn');

        if (overlay) {
            overlay.onclick = closeModal;
        }

        if (closeBtn) {
            closeBtn.onclick = closeModal;
        }

        document.removeEventListener('keydown', handleEscape);
        document.addEventListener('keydown', handleEscape);
    }

    function handleEscape(event) {
        if (event.key === 'Escape') {
            closeModal();
        }
    }

    function renderGrid() {
        var grid = document.getElementById('shortsGrid');

        if (!grid) {
            return;
        }

        grid.innerHTML = '';

        shortsData.forEach(function (shortItem, index) {
            grid.appendChild(createShortItem(shortItem, index));
        });
    }

    function createShortItem(shortItem, index) {
        var item = document.createElement('div');
        var isSelfType = shortItem.type === 'self' && !!shortItem.videoUrl;
        var isExternalType = !isSelfType;

        item.className = 'short-item' + (isExternalType ? ' external-only' : '');
        item.dataset.index = index;

        if (isSelfType) {
            mutedItems[index] = true;
        }

        item.innerHTML =
            createThumbnailHtml(shortItem)
            + createVideoHtml(shortItem, isSelfType)
            + createMuteButtonHtml(index, isSelfType)
            + createExternalMessageHtml(isExternalType)
            + '<div class="gradient-overlay"></div>'
            + createProgressHtml(isSelfType);

        if (isSelfType) {
            bindSelfVideoEvents(item, index);
        }

        item.addEventListener('click', function () {
            if (isSelfType) {
                openModal(index);
                return;
            }

            openExternal(shortItem);
        });

        return item;
    }

    function createThumbnailHtml(shortItem) {
        return '<img class="thumbnail" src="'
            + escapeAttr(shortItem.thumbnail || '/vod/miniLecture/thumb/default_thumbnail.png')
            + '" alt="'
            + escapeAttr(stripHtml(decodeHtml(shortItem.title || 'Shorts thumbnail')))
            + '" onerror="this.onerror=null; this.src=\'/vod/miniLecture/thumb/default_thumbnail.png\';">';
    }

    function decodeHtml(value) {
        var textarea = document.createElement('textarea');
        textarea.innerHTML = safeText(value);
        return textarea.value;
    }

    function stripHtml(value) {
        return safeText(value).replace(/<[^>]*>/g, '');
    }

    function createVideoHtml(shortItem, isSelfType) {
        if (!isSelfType) {
            return '';
        }

        return '<video controls muted playsinline preload="metadata">'
            + '<source src="' + escapeAttr(shortItem.videoUrl) + '" type="video/mp4">'
            + '</video>';
    }

    function createMuteButtonHtml(index, isSelfType) {
        if (!isSelfType) {
            return '';
        }

        return '<button class="mute-btn" data-index="' + index + '" type="button">'
            + '<svg viewBox="0 0 24 24" class="volume-off">'
            + '<path d="M11 5L6 9H2v6h4l5 4V5z"></path>'
            + '<line x1="23" y1="9" x2="17" y2="15" stroke="#fff" stroke-width="2"></line>'
            + '<line x1="17" y1="9" x2="23" y2="15" stroke="#fff" stroke-width="2"></line>'
            + '</svg>'
            + '<svg viewBox="0 0 24 24" class="volume-on" style="display:none;">'
            + '<path d="M11 5L6 9H2v6h4l5 4V5z"></path>'
            + '<path d="M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07" fill="none" stroke="#fff" stroke-width="2"></path>'
            + '</svg>'
            + '</button>';
    }

    function createExternalMessageHtml(isExternalType) {
        if (!isExternalType) {
            return '';
        }

        return '<div class="external-hover-message">\uB0B4\uC6A9\uC774 \uAD81\uAE08\uD558\uB2E4\uBA74 \uBC14\uB85C \uD074\uB9AD</div>';
    }

    function createProgressHtml(isSelfType) {
        if (!isSelfType) {
            return '';
        }

        return '<div class="hover-progress"><div class="hover-progress-bar"></div></div>';
    }

    function bindSelfVideoEvents(item, index) {
        var isHovered = false;
        var video = item.querySelector('video');
        var muteBtn = item.querySelector('.mute-btn');
        var progressBar = item.querySelector('.hover-progress-bar');

        if (muteBtn && video) {
            muteBtn.addEventListener('click', function (event) {
                event.stopPropagation();
                toggleMute(index, video, muteBtn);
            });
        }

        item.addEventListener('mouseenter', function () {
            var progress = 0;
            isHovered = true;

            if (progressBar) {
                progressBar.style.width = '0%';
            }

            hoverTimeouts[index] = setTimeout(function () {
                if (!isHovered || !video || !video.paused) {
                    return;
                }

                item.classList.add('playing');

                try {
                    video.muted = true;
                    video.playsInline = true;
                    video.play();
                } catch (error) {
                    console.log('재생 실패:', error);
                }
            }, 500);

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

            if (video) {
                try {
                    video.pause();
                    video.currentTime = 0;
                } catch (error) {
                    console.log('preview pause 실패:', error);
                }
            }

            if (progressBar) {
                progressBar.style.width = '0%';
            }
        });
    }

    function toggleMute(index, video, button) {
        if (!video || !button) {
            return;
        }

        mutedItems[index] = !mutedItems[index];
        video.muted = mutedItems[index];

        var volumeOff = button.querySelector('.volume-off');
        var volumeOn = button.querySelector('.volume-on');

        if (!volumeOff || !volumeOn) {
            return;
        }

        if (mutedItems[index]) {
            volumeOff.style.display = 'block';
            volumeOn.style.display = 'none';
        } else {
            volumeOff.style.display = 'none';
            volumeOn.style.display = 'block';
        }
    }

    function openExternal(shortItem) {
        var url = shortItem.externalUrl || shortItem.detailUrl;

        if (!url) {
            return;
        }

        if (isExternalUrl(url)) {
            window.open(url, '_blank');
        } else {
            location.href = url;
        }
    }

    function isExternalUrl(url) {
        return /^https?:\/\//i.test(url) && url.indexOf(location.host) === -1;
    }

    function openModal(index) {
        var clickedShort = shortsData[index];

        if (!clickedShort || clickedShort.type !== 'self' || !clickedShort.videoUrl) {
            return;
        }

        var selfShorts = shortsData.filter(function (item) {
            return item.type === 'self' && !!item.videoUrl;
        });

        var selfIndex = selfShorts.findIndex(function (item) {
            return item.id === clickedShort.id;
        });

        if (selfIndex < 0) {
            return;
        }

        currentModalIndex = selfIndex;

        var overlay = document.getElementById('modalOverlay');
        var modal = document.getElementById('modal');
        var modalContent = document.getElementById('modalContent');
        var modalIndicator = document.getElementById('modalIndicator');

        if (!overlay || !modal || !modalContent || !modalIndicator) {
            return;
        }

        overlay.classList.add('active');
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';

        modalContent.innerHTML = '';
        modalIndicator.innerHTML = '';
        modalVideos = [];

        selfShorts.forEach(function (shortItem, idx) {
            var item = createModalShort(shortItem, idx);
            modalContent.appendChild(item);

            modalVideos.push(item.querySelector('video') || null);

            var dot = document.createElement('div');
            dot.className = 'indicator-dot';

            if (idx === selfIndex) {
                dot.classList.add('active');
            }

            modalIndicator.appendChild(dot);
        });

        setTimeout(function () {
            modalContent.scrollTop = selfIndex * modalContent.clientHeight;

            if (modalVideos[selfIndex]) {
                playModalVideoFor30Seconds(modalVideos[selfIndex]);
            }
        }, 50);

        modalContent.onscroll = onModalScroll;
    }

    function createModalShort(shortItem, index) {
        var item = document.createElement('div');

        item.className = 'modal-short';
        item.dataset.index = index;

        item.innerHTML =
            '<video controls muted playsinline preload="metadata">'
            + '<source src="' + escapeAttr(shortItem.videoUrl) + '" type="video/mp4">'
            + '</video>'
            + '<div class="modal-gradient"></div>'
            + '<button class="detail-btn" data-url="' + escapeAttr(shortItem.detailUrl || '') + '" type="button">\uC0C1\uC138\uBCF4\uAE30</button>';

        var detailBtn = item.querySelector('.detail-btn');

        if (detailBtn) {
            detailBtn.addEventListener('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
                event.stopImmediatePropagation();

                var url = this.getAttribute('data-url');

                if (url) {
                    window.location.href = url;
                }

                return false;
            });
        }

        return item;
    }

    function playModalVideoFor30Seconds(video) {
        if (!video) {
            return;
        }

        if (modalVideoLimitTimeout) {
            clearTimeout(modalVideoLimitTimeout);
        }

        try {
            video.muted = true;
            video.playsInline = true;
            video.currentTime = 0;
            video.play();
        } catch (error) {
            console.log('재생 실패:', error);
        }

        modalVideoLimitTimeout = setTimeout(function () {
            try {
                video.pause();
            } catch (error) {
                console.log('pause 실패:', error);
            }
        }, 30000);
    }

    function closeModal() {
        var overlay = document.getElementById('modalOverlay');
        var modal = document.getElementById('modal');

        if (overlay) {
            overlay.classList.remove('active');
        }

        if (modal) {
            modal.classList.remove('active');
        }

        document.body.style.overflow = '';

        if (modalVideoLimitTimeout) {
            clearTimeout(modalVideoLimitTimeout);
            modalVideoLimitTimeout = null;
        }

        modalVideos.forEach(function (video) {
            if (video) {
                try {
                    video.pause();
                    video.currentTime = 0;
                } catch (error) {
                    console.log('modal video pause 실패:', error);
                }
            }
        });
    }

    function onModalScroll() {
        if (rafId) {
            return;
        }

        rafId = requestAnimationFrame(function () {
            rafId = null;
            handleModalScroll();
        });
    }

    function handleModalScroll() {
        var modalContent = document.getElementById('modalContent');

        if (!modalContent) {
            return;
        }

        var itemHeight = modalContent.clientHeight;

        if (!itemHeight) {
            return;
        }

        var newIndex = Math.round(modalContent.scrollTop / itemHeight);

        if (newIndex < 0 || newIndex >= modalVideos.length) {
            return;
        }

        if (newIndex === currentModalIndex) {
            return;
        }

        if (modalVideos[currentModalIndex]) {
            try {
                modalVideos[currentModalIndex].pause();
            } catch (error) {
                console.log('이전 영상 pause 실패:', error);
            }
        }

        currentModalIndex = newIndex;

        if (modalVideos[newIndex]) {
            playModalVideoFor30Seconds(modalVideos[newIndex]);
        }

        updateIndicator(newIndex);
    }

    function updateIndicator(activeIndex) {
        var dots = document.querySelectorAll('.indicator-dot');

        Array.prototype.forEach.call(dots, function (dot, idx) {
            if (idx === activeIndex) {
                dot.classList.add('active');
            } else {
                dot.classList.remove('active');
            }
        });
    }

    function escapeAttr(value) {
        return safeText(value)
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }

    function safeText(value) {
        if (value === null || value === undefined || value === 'null') {
            return '';
        }

        return String(value);
    }

    return {
        init: init,
        closeModal: closeModal
    };
})();