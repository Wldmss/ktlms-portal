/**
 * 공통 file 유틸 (업로드 전 검증/파일 정보 추출)
 */
const CommonFile = window.CommonFile = {
    /**
     * 파일 정보 객체 추출. File 객체와 파일명 문자열 모두 허용
     * @param {File|string} fileOrName - File 객체 또는 파일명
     * @returns {{name: string, baseName: string, extension: string, size: number, displaySize: string}}
     * @example CommonFile.getFileInfo(input.files[0]) → {name:"a.pdf", baseName:"a", extension:"pdf", size:1024, displaySize:"1.0 KB"}
     */
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

    /**
     * 확장자 추출 (소문자, 점 제외. 없으면 "")
     * @example CommonFile.getExtension("report.PDF") → "pdf"
     */
    getExtension: function (fileName) {
        const name = String(fileName || "");
        const index = name.lastIndexOf(".");
        return index < 0 ? "" : name.slice(index + 1).toLowerCase();
    },

    /**
     * 허용 확장자 검사 (대소문자/점 유무 무관). 허용 목록이 비어있으면 항상 true
     * @example CommonFile.validateExtension(file, ["jpg", ".png"]) → true/false
     */
    validateExtension: function (fileOrName, allowedExtensions = []) {
        if (!allowedExtensions || allowedExtensions.length === 0) return true;

        const isFile = typeof File !== "undefined" && fileOrName instanceof File;
        const extension = this.getExtension(isFile ? fileOrName.name : fileOrName);
        const allowed = allowedExtensions.map((item) => String(item).replace(".", "").toLowerCase());

        return allowed.includes(extension);
    },

    /**
     * 파일 용량 검사 (maxBytes 이하면 true). file/maxBytes 없으면 통과
     * @example CommonFile.validateSize(file, 10 * 1024 * 1024) — 10MB 제한
     */
    validateSize: function (file, maxBytes) {
        if (!file || !maxBytes) return true;
        return file.size <= maxBytes;
    },

    /**
     * 확장자+용량 통합 검증. 업로드 전 한 번에 호출하는 용도
     * @param {File} file - 검증할 파일
     * @param {Object} options - {extensions: ["jpg","png"], maxSize: 바이트}
     * @returns {{valid: boolean, message: string}} 실패 시 message 에 안내 문구
     * @example
     * const result = CommonFile.validate(file, {extensions: ["pdf"], maxSize: 5*1024*1024});
     * if (!result.valid) { openAlert(result.message); return; }
     */
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
