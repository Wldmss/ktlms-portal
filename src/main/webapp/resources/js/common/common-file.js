/**
 * 공통 file 유틸
 */
const CommonFile = window.CommonFile = {
    getFileInfo: function (fileOrName) {
        const file = typeof File !== "undefined" && fileOrName instanceof File ? fileOrName : null;
        const name = file ? file.name : String(fileOrName || "");
        const extension = this.getExtension(name);

        return {
            name: name,
            baseName: extension ? name.slice(0, -(extension.length + 1)) : name,
            extension: extension,
            size: file ? file.size : 0,
            displaySize: file && window.Formatter ? window.Formatter.fileSize(file.size) : "0 B"
        };
    },

    getExtension: function (fileName) {
        const name = String(fileName || "");
        const index = name.lastIndexOf(".");
        return index < 0 ? "" : name.slice(index + 1).toLowerCase();
    },

    validateExtension: function (fileOrName, allowedExtensions = []) {
        if (!allowedExtensions || allowedExtensions.length === 0) return true;

        const isFile = typeof File !== "undefined" && fileOrName instanceof File;
        const extension = this.getExtension(isFile ? fileOrName.name : fileOrName);
        const allowed = allowedExtensions.map((item) => String(item).replace(".", "").toLowerCase());

        return allowed.includes(extension);
    },

    validateSize: function (file, maxBytes) {
        if (!file || !maxBytes) return true;
        return file.size <= maxBytes;
    },

    validate: function (file, options = {}) {
        if (!file) {
            return {valid: false, message: "파일을 선택해주세요."};
        }

        if (!this.validateExtension(file, options.extensions)) {
            return {
                valid: false,
                message: `허용되지 않는 파일 형식입니다. (${(options.extensions || []).join(", ")})`
            };
        }

        if (!this.validateSize(file, options.maxSize)) {
            return {
                valid: false,
                message: `파일 용량은 ${window.Formatter.fileSize(options.maxSize)} 이하만 가능합니다.`
            };
        }

        return {valid: true, message: ""};
    }
};
