# KT 지니어스

# 환경 설정

## 설치 파일
1. jboss wildFly 27.0.1
   https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.zip
2. jdk 21
3. intellij

---

## intellij jboss 연결

1. [Add Configuration...] -> + -> JBoss Server -> Local 선택.
2. Configure... 버튼을 눌러 로컬의 WildFly 27.0.1 설치 디렉토리를 지정합니다.
3. Deployment 탭으로 이동하여 + -> Artifact... -> ktlms-portal:war exploded를 추가합니다.
4. Server 탭 < URL : http://localhost:8080/ 로 변경
5. VM Options : -Dspring.profiles.active=local 입력

[//]: # (   -Djboss.http.config.enable-welcome-root=false)
4. 실행 시 지정된 와일드플라이 서버를 직접 구동하여 배포를 수행합니다.

---

## css, js minify 자동 설정

1. terser, cleancss 설치 (node가 있어야 함)
```
# JS 압축기 설치
npm install -g terser

# CSS 압축기 설치
npm install -g clean-css-cli

```

2. terser, cleancss 위치 확인
```
(Window)


(macOS)
sqi@SQIuiMacBookAir ~ % which terser
/opt/homebrew/bin/terser

sqi@SQIuiMacBookAir ~ % which cleancss
/opt/homebrew/bin/cleancss
```

3. Settings > Plugins > File Watchers 설치 (IDE 재시작)
4. Settings > Tools > File Watchers 이동 후 하단의 + 버튼을 누르고 <custom>을 선택합니다.
5. 상세 설정 입력: CSS, JS 각각 생성

    - Name: CSS Minifier | JS Minifier
    - type: Cascading style sheet | JavaScript
    - Scope: 우측 ... 클릭 -> + 클릭
       - Name: Only Original CSS | Only Original JS
       - Pattern:
         ```
         - css : file:src/main/webapp/resources/css/_.css&&!file:src/main/webapp/resources/css/_.min.css
         - js : file:src/main/webapp/resources/js/_.js&&!file:src/main/webapp/resources/js/_.min.js
         ```
    
    - css
      - Program: cleancss (또는 cleancss 설치 경로)
      - Arguments: -o $FileNameWithoutExtension$.min.css $FileName$
      - Output paths to refresh: $FileNameWithoutExtension$.min.css
      - Working directory: $FileDir$
   
    - js
      - Program: terser (또는 terser 설치 경로)
      - Arguments: $FileName$ -o $FileNameWithoutExtension$.min.js --mangle --compress 
      - Output paths to refresh: $FileNameWithoutExtension$.min.js
      - Working directory: $FileDir$

   - 고급 설정: 하단의 Advanced Options에서 Auto-save edited files to trigger the watcher를 체크 해제

6. common-ui.css 용 file watcher 추가
   - Name: Common UI Minifier
   - type: Cascading style sheet
   - Scope: 우측 ... 클릭 -> + 클릭
      - Name: Common UI
     ```text
     file[ktlms-portal]:src/main/webapp/resources/css/common/components/*.css&&!file[ktlms-portal]:src/main/webapp/resources/css/common/components/*.min.css
     file:src/main/webapp/resources/css/common/common-ui.css
     ```
   - Program: cleancss (또는 cleancss 설치 경로)
   - Arguments: -o $FileDir$/../common-ui.min.css $FileDir$/../common-ui.css
   - Output paths to refresh: $FileDir$/../common-ui.min.css
   - Working directory: $FileDir$

---

# 환경 변수 설정

1. /env 하위에 전달 받은 local.env 파일 넣기
2. Settings > Plugins > EnvFile install
3. Jboss Edit Configurations > Env File > Enable EnvFile > + local.env, common.env 추가

### /resources/conf/env/application.properties

*.env 파일들은 git 에 절대 업로드하지 말 것.

---

# gitignore 파일 리스트

- src/main/resources/firebase/ktgenius-firebase-adminsdk.json
- src/main/resources/saml/*.crt

---

# standalone.xml 변경

/settings/standalone.xml 내용으로 jboss폴더/standalone/configuration/standalone.xml 변경

- 아래 내용 추가 해야 jsp 저장 후 새로고침 시 적용됨

```xml

<servlet-container name="default">
   <jsp-config development="true"
               check-interval="1"
               modification-test-interval="1"
               recompile-on-fail="true"/>
   <websockets/>
</servlet-container>
```

---

# formatter 설정

Settings > Tools > Actions on Save > Reformat Code 체크 (.properties는 제외)