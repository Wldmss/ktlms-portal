/**
 * 공통 ajax 유틸
 */

function getCookie(name) {
    const value = document.cookie
        .split("; ")
        .find(row => row.startsWith(name + "="));

    return value ? decodeURIComponent(value.split("=")[1]) : null;
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
        ...options
    };

    // 전역 contextPath 가 선언되어 있다면 주소 앞에 자동 결합
    const requestUrl = (typeof contextPath !== "undefined" && !url.startsWith(contextPath))
        ? contextPath + url
        : url;

    // GET 메서드인 경우 데이터 직렬화 처리 분기
    let requestData = data;
    if (defaultOptions.method.toUpperCase() === "POST" && defaultOptions.contentType.includes("json")) {
        requestData = JSON.stringify(data);
    }

    return new Promise((resolve, reject) => {
        $.ajax({
            url: requestUrl,
            type: defaultOptions.method,
            contentType: defaultOptions.contentType,
            dataType: defaultOptions.dataType,
            data: requestData,
            global: defaultOptions.global !== false,

            success: function (response, textStatus, xhr) {
                resolve(response);
            },

            error: function (xhr, textStatus, errorThrown) {
                console.error(`❌ [Ajax Error] URL: ${url} | Status: ${xhr.status}`);

                if (xhr.status === 401 || xhr.status === 403) {
                    if (typeof openAlert === "function") {
                        openAlert("세션이 만료되었거나 접근 권한이 없습니다.\n로그인 화면으로 이동합니다.").then(() => {
                            location.href = (typeof contextPath !== "undefined" ? contextPath : "") + "/login";
                        });
                    } else {
                        alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
                        location.href = "/login";
                    }
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