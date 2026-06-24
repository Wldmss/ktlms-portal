<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<h3>공통 컴포넌트</h3>
<div class="ui-container group-box">
    <div class="ui-section">
        <div class="ui-title">1. Input Text Box</div>
        <div style="margin-bottom: 10px;">
            <input type="text" class="input-text" placeholder="기본 텍스트를 입력하세요.">
        </div>
        <div>
            <input type="text" class="input-text" value="비활성화된 텍스트 박스" disabled>
        </div>
    </div>

    <div class="ui-section">
        <div class="ui-title">2. Textarea (장문 입력창)</div>
        <textarea class="textarea" placeholder="여기에 내용을 입력하세요."></textarea>
    </div>

    <div class="ui-section">
        <div class="ui-title">3. Buttons (상태별 라인업)</div>
        <div class="btn-group-row">
            <button type="button" class="btn btn-filled">확인 (OK / Filled)</button>
            <button type="button" class="btn btn-outline">아웃라인 (Outline)</button>
            <button type="button" class="btn btn-cancel">취소 (Cancel)</button>
            <button type="button" class="btn btn-filled" disabled>비활성 (Disabled)</button>
        </div>
    </div>

    <div class="ui-section">
        <div class="ui-title">4. Checkbox & Radio</div>
        <div style="margin-bottom: 10px;">
            <label class="control-label">
                <input type="checkbox" name="chk_test" checked>
                <span class="checkbox-box"></span>
                동의함 (체크됨)
            </label>
            <label class="control-label">
                <input type="checkbox" name="chk_test">
                <span class="checkbox-box"></span>
                동의 안 함
            </label>
        </div>
        <div>
            <label class="control-label">
                <input type="radio" name="radio_test" value="Y" checked>
                <span class="radio-circle"></span>
                선택 A
            </label>
            <label class="control-label">
                <input type="radio" name="radio_test" value="N">
                <span class="radio-circle"></span>
                선택 B
            </label>
        </div>
    </div>

    <div class="ui-section">
        <div class="ui-title">5. Select Box</div>
        <select class="custom-select" style="width: 200px;">
            <option value="">선택하세요</option>
            <option value="1">옵션 항목 01</option>
            <option value="2">옵션 항목 02</option>
        </select>
    </div>

</div>