@echo off
chcp 65001 > nul
echo ⚙️ 로컬(local), 개발(dev), 운영(prod) 샘플 파일 생성 중...

:: window 용 bat
set "LOCAL_REAL=src/main/resources/conf/env/application-local.properties"
set "LOCAL_SAMPLE=src/main/resources/conf/env/sample/application-local.properties.sample"
set "DEV_REAL=src/main/resources/conf/env/application-dev.properties"
set "DEV_SAMPLE=src/main/resources/conf/env/sample/application-dev.properties.sample"
set "PROD_REAL=src/main/resources/conf/env/application-prod.properties"
set "PROD_SAMPLE=src/main/resources/conf/env/sample/application-prod.properties.sample"

:: 1. 로컬(local) 파일 샘플화
if exist "%LOCAL_REAL%" (
    powershell -Command "(Get-Content '%LOCAL_REAL%') -replace '(=.*)', '=YOUR_VALUE_HERE' | Set-Content '%LOCAL_SAMPLE%'"
    echo ✅ %LOCAL_SAMPLE% 생성 완료!
) else (
    echo ❌ %LOCAL_REAL% 파일이 없습니다.
)

:: 2. 개발(dev) 파일 샘플화
if exist "%DEV_REAL%" (
    powershell -Command "(Get-Content '%DEV_REAL%') -replace '(=.*)', '=YOUR_VALUE_HERE' | Set-Content '%DEV_SAMPLE%'"
    echo ✅ %DEV_SAMPLE% 생성 완료!
) else (
    echo ❌ %DEV_REAL% 파일이 없습니다.
)

:: 3. 운영(prod) 파일 샘플화
if exist "%PROD_REAL%" (
    powershell -Command "(Get-Content '%PROD_REAL%') -replace '(=.*)', '=YOUR_VALUE_HERE' | Set-Content '%PROD_SAMPLE%'"
    echo ✅ %PROD_SAMPLE% 생성 완료!
) else (
    echo ❌ %PROD_REAL% 파일이 없습니다.
)

echo.
echo 모든 3종 샘플 파일 변환 작업이 끝났습니다.
pause