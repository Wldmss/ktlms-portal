# env 파일

application.properties에 ${} 로 설정되어 있는 환경 변수 파일

## common.env

- 모든 profile 공통 변수

## local.env

- 로컬 profile 변수

## dev.env

- 개발 profile 변수

## production.env

- 운영 profile 변수

---

# 환경변수 key 생성 공식

환경변수 key 생성 공식 : key 값을 점(.)과 하이픈(-)을 언더바(_)로 바꾸고 대문자로 만든다. (권장)

- spring 기반에서는 application.properties에 선언된 key 값을 위 공식에 따라 생성된 이름의 변수를 .env에서 먼저 조회 -> 없는 경우 ${} 내의 key 값으로 조회.
- key 값은 겹치지 않아야 하고, 가능한 공식을 지켜서 생성

---

# env 수정하는 경우

새로운 환경 변수 추가 시, 각 profile에 맞게 application.properties에 선언, {profile}.env에 변수 선언, 수정된 .env 파일은 팀 전체에 메일 또는 space에 공유*

- 공개되거나 유출되어도 상관없고 모든 profile의 값이 동일한 변수는 application.properties에 string 값으로 작성

---

# 환경 설정 방법

## intellij(로컬) 설정

1. /env 하위에 전달 받은 local.env 파일 넣기
2. Settings > Plugins > EnvFile install
3. Jboss Edit Configurations > Env File > Enable EnvFile > + local.env, common.env 추가

## 개발, 운영 서버 설정

1. azure keyvault 에 {profile}.env 의 값 등록
2. Secrets Store CSI Driver 방식으로 값 로드 (설정 필요)