/**
 * 공통 ajax 유틸
 */

function getCookie(name) {
    const value = document.cookie
        .split("; ")
        .find(row => row.startsWith(name + "="));

    return value ? decodeURIComponent(value.split("=")[1]) : null;
}

function getContextPath() {
    if (typeof window._contextPath !== "undefined") return window._contextPath;
    if (typeof contextPath !== "undefined") return contextPath;
    return "";
}

function resolveAjaxUrl(url) {
    if (!url) return "";
    if (/^(https?:)?\/\//.test(url)) return url;

    const basePath = getContextPath();
    if (!basePath || url.startsWith(basePath)) return url;

    return basePath + (url.startsWith("/") ? url : "/" + url);
}

/* Bearer token + CSRF 적용 */
$.ajaxSetup({
    beforeSend: function (xhr, settings) {
        const token = window._accessToken;
        if (token) {
            xhr.setRequestHeader("Authorization", "Bearer " + token);
        }

        const method = (settings.type || settings.method || "GET").toUpperCase();
        const csrfToken = getCookie("XSRF-TOKEN");

        if (csrfToken && !["GET", "HEAD", "OPTIONS", "TRACE"].includes(method)) {
            xhr.setRequestHeader("X-XSRF-TOKEN", csrfToken);
        }
    }
});

/* 진행 중인 refresh 요청 (동시다발 401 시 refresh 가 중복 호출되지 않도록 공유) */
let _refreshPromise = null;

/**
 * Access Token 재발급 요청.
 * refresh token(HttpOnly 쿠키)으로 /auth/refresh 를 호출하고, 새 access token 을 반환한다.
 * 이미 진행 중인 refresh 가 있으면 그 Promise 를 그대로 공유한다.
 * @returns {Promise<string|null>} 새 access token (실패 시 reject)
 */
function refreshAccessToken() {
    if (_refreshPromise) return _refreshPromise;

    const refreshUrl = resolveAjaxUrl("/auth/refresh");

    _refreshPromise = $.ajax({
        url: refreshUrl,
        type: "POST",
        dataType: "json",
        global: false
    }).then(function (res) {
        const newToken = res && res.data ? res.data.accessToken : null;
        if (newToken) {
            window._accessToken = newToken; // Bearer 헤더용 갱신 (access_token 쿠키는 서버가 재설정)
        }
        return newToken;
    });

    // 성공/실패와 무관하게 끝나면 다음 만료를 위해 캐시 해제
    _refreshPromise.always(function () {
        _refreshPromise = null;
    });

    return _refreshPromise;
}

/* 세션 만료/권한 없음 시 로그인 화면으로 이동 */
function redirectToLogin() {
    const loginUrl = resolveAjaxUrl("/login");
    if (typeof openAlert === "function") {
        openAlert("세션이 만료되었거나 접근 권한이 없습니다.\n로그인 화면으로 이동합니다.").then(() => {
            location.href = loginUrl;
        });
    } else {
        alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
        location.href = loginUrl;
    }
}

/**
 * 전역 공통 Ajax 함수
 * @param {string} url - 요청할 API 주소
 * @param {Object} data - 서버로 전송할 데이터 객체
 * @param {Object} options - 추가적인 부가 옵션 (method, noBg 등)
 * @returns {Promise}
 * @example
 * callAjax("/api/course/list", { category: "IT", page: 1 })
 *     .then(res => {
 *         // 성공했을 때 화면 그리는 로직만 집중 코딩!
 *         console.log("강좌 리스트 수신 완료:", res);
 *     });
 *
 * callAjax("/api/excel/upload", formData, { noBg: false })
 *     .then(res => { openAlert("업로드 완료!"); });
 *
 * callAjax("/api/course/list",
 *     {
 *         category: "IT",
 *         page: 1
 *     },
 *     {
 *         method: "GET" // ⭐️ 중요: 이 옵션을 주면 GET 방식으로 변경됩니다!
 *     }
 * ).then(res => {
 *     console.log("GET 요청 성공 데이터:", res);
 * });
 */
function callAjax(url, data = {}, options = {}) {
    // 1. 기본 옵션 세팅 (따로 지정 안 하면 POST, JSON 통신이 기본)
    const defaultOptions = {
        method: "POST",
        contentType: "application/json",
        dataType: "json",
        noBg: true,
        handleAuthError: true,
        ...options
    };

    const requestUrl = resolveAjaxUrl(url);
    const method = defaultOptions.method.toUpperCase();
    const isFormData = typeof FormData !== "undefined" && data instanceof FormData;

    // GET 메서드인 경우 데이터 직렬화 처리 분기
    let requestData = data;
    if (!isFormData && method === "POST" && typeof defaultOptions.contentType === "string"
            && defaultOptions.contentType.includes("json")) {
        requestData = JSON.stringify(data);
    }

    return new Promise((resolve, reject) => {
        $.ajax({
            url: requestUrl,
            type: method,
            contentType: isFormData ? false : defaultOptions.contentType,
            processData: isFormData ? false : defaultOptions.processData,
            dataType: defaultOptions.dataType,
            data: requestData,
            global: defaultOptions.global !== false,
            headers: defaultOptions.headers || {},

            success: function (response, textStatus, xhr) {
                resolve(response);
            },

            error: function (xhr, textStatus, errorThrown) {
                console.error(`❌ [Ajax Error] URL: ${url} | Status: ${xhr.status}`);

                // Access Token 만료(401) → refresh 후 원 요청을 1회 재시도.
                // _isRetry 플래그로 재시도 요청이 다시 이 분기로 들어와 무한 루프가 되는 것을 막는다.
                if (xhr.status === 401 && defaultOptions.handleAuthError !== false && !defaultOptions._isRetry) {
                    refreshAccessToken()
                        .then(function (newToken) {
                            if (!newToken) {
                                return $.Deferred().reject().promise();
                            }
                            // 재시도: 자기 자신은 auth 처리를 끄고(중복 리다이렉트 방지) 실패는 아래 catch 로 위임
                            return callAjax(url, data, {...options, _isRetry: true, handleAuthError: false});
                        })
                        .then(resolve)
                        .catch(function () {
                            redirectToLogin();
                            reject(xhr);
                        });
                    return;
                }

                if (defaultOptions.handleAuthError !== false && (xhr.status === 401 || xhr.status === 403)) {
                    redirectToLogin();
                } else if (xhr.status === 500) {
                    if (typeof openAlert === "function") {
                        void openAlert("다시 시도해주세요.");
                    }
                }

                reject(xhr);
            }
        });
    });
}

/**
 * GET 통신
 * @param {string} url - 요청 주소
 * @param {Object} data - 쿼리스트링 파라미터 데이터
 * @param {Object} options - 추가 옵션 (생략 가능)
 */
function getAjax(url, data = {}, options = {}) {
    return callAjax(url, data, {method: "GET", ...options});
}

/**
 * POST 통신
 * @param {string} url - 요청 주소
 * @param {Object} data - HTTP 바디에 담을 JSON 데이터
 * @param {Object} options - 추가 옵션 (생략 가능)
 */
function postAjax(url, data = {}, options = {}) {
    return callAjax(url, data, {method: "POST", ...options});
}
