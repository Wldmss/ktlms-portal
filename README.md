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
      
     - 고급 설정: 하단의 Advanced Options에서 Auto-save edited files to trigger the watcher를 체크하 해제