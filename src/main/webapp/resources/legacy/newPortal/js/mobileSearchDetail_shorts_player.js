window.MobileSearchDetailShortsPlayer = (function () {
    'use strict';

    var hoverTimeouts = {};
    var hoverProgressIntervals = {};
    var mutedItems = {};
    var currentModalIndex = 0;
    var modalVideos = [];
    var modalVideoLimitTimeout = null;
    var shortsData = [];

    var TEXT = {
        detail: '\uC0C1\uC138\uBCF4\uAE30',
        hoverMsg: '\uB0B4\uC6A9\uC774 \uAD81\uAE08\uD558\uB2E4\uBA74 \uBC14\uB85C \uD074\uB9AD'
    };

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
    }

    function bindBaseEvents() {
        var closeBtn = document.getElementById('closeBtn');
        var overlay = document.getElementById('modalOverlay');

        if (closeBtn) {
            closeBtn.onclick = closeModal;
        }

        if (overlay) {
            overlay.onclick = closeModal;
        }

        document.removeEventListener('keydown', handleEscape);
        document.addEventListener('keydown', handleEscape);
    }

    function handleEscape(e) {
        if (e.key === 'Escape') {
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
        var isSelfType = shortItem.type === 'self' && !!shortItem.videoUrl;
        var isExternalType = !isSelfType;
        var isHovered = false;

        var item = document.createElement('div');
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
            bindPreviewEvents(item, index, function () {
                return isHovered;
            }, function (value) {
                isHovered = value;
            });
        }

        bindTouchAndClick(item, shortItem, index, isSelfType);

        return item;
    }

    function createThumbnailHtml(shortItem) {
        return '<img class="thumbnail" src="'
            + attr(shortItem.thumbnail || '/vod/miniLecture/thumb/default_thumbnail.png')
            + '" alt="'
            + attr(shortItem.title || 'Shorts')
            + '" onerror="this.onerror=null; this.src=\'/vod/miniLecture/thumb/default_thumbnail.png\';">';
    }

    function createVideoHtml(shortItem, isSelfType) {
        if (!isSelfType) {
            return '';
        }

        return '<video controls muted playsinline preload="metadata">'
            + '<source src="' + attr(shortItem.videoUrl) + '" type="video/mp4">'
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
        return isExternalType
            ? '<div class="external-hover-message">' + TEXT.hoverMsg + '</div>'
            : '';
    }

    function createProgressHtml(isSelfType) {
        return isSelfType
            ? '<div class="hover-progress"><div class="hover-progress-bar"></div></div>'
            : '';
    }

    function bindPreviewEvents(item, index, getHovered, setHovered) {
        var video = item.querySelector('video');
        var muteBtn = item.querySelector('.mute-btn');
        var progressBar = item.querySelector('.hover-progress-bar');

        if (muteBtn && video) {
            muteBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                toggleMute(index, video, muteBtn);
            });
        }

        item.addEventListener('mouseenter', function () {
            var progress = 0;
            setHovered(true);

            if (progressBar) {
                progressBar.style.width = '0%';
            }

            hoverTimeouts[index] = setTimeout(function () {
                if (!getHovered() || !video || !video.paused) {
                    return;
                }

                item.classList.add('playing');

                try {
                    video.muted = true;
                    video.playsInline = true;
                    video.play();
                } catch (e) {
                    console.log('재생 실패:', e);
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
            setHovered(false);

            clearTimeout(hoverTimeouts[index]);
            clearInterval(hoverProgressIntervals[index]);

            item.classList.remove('playing');

            if (video) {
                video.pause();
                video.currentTime = 0;
            }

            if (progressBar) {
                progressBar.style.width = '0%';
            }
        });
    }

    function bindTouchAndClick(item, shortItem, index, isSelfType) {
        var startX = 0;
        var startY = 0;
        var moved = false;

        item.addEventListener('touchstart', function (e) {
            if (!e.touches || !e.touches.length) {
                return;
            }

            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
            moved = false;
        }, {passive: true});

        item.addEventListener('touchmove', function (e) {
            if (!e.touches || !e.touches.length) {
                return;
            }

            if (Math.abs(e.touches[0].clientX - startX) > 10
                || Math.abs(e.touches[0].clientY - startY) > 10) {
                moved = true;
            }
        }, {passive: true});

        item.addEventListener('touchend', function (e) {
            if (moved) {
                return;
            }

            e.preventDefault();

            if (isSelfType) {
                openModal(index);
            } else {
                openExternal(shortItem);
            }
        }, {passive: false});

        item.addEventListener('click', function () {
            if (isSelfType) {
                openModal(index);
            } else {
                openExternal(shortItem);
            }
        });
    }

    function toggleMute(index, video, btn) {
        mutedItems[index] = !mutedItems[index];
        video.muted = mutedItems[index];

        var off = btn.querySelector('.volume-off');
        var on = btn.querySelector('.volume-on');

        if (!off || !on) {
            return;
        }

        off.style.display = mutedItems[index] ? 'block' : 'none';
        on.style.display = mutedItems[index] ? 'none' : 'block';
    }

    function openExternal(shortItem) {
        var url = shortItem.externalUrl || shortItem.detailUrl;
        moveUrl(url);
    }

    function openModal(index) {
        var clickedShort = shortsData[index];

        if (!clickedShort || clickedShort.type !== 'self' || !clickedShort.videoUrl) {
            return;
        }

        var selfShorts = shortsData.filter(function (item) {
            return item.type === 'self' && !!item.videoUrl;
        });

        var selfIndex = -1;

        selfShorts.forEach(function (item, idx) {
            if (item.id === clickedShort.id) {
                selfIndex = idx;
            }
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

        modalContent.scrollTop = selfIndex * modalContent.clientHeight;

        setTimeout(function () {
            if (modalVideos[selfIndex]) {
                playModalVideoFor30Seconds(modalVideos[selfIndex]);
            }
        }, 50);

        modalContent.removeEventListener('scroll', handleModalScroll);
        modalContent.addEventListener('scroll', handleModalScroll);
    }

    function createModalShort(shortItem, index) {
        var item = document.createElement('div');

        item.className = 'modal-short';
        item.dataset.index = index;

        item.innerHTML =
            '<video controls muted playsinline preload="metadata">'
            + '<source src="' + attr(shortItem.videoUrl) + '" type="video/mp4">'
            + '</video>'
            + '<div class="modal-gradient"></div>'
            + '<button class="detail-btn" data-url="' + attr(shortItem.detailUrl || '') + '" type="button">'
            + TEXT.detail
            + '</button>';

        var detailBtn = item.querySelector('.detail-btn');

        if (detailBtn) {
            detailBtn.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();

                var url = this.getAttribute('data-url');

                if (url) {
                    try {
                        var sStorage = window.sessionStorage;
                        var taskInfo = {
                            taskType: 'MICRO',
                            taskId: getParam(url, 'taskId'),
                            path: location.pathname + location.search
                        };

                        sStorage.setItem(sStorage.length + 1, JSON.stringify(taskInfo));
                    } catch (e) {
                    }

                    moveUrl(url);
                }
            });
        }

        return item;
    }

    function getParam(url, name) {
        var query = url.split('?')[1] || '';
        var params = query.split('&');

        for (var i = 0; i < params.length; i++) {
            var pair = params[i].split('=');

            if (pair[0] === name) {
                return decodeURIComponent(pair[1] || '');
            }
        }

        return '';
    }

    function playModalVideoFor30Seconds(video) {
        if (!video) {
            return;
        }

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
                video.pause();
                video.currentTime = 0;
            }
        });
    }

    function handleModalScroll() {
        var modalContent = document.getElementById('modalContent');

        if (!modalContent || !modalContent.clientHeight) {
            return;
        }

        var newIndex = Math.round(
            modalContent.scrollTop / modalContent.clientHeight
        );

        if (newIndex === currentModalIndex) {
            return;
        }

        if (modalVideos[currentModalIndex]) {
            modalVideos[currentModalIndex].pause();
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

    function attr(value) {
        return safeText(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function safeText(value) {
        if (value === null || value === undefined || value === 'null') {
            return '';
        }

        return String(value);
    }

    function append(data) {
        var grid = document.getElementById('shortsGrid');

        if (!grid || !data || data.length === 0) {
            return;
        }

        data.forEach(function (shortItem) {
            var index = shortsData.length;

            grid.appendChild(createShortItem(shortItem, index));
            shortsData.push(shortItem);
        });
    }

    function moveUrl(url) {
        if (!url) {
            return;
        }

        if (url.indexOf('/education/courseContents') > -1) {
            location.href = url;
            return;
        }

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

    return {
        init: init,
        append: append,
        closeModal: closeModal
    };
})();