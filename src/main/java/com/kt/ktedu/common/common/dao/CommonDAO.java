package com.kt.ktedu.common.common.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * genius {@code CommonDAO}(iBATIS2 제네릭 DAO) 이관용 shim.
 *
 * <p>MyBatis {@link SqlSessionTemplate} 위에서 genius 와 <b>동일한 메서드 시그니처</b>
 * (getList / getView / getPageList / insert / update / delete) 를 제공한다.
 * 그래서 이관 대상 비즈니스 코드의 {@code commonDAO.getList(map, "ns.queryId")} 호출부를
 * (import 변경 외) <b>수정 없이 그대로</b> 재사용할 수 있다.</p>
 *
 * <p>genius 전체에서 실제 사용되는 메서드만 포함한다
 * (getList / getView / insert / update / getPageList / delete).
 * {@code *2}(2nd datasource) · batch · solr · excel 변형은 해당 기능 이관 시 추가한다.</p>
 *
 * <p>동작 호환 포인트:</p>
 * <ul>
 *   <li>statement id 는 genius 와 동일한 {@code "namespace.queryId"} 문자열
 *       (iBATIS sqlMap XML → MyBatis mapper XML 변환 시 namespace/id 유지).</li>
 *   <li>결과 맵 키는 genius {@code SharedMethods.getFieldName} 과 동일하게 camelCase 로 정규화한다.</li>
 *   <li>getPageList 반환 맵 키는 genius 와 동일하게 {@code count} / {@code output}.</li>
 * </ul>
 *
 * <p>참고 문서: /migration/ibatis-to-mybatis-guide.md</p>
 */
@Repository("commonDAO")
public class CommonDAO {

    private final SqlSessionTemplate sql;

    public CommonDAO(SqlSessionFactory sqlSessionFactory) {
        this.sql = new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 목록 조회 (genius {@code getList}). 결과 맵 키는 camelCase 로 정규화.
     */
    public List<Map<String, Object>> getList(Map<String, Object> input, String queryId) {
        List<Map<String, Object>> list = sql.selectList(queryId, input);
        camelCaseKeys(list);
        return list;
    }

    /**
     * 단건 조회 (genius {@code getView}).
     */
    public Object getView(Map<String, Object> input, String queryId) {
        Object output = sql.selectOne(queryId, input);
        if (output instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) output;
            camelCaseKeys(map);
        }
        return output;
    }

    /**
     * 페이징 목록 (genius {@code getPageList}).
     * 반환 맵 키는 genius 와 동일하게 {@code count}(전체 건수) / {@code output}(리스트).
     */
    public Map<String, Object> getPageList(Map<String, Object> input, String countQueryId, String listQueryId) {
        // rowsPerPage / startLimit 계산 (genius 로직)
        Object rpp = input.get("rowsPerPage");
        if (rpp != null && !"".equals(rpp)) {
            int rowsPerPage = Integer.parseInt(String.valueOf(rpp));
            Object pageObj = input.get("page");
            int page = (pageObj == null || "".equals(pageObj)) ? 1 : Integer.parseInt(String.valueOf(pageObj));
            input.put("startLimit", rowsPerPage * (page - 1));
        }

        Map<String, Object> output = new HashMap<>();
        output.put("count", sql.selectOne(countQueryId, input));

        List<Map<String, Object>> list = sql.selectList(listQueryId, input);
        camelCaseKeys(list);
        output.put("output", list);
        return output;
    }

    /**
     * 등록 (genius {@code insert}).
     *
     * <p>⚠️ iBATIS 의 insert 는 생성키(Object) 를 반환했지만 MyBatis 의 insert 는 영향 행수(int) 를 반환한다.
     * 생성키가 필요하면 mapper 의 {@code <selectKey>} 가 파라미터 맵(input) 에 값을 채우므로
     * 호출 후 {@code input.get("키")} 로 접근한다. (insert 반환값 자체를 키로 쓰던 코드만 점검)</p>
     */
    public Object insert(Map<String, Object> input, String queryId) {
        return sql.insert(queryId, input);
    }

    /**
     * 수정 (genius {@code update}). 영향 행수 반환.
     */
    public int update(Map<String, Object> input, String queryId) {
        return sql.update(queryId, input);
    }

    /**
     * 삭제 (genius {@code delete}). 영향 행수 반환.
     */
    public int delete(Map<String, Object> input, String queryId) {
        return sql.delete(queryId, input);
    }

    // ── 내부 유틸 ─────────────────────────────────────────────

    private void camelCaseKeys(List<Map<String, Object>> list) {
        if (list == null) return;
        for (Map<String, Object> row : list) {
            if (row instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = row;
                camelCaseKeys(map);
            }
        }
    }

    /**
     * 결과 맵 키를 camelCase 로 정규화 (genius {@code SharedMethods.bindDAOObjectOnlyKey} 의 키 변환부).
     */
    private void camelCaseKeys(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return;
        Map<String, Object> converted = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (e.getValue() != null) {
                converted.put(toCamelCase(e.getKey()), e.getValue());
            }
        }
        map.clear();
        map.putAll(converted);
    }

    /**
     * {@code USER_NAME → userName} (genius {@code SharedMethods.getFieldName}).
     * MyBatis 의 mapUnderscoreToCamelCase 와의 이중 변환을 막기 위해
     * <b>언더스코어가 있을 때만</b> 변환한다(이미 camelCase 인 키는 그대로 둔다).
     */
    private String toCamelCase(String key) {
        if (key == null) return "";
        if (key.indexOf('_') < 0) return key;
        String[] parts = key.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            if (sb.isEmpty()) {
                sb.append(p);
            } else {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
            }
        }
        return sb.toString();
    }
}
