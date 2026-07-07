/**
 * 데이터 포맷 변환 전역 유틸리티
 */
const Formatter = window.Formatter = {
    /**
     * null/undefined/공백이면 defaultValue, 아니면 trim 된 문자열 반환
     * @example Formatter.nvl(null, "-") → "-" / Formatter.nvl(" a ") → "a"
     */
    nvl: function (value, defaultValue = "") {
        if (value === null || value === undefined) return defaultValue;

        const str = String(value).trim();
        return str === "" ? defaultValue : str;
    },

    /**
     * 콤마 제거 (숫자 파싱 전처리용)
     * @example Formatter.removeComma("1,234") → "1234"
     */
    removeComma: function (value) {
        return this.nvl(value).replace(/,/g, "");
    },

    /**
     * 숫자 금액 세 자리마다 콤마 추가 (예: 500000 -> "500,000")
     * 숫자로 해석 불가하면 defaultValue("0") 반환
     */
    number: function (value, defaultValue = "0") {
        const normalized = this.removeComma(value);
        if (normalized === "") return defaultValue;

        const num = Number(normalized);
        if (isNaN(num)) return defaultValue;
        return num.toLocaleString("ko-KR");
    },

    /**
     * number() 별칭 — 세 자리 콤마
     */
    comma: function (value, defaultValue = "0") {
        return this.number(value, defaultValue);
    },

    /**
     * number() 별칭 — 금액 표시용 (실패 시 "0")
     */
    price: function (value) {
        return this.number(value, "0");
    },

    /**
     * 소수점 고정 자릿수 + 콤마 포맷
     * @example Formatter.decimal(1234.5) → "1,234.50" / Formatter.decimal(1234.567, 1) → "1,234.6"
     */
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
     * 숫자만 추출해 8자리(yyyyMMdd)/6자리(yyyyMM)를 구분자로 조립. 규격 외는 원본 반환, 빈값은 "-"
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

    /**
     * 일시 포맷팅 (예: "20260609143000" -> "2026-06-09 14:30:00")
     * 14자리(초까지)/12자리(분까지) 지원, 그보다 짧으면 date() 로 위임. 빈값은 "-"
     */
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

    /**
     * 시각 포맷팅 (예: "143000" -> "14:30:00", "1430" -> "14:30")
     * 규격 외는 원본 반환, 빈값은 "-"
     */
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
     * 휴대폰(11자리)/유선(10자리)/서울 짧은번호(9자리, 02) 지원. 규격 외는 원본 반환, 빈값은 "-"
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

    /**
     * 바이트 수를 읽기 좋은 용량 문자열로 변환
     * @example Formatter.fileSize(1048576) → "1.0 MB" / Formatter.fileSize(500) → "500 B"
     */
    fileSize: function (bytes, fractionDigits = 1) {
        const size = Number(bytes);
        if (!size || isNaN(size)) return "0 B";

        const units = ["B", "KB", "MB", "GB", "TB"];
        const index = Math.min(Math.floor(Math.log(size) / Math.log(1024)), units.length - 1);
        const value = size / Math.pow(1024, index);

        return `${value.toFixed(index === 0 ? 0 : fractionDigits)} ${units[index]}`;
    },

    /**
     * 백분율 계산 (분자/분모 × 100). 분모 0/변환불가면 "0", 끝자리 .0 제거
     * @example Formatter.percent(30, 200) → "15" / Formatter.percent(1, 3, 2) → "33.33"
     */
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

    /**
     * HTML 특수문자 이스케이프 (XSS 방지 — 사용자 입력을 innerHTML 로 넣기 전에 사용)
     * @example Formatter.escapeHtml('<b>') → "&lt;b&gt;"
     */
    escapeHtml: function (value) {
        return this.nvl(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    },

    /**
     * escapeHtml 반대 — HTML 엔티티를 원래 문자로 복원
     * @example Formatter.unescapeHtml("&lt;b&gt;") → "<b>"
     */
    unescapeHtml: function (value) {
        const textarea = document.createElement("textarea");
        textarea.innerHTML = this.nvl(value);
        return textarea.value;
    }
};
