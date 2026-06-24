package com.kt.ktedu.common.util.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    String headerName(); // 엑셀 상단에 표출될 헤더 이름 (예: "사용자 ID")

    int order() default 999; // 엑셀 컬럼 순서
}