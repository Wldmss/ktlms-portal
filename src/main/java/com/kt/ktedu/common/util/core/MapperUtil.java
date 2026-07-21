package com.kt.ktedu.common.util.core;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 매퍼(또는 임의) 호출을 안전하게 감싸 예외 시 기본값을 반환하는 유틸.
 *
 * <p>⚠️ 예외를 조용히 삼키므로 <b>이관 과도기용</b>이다.
 * (DB/테이블/매퍼가 아직 완비되지 않은 구간에서 로그인 등 흐름이 죽지 않게 하기 위함)
 * 정상 코드는 매퍼를 직접 호출(예외 전파)하는 것을 기본으로 한다.</p>
 *
 * <p><b>이관 완료 후 제거 방법</b> — 각 호출부의 fallback 의도를 구분해 걷어낸다:</p>
 * <ul>
 *   <li>fallback 이 <b>정상 기본값</b>(조회 결과 없음 → 기본값)이면:
 *       {@code callOrDefault(() -> m.x(p), 0)} → {@code Optional.ofNullable(m.x(p)).orElse(0)}
 *       (예외는 전파, "없으면 기본값"만 유지)</li>
 *   <li>fallback 이 <b>에러 숨김용</b>이었으면: 래핑 제거 후 매퍼 직접 호출
 *       ({@code m.x(p)}). 에러는 {@code GlobalExceptionHandler} + {@code @Transactional} 롤백으로 처리.</li>
 * </ul>
 * <p>모든 {@code callOrDefault} 참조가 사라지면 이 클래스를 삭제한다.
 * 판단 기준: "이 fallback 이 <i>정상 상황의 기본값</i>인가, <i>에러를 숨기려던 것</i>인가?"</p>
 */
@Slf4j
public class MapperUtil {

    private MapperUtil() {
        // 인스턴스화 방지
    }

    /**
     * {@code supplier} 를 실행하고, 예외가 나면 {@code fallback} 을 반환한다.
     *
     * @param supplier 매퍼 호출 등 실행할 로직
     * @param fallback 예외 발생 시 반환할 기본값
     */
    public static <T> T callOrDefault(Supplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.debug("mapper call skipped: {}", e.getMessage());
            return fallback;
        }
    }
}
