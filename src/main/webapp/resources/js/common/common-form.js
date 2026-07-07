/**
 * 공통 form 유틸
 */
const CommonForm = window.CommonForm = {
    /**
     * 폼 입력값을 객체로 직렬화 (AJAX 전송용). 같은 name 이 여러 개면 배열로 묶임
     * @param {string|Element|jQuery} form - 폼 선택자 또는 요소
     * @returns {Object}
     * @example postAjax("/save", CommonForm.serializeObject("#searchForm"));
     */
    serializeObject: function (form) {
        const result = {};
        const fields = $(form).serializeArray();

        fields.forEach(({name, value}) => {
            if (Object.prototype.hasOwnProperty.call(result, name)) {
                if (!Array.isArray(result[name])) {
                    result[name] = [result[name]];
                }
                result[name].push(value);
                return;
            }

            result[name] = value;
        });

        return result;
    },

    /**
     * 폼 초기화 (HTML 기본 reset — 초기 렌더링 값으로 복원)
     */
    reset: function (form) {
        const element = $(form).get(0);
        if (element) {
            element.reset();
        }
    },

    /**
     * 요소 비활성/활성 토글 (disabled 속성 + is-disabled 클래스 동시 처리)
     * @example CommonForm.setDisabled("#btnSave");         // 비활성
     * @example CommonForm.setDisabled("#btnSave", false);  // 활성
     */
    setDisabled: function (selector, disabled = true) {
        $(selector).prop("disabled", disabled).toggleClass("is-disabled", disabled);
    },

    /**
     * 데이터를 hidden 필드로 담은 임시 폼 생성 (body 에 추가됨, 직접 쓸 일은 드물고 submit() 이 사용)
     * @param {string} action - 전송 URL (컨텍스트 패스 자동 결합)
     * @param {Object} data - hidden 필드로 변환할 데이터 (중첩 객체는 "a.b", 배열은 동일 name 반복)
     * @param {Object} options - {method: "POST", target: "_blank" 등}
     * @returns {jQuery} 생성된 폼
     */
    createTempForm: function (action, data = {}, options = {}) {
        const method = (options.method || "POST").toUpperCase();
        const target = options.target || "";
        const $form = $("<form>", {
            action: resolveCommonFormUrl(action),
            method: method,
            target: target,
            style: "display:none;"
        });

        appendHiddenFields($form, data);
        $("body").append($form);

        return $form;
    },

    /**
     * 데이터를 폼 전송으로 제출 (페이지 이동/파일 다운로드처럼 AJAX 가 아닌 전송에 사용)
     * @example CommonForm.submit("/excel/download", {searchType: "A"});           // 다운로드
     * @example CommonForm.submit("/detail", {id: 3}, {target: "_blank"});          // 새 창
     */
    submit: function (action, data = {}, options = {}) {
        const $form = this.createTempForm(action, data, options);
        $form.get(0).submit();
        $form.remove();
    }
};

/**
 * 객체를 hidden input 들로 재귀 변환 (배열 → 동일 name 반복, 중첩 객체 → "부모.자식" name)
 */
function appendHiddenFields($form, data, prefix) {
    if (!data) return;

    Object.keys(data).forEach((key) => {
        const fieldName = prefix ? `${prefix}.${key}` : key;
        const value = data[key];

        if (Array.isArray(value)) {
            value.forEach((item) => appendHiddenField($form, fieldName, item));
            return;
        }

        if (value && typeof value === "object") {
            appendHiddenFields($form, value, fieldName);
            return;
        }

        appendHiddenField($form, fieldName, value);
    });
}

/**
 * hidden input 1개 추가 (null/undefined 는 빈값 처리)
 */
function appendHiddenField($form, name, value) {
    $("<input>", {
        type: "hidden",
        name: name,
        value: value == null ? "" : value
    }).appendTo($form);
}

/**
 * URL 에 컨텍스트 패스 자동 결합 (절대 URL/이미 결합된 URL 은 그대로)
 */
function resolveCommonFormUrl(url) {
    if (!url) return "";
    if (/^(https?:)?\/\//.test(url)) return url;

    const contextPath = window._contextPath || "";
    return url.startsWith(contextPath) ? url : contextPath + (url.startsWith("/") ? url : "/" + url);
}
