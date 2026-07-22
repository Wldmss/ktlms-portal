1. 모든 답변은 한글로 한다.
2. 현재 경로의 프로젝트 ktlms-portal 프로젝트는 'portal' 이라고 칭한다.
2. C:\workspace\KTedu_jenkins  프로젝트는 'genius' 라고 칭한다.
3. genius 프로젝트는 jdk1.7 레거시 프로젝토이다.
4. portal 프로젝트는 Jdk21, spring 6, Jboss 38.0.1 버전의 신규 프로젝트이다.
5. portal 프로젝트는 genius 프로젝트를 이관할 프로젝트이다.
6. genius를 portal로 마이그레이션을 진행한다.
7. genius의 코드를 최소한으로 수정하되, deprecated 된 기술에 대해서는 신규 기술로 대체하며 /migration에 .md 파일로 가이드를 작성한다.
8. 우선순위는 공통 기능 - 정적 리소스 - 공통 소스 - jsp - java - xml 순으로 이관한다.
9. 리소스는 portal의 webapp/resources/legacy 하위에 원본계열을 유지해 파일을 이관하고, 모든 소스 이관 이후 파일 통합, 정리 재설정을 하며, 이관된 경로는
   /migration/migration_resource_path.md 파일에 가이드 문서를 정의한다.
10. genius 는 /mobile/m , / 로 웹과 모바일 소스가 나눠져 있다. jsp 파일 중 genius의 /mobile/mobile/m 소스는 portal의
    /webapp/WEB-INF/views/mobile/m 하위로 이관하며, 웹소스는 /webapp/WEB-INF/views 하위로 이관한다.
11. portal /resouces/vender 에 넣어둔 라이브러리들은 페이지 이관 시 vendor 내 소스로 include를 변경한다.
12. jquery, bootstrap은 webjars 로 resource.jsp 에 전역 설정되어 있기 때문에, 소스 상에서 include가 되어 있다면 삭제한다.
13. jsp, java 소스 이관 시 .do url 로 선언되어 있는 부분은 clean url 로 변경한다.
14. /mobile/t 관련 url은 미사용하므로 삭제한다.