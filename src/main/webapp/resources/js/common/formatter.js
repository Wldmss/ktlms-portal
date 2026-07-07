/**
 * 데이터 포맷 변환 전역 유틸리티
 */
const Formatter = window.Formatter = {
    nvl: function (value, defaultValue = "") {
        if (value === null || value === undefined) return defaultValue;

        const str = String(value).trim();
        return str === "" ? defaultValue : str;
    },

    removeComma: function (value) {
        return this.nvl(value).replace(/,/g, "");
    },

    /**
     * 숫자 금액 세 자리마다 콤마 추가 (예: 500000 -> "500,000")
     */
    number: function (value, defaultValue = "0") {
        const normalized = this.removeComma(value);
        if (normalized === "") return defaultValue;

        const num = Number(normalized);
        if (isNaN(num)) return defaultValue;
        return num.toLocaleString("ko-KR");
    },

    comma: function (value, defaultValue = "0") {
        return this.number(value, defaultValue);
    },

    price: function (value) {
        return this.number(value, "0");
    },

    decimal: function (value, fractionDigits = 2, defaultValue = "0") {
        const normalized = this.removeComma(value);
        if (normalized === "") return defaultValue;

        const num = Number(normalized);
        if (isNaN(num)) return "0";
        return num.toLocaleString("ko-KR", {
            minimumFractionDigits: fractionDigits,
            maximumFractionDigits: fractionDigits
        });
    },

    /**
     * 날짜 포맷팅 (예: "20260609" 또는 Date객체 -> "2026-06-09")
     */
    date: function (value, delimiter = "-") {
        if (!value) return "-";

        // 정수나 문자열 처리
        let str = String(value).replace(/[^0-9]/g, "");

        if (str.length === 8) {
            return str.replace(/(\d{4})(\d{2})(\d{2})/, `$1${delimiter}$2${delimiter}$3`);
        } else if (str.length === 6) {
            return str.replace(/(\d{4})(\d{2})/, `$1${delimiter}$2`);
        }
        return value; // 규격에 안 맞으면 원본 리턴
    },

    dateTime: function (value, dateDelimiter = "-", timeDelimiter = ":") {
        if (!value) return "-";

        const str = String(value).replace(/[^0-9]/g, "");
        if (str.length >= 14) {
            return str.replace(
                /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2}).*/,
                `$1${dateDelimiter}$2${dateDelimiter}$3 $4${timeDelimiter}$5${timeDelimiter}$6`
            );
        }

        if (str.length === 12) {
            return str.replace(
                /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})/,
                `$1${dateDelimiter}$2${dateDelimiter}$3 $4${timeDelimiter}$5`
            );
        }

        return this.date(value, dateDelimiter);
    },

    time: function (value, delimiter = ":") {
        if (!value) return "-";

        const str = String(value).replace(/[^0-9]/g, "");
        if (str.length >= 6) {
            return str.replace(/(\d{2})(\d{2})(\d{2}).*/, `$1${delimiter}$2${delimiter}$3`);
        }
        if (str.length === 4) {
            return str.replace(/(\d{2})(\d{2})/, `$1${delimiter}$2`);
        }

        return value;
    },

    /**
     * 전화번호 하이픈 자동 삽입 (예: "01012345678" -> "010-1234-5678")
     */
    phone: function (value) {
        if (!value) return "-";
        let str = String(value).replace(/[^0-9]/g, "");

        if (str.length === 11) {
            return str.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
        } else if (str.length === 10) {
            if (str.startsWith("02")) { // 서울 유선전화 (02-XXXX-XXXX)
                return str.replace(/(\d{2})(\d{4})(\d{4})/, "$1-$2-$3");
            }
            return str.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3"); // 일반 유선/인터넷 전화
        } else if (str.length === 9 && str.startsWith("02")) { // 서울 유선전화 짧은 번호 (02-XXX-XXXX)
            return str.replace(/(\d{2})(\d{3})(\d{4})/, "$1-$2-$3");
        }
        return value;
    },

    fileSize: function (bytes, fractionDigits = 1) {
        const size = Number(bytes);
        if (!size || isNaN(size)) return "0 B";

        const units = ["B", "KB", "MB", "GB", "TB"];
        const index = Math.min(Math.floor(Math.log(size) / Math.log(1024)), units.length - 1);
        const value = size / Math.pow(1024, index);

        return `${value.toFixed(index === 0 ? 0 : fractionDigits)} ${units[index]}`;
    },

    percent: function (numerator, denominator, fractionDigits = 1) {
        const top = Number(this.removeComma(numerator));
        const bottom = Number(this.removeComma(denominator));
        if (isNaN(top) || isNaN(bottom) || bottom === 0) return "0";

        return ((top / bottom) * 100).toFixed(fractionDigits).replace(/\.0+$/, "");
    },

    /**
     * 바이트(Byte) 수 계산 (오라클 DB 문자열 길이 검사 대비용 - 한글 3바이트)
     */
    getByteLength: function (str) {
        if (!str) return 0;
        let byteLength = 0;
        for (let i = 0; i < str.length; i++) {
            const code = str.charCodeAt(i);
            // 한글 및 특수문자 (3바이트 처리, 프로젝트 한글 인코딩 설정이 UTF-8인 경우 정석)
            if (code > 127) {
                byteLength += 3;
            } else {
                byteLength += 1;
            }
        }
        return byteLength;
    },

    escapeHtml: function (value) {
        return this.nvl(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    },

    unescapeHtml: function (value) {
        const textarea = document.createElement("textarea");
        textarea.innerHTML = this.nvl(value);
        return textarea.value;
    }
};
