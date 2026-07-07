/**
 * 공통 form 유틸
 */
const CommonForm = window.CommonForm = {
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

    reset: function (form) {
        const element = $(form).get(0);
        if (element) {
            element.reset();
        }
    },

    setDisabled: function (selector, disabled = true) {
        $(selector).prop("disabled", disabled).toggleClass("is-disabled", disabled);
    },

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

    submit: function (action, data = {}, options = {}) {
        const $form = this.createTempForm(action, data, options);
        $form.get(0).submit();
        $form.remove();
    }
};

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

function appendHiddenField($form, name, value) {
    $("<input>", {
        type: "hidden",
        name: name,
        value: value == null ? "" : value
    }).appendTo($form);
}

function resolveCommonFormUrl(url) {
    if (!url) return "";
    if (/^(https?:)?\/\//.test(url)) return url;

    const contextPath = window._contextPath || "";
    return url.startsWith(contextPath) ? url : contextPath + (url.startsWith("/") ? url : "/" + url);
}
