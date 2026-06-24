/**
 * CommonDatepicker
 * flatpickr 기반 커스텀 달력 컴포넌트
 * - single / range / split 지원
 * - 오늘 / 초기화 버튼
 */
const CommonDatepicker = (function () {

    /* ── 푸터 (오늘 / 초기화) ── */
    function buildFooter(fp) {
        const footer = document.createElement('div');
        footer.className = 'cdp-footer';

        const todayBtn = document.createElement('button');
        todayBtn.type = 'button';
        todayBtn.className = 'cdp-btn-today';
        todayBtn.textContent = '오늘';
        todayBtn.addEventListener('click', function () {
            fp.jumpToDate(new Date());
        });

        const clearBtn = document.createElement('button');
        clearBtn.type = 'button';
        clearBtn.className = 'cdp-btn-clear';
        clearBtn.textContent = '초기화';
        clearBtn.addEventListener('click', function () {
            fp.clear();
        });

        footer.appendChild(todayBtn);
        footer.appendChild(clearBtn);
        fp.calendarContainer.appendChild(footer);
    }

    const hooks = {
        onReady: function (_, __, fp) {
            buildFooter(fp);
        },
    };

    return {
        init: function (selector, type, options) {
            type = type || 'single';
            options = options || {};

            const base = Object.assign({
                locale: 'ko',
                dateFormat: 'Y-m-d',
                allowInput: true,
                disableMobile: true,
            }, hooks, options);

            if (type === 'single') {
                return flatpickr(selector, Object.assign({}, base, {mode: 'single'}));
            }

            if (type === 'range') {
                return flatpickr(selector, Object.assign({}, base, {mode: 'range'}));
            }

            if (type === 'split') {
                document.querySelectorAll(selector).forEach(function (el) {
                    const startEl = el.querySelector('.date-start');
                    const endEl = el.querySelector('.date-end');
                    if (!startEl || !endEl) {
                        console.warn('[CommonDatepicker] split 그룹에 .date-start 또는 .date-end 가 없습니다.');
                        return;
                    }
                    const fpEnd = flatpickr(endEl, Object.assign({}, base));
                    flatpickr(startEl, Object.assign({}, base, {
                        onChange: function (_, dateStr) {
                            fpEnd.set('minDate', dateStr);
                        }
                    }));
                    if (startEl.value) fpEnd.set('minDate', startEl.value);
                });
            }
        }
    };

})();
