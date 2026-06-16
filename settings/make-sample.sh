#!/bin/bash

# mac 용 sh
echo "⚙️  로컬(local), 개발(dev), 운영(prod) 샘플 파일 생성 중..."

LOCAL_REAL="src/main/resources/conf/env/application-local.properties"
LOCAL_SAMPLE="src/main/resources/conf/env/sample/application-local.properties.sample"
DEV_REAL="src/main/resources/conf/env/application-dev.properties"
DEV_SAMPLE="src/main/resources/conf/env/sample/application-dev.properties.sample"
PROD_REAL="src/main/resources/conf/env/application-prod.properties"
PROD_SAMPLE="src/main/resources/conf/env/sample/application-prod.properties.sample"

# 1. 로컬(local) 파일 샘플화
if [ -f "$LOCAL_REAL" ]; then
    awk -F= '{if(NF>1) print $1"=YOUR_VALUE_HERE"; else print $0}' "$LOCAL_REAL" > "$LOCAL_SAMPLE"
    echo "✅ $LOCAL_SAMPLE 생성 완료!"
else
    echo "❌ $LOCAL_REAL 파일이 없습니다."
fi

# 2. 개발(dev) 파일 샘플화
if [ -f "$DEV_REAL" ]; then
    awk -F= '{if(NF>1) print $1"=YOUR_VALUE_HERE"; else print $0}' "$DEV_REAL" > "$DEV_SAMPLE"
    echo "✅ $DEV_SAMPLE 생성 완료!"
else
    echo "❌ $DEV_REAL 파일이 없습니다."
fi

# 3. 운영(prod) 파일 샘플화
if [ -f "$PROD_REAL" ]; then
    awk -F= '{if(NF>1) print $1"=YOUR_VALUE_HERE"; else print $0}' "$PROD_REAL" > "$PROD_SAMPLE"
    echo "✅ $PROD_SAMPLE 생성 완료!"
else
    echo "❌ $PROD_REAL 파일이 없습니다."
fi

echo "\n 모든 3종 샘플 파일 변환 작업이 끝났습니다."