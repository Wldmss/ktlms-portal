환경변수 값이 수십 개, 수백 개로 불어나기 시작하면 인텔리제이 VM Options 창이나 ArgoCD의 YAML 파일이 환경변수 선언문으로 주렁주렁 도배되어 관리 지옥이 펼쳐집니다.

이렇게 **환경변수가 너무 많을 때 클라우드 네이티브 아키텍처(Azure + ArgoCD)에서 사용하는 실무 끝판왕 해결책 2가지**를 알려드릴게요. 핵심은 "개별 변수로 쪼개지 말고, 통째로 넘기거나 주입 방식을
바꾸는 것"입니다.

---

## 🛠️ [해결책 ①] 제일 추천: Azure Key Vault의 '통파일(JSON/Properties)' 맵핑 방식

비밀번호, URL, 폴더 경로 등 수십 개의 변수를 하나하나 따로 등록하지 않고, **`application-dev.properties` 파일 내용 전체를 통째로 Azure Key Vault에 텍스트 형태로 저장**
하는
방식입니다.

### 1. Azure Key Vault 세팅

Key Vault에 `db.url`, `db.password`를 따로 만들지 않고, `application-env-secret`이라는 이름의 비밀(Secret)을 딱 하나만 만듭니다. 그리고 그 **Value(값)
칸에 파일
내용을 통째로 복사·붙여넣기** 합니다.

```properties
# 🔒 Azure Key Vault의 'application-env-secret' 뱃속 내용
db.url=jdbc:postgresql://azure-postgres:5432/db
db.username=ktgenius
db.password=secret1234!
pdfPath=/app/upload/pdf/
naverMapKey=naver1234key
# ... 앞으로 수십 개가 늘어나도 여기에 줄바꿈으로 그냥 쭉 적으면 끝!

```

### 2. ArgoCD 매니페스트 세팅 (볼륨 마운트 맵핑)

ArgoCD(K8s/컨테이너 환경)에서는 이 통짜 비밀값을 환경변수가 아닌, **서버 내부의 진짜 물리적인 파일 경로로 자동 생성(볼륨 마운트)** 해 주도록 설정을 잡습니다.

이렇게 하면 ArgoCD가 배포될 때 Azure Key Vault의 통짜 텍스트를 긁어와 Rocky Linux 내부의 `/env/conf/properties/application-dev.properties` 자리에
**파일을
실시간으로 뚝딱 구워내 줍니다.**

### 3. 스프링 설정 (`KT-properties.xml`)

이 방식을 쓰면 우리가 앞서 설계했던 가장 직관적인 프로파일 덮어쓰기 코드를 단 한 줄도 고치지 않고 그대로 쓸 수 있습니다!

```xml

<util:properties id="config"
                 location="classpath:conf/properties/application-common.properties,
                           ${jboss.config.path:classpath:conf/properties/application-${spring.profiles.active:local}.properties}"/>

```

---

## 💻 이렇게 했을 때 로컬(IntelliJ) 개발자 세팅은?

어떤 방식을 선택하든 로컬 개발자는 전혀 타격이 없습니다!

* **[해결책 ①]의 경우:** 내 컴퓨터 `src/main/resources/conf/properties/application-local.properties` 파일 안에 내가 쓸 변수들을 주렁주렁 편하게 적어두고
  쓰면
  끝납니다. (어차피 Git Actions나 서버에서만 Key Vault 통파일을 읽으니까요.)
  능을 백분 활용하는 방식이라 소스코드나 구동 스크립트가 수백 개의 변수 선언문으로 더러워지는 것을 원천 차단할 수 있습니다. 변수가 계속 늘어날 조짐이 보인다면 이 통파일 맵핑 방식으로 깔끔하게 기틀을
  잡아보세요! 🚀 😊

---

# 인프라 멀티 배포 가이드 (ArgoCD 연동)

이 스크립트를 통해 브랜치에 따라 완전히 분리된 ktlms-portal-dev-war 또는 ktlms-portal-prod-war가 생성됩니다.

이후 ArgoCD(또는 배포 스크립트)에서 이 각각의 파일들을 낚아채서 Azure Rocky Linux 개발 서버와 운영 서버에 꽂아줄 때, 우리가 앞서 설계한 JBoss 구동 프로파일을 서버별로 각각 매칭해 주시면
완전히 마감됩니다.

개발 서버 ArgoCD / JBoss 옵션:

```Plaintext
-Dspring.profiles.active=dev -Djboss.config.path=file:/env/conf/properties/application-dev.properties
운영 서버 ArgoCD / JBoss 옵션:
```

```Plaintext
-Dspring.profiles.active=prod -Djboss.config.path=file:/env/conf/properties/application-prod.properties

```