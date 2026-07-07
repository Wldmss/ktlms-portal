<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- global snackbar --%>
<style>
    .snackbar-container {
        position: fixed;
        bottom: -60px;
        left: 50%;
        transform: translateX(-50%);
        background-color: #333333;
        color: #ffffff;
        padding: 14px 28px;
        border-radius: 30px;
        font-size: 14px;
        font-weight: 400;
        z-index: 9999999;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        display: flex;
        align-items: center;
        gap: 10px;
        transition: bottom 0.3s ease-out, opacity 0.3s ease-out;
        opacity: 0;
        pointer-events: none;
    }

    .snackbar-container.show {
        bottom: 40px;
        opacity: 1;
    }

    .snackbar-badge {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background-color: var(--primary-color);
        display: inline-block;
    }
</style>

<div id="global-snackbar" class="snackbar-container">
    <span class="snackbar-badge"></span>
    <span id="global-snackbar-text"></span>
</div>
