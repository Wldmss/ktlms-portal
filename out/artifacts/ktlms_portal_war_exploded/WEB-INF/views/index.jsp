<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KTLMS Portal - Migration Success</title>
    
    <!-- Google Fonts (Outfit & Inter) -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Outfit:wght@400;600;800&display=swap" rel="stylesheet">
    
    <!-- External Webapp Stylesheets & Scripts -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.min.css">
    <script src="${pageContext.request.contextPath}/resources/js/main.js" defer></script>
</head>
<body>

    <div class="ambient-glow-1"></div>
    <div class="ambient-glow-2"></div>

    <header>
        <div class="logo">
            KTLMS Portal <span class="logo-tag">Migrated</span>
        </div>
        <ul class="nav-links">
            <li><a href="#">Dashboard</a></li>
            <li><a href="https://wildfly.org" target="_blank">WildFly 27 Docs</a></li>
            <li><a href="https://spring.io" target="_blank">Spring Docs</a></li>
        </ul>
    </header>

    <main>
        <section class="hero-section">
            <h1>Spring & Maven Migration Success</h1>
            <p>레거시 프로젝트(Eclipse/Ant)의 Maven 전환 및 Spring 6.x 버전업 빌드가 성공적으로 완료되었습니다. JBoss WildFly 27.0.1 WAS 환경에서 안전하고 쾌적하게 동작 중입니다.</p>
        </section>

        <section class="grid-container">
            <!-- Card 1 -->
            <div class="card">
                <div class="card-icon">🏗️</div>
                <div class="card-title">Maven WAR Packaging</div>
                <div class="card-desc">기존 Ant 빌드 환경에서 표준 Maven 디렉토리 구조로 성공적으로 재구조화되었습니다. 패키지 의존성 및 플러그인은 pom.xml에서 중앙 집중 관리됩니다.</div>
            </div>

            <!-- Card 2 -->
            <div class="card">
                <div class="card-icon">🛡️</div>
                <div class="card-title">Spring 6.1.21 & MVC</div>
                <div class="card-desc">최신 Spring Framework 6.1.x 기반의 MVC 구조가 확립되었습니다. Controller 스캔 및 JSP ViewResolver 설정이 완료되어 비즈니스 코드 마이그레이션 준비가 완료되었습니다.</div>
            </div>

            <!-- Card 3 -->
            <div class="card">
                <div class="card-icon">🚀</div>
                <div class="card-title">WildFly 27.0.1 Ready</div>
                <div class="card-desc">최신 Jakarta EE 10 스택 기반의 JBoss WildFly 27.0.1.Final에 적합한 서블릿 사양(web.xml 6.0) 및 jakarta 네임스페이스 환경이 갖추어졌습니다.</div>
            </div>

            <!-- Dashboard Card -->
            <div class="card status-card">
                <div class="dashboard-header">
                    <div class="dashboard-title">
                        <span class="status-pulse"></span> Runtime Environment Information
                    </div>
                </div>
                <div class="stats-grid">
                    <div class="stat-box">
                        <div class="stat-label">WAS Engine</div>
                        <div class="stat-value">WildFly 27.0.1</div>
                        <div class="stat-badge badge-success">Jakarta EE 10</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-label">Java Version</div>
                        <div class="stat-value">${javaVersion}</div>
                        <div class="stat-badge badge-success">Java 21 Active</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-label">Spring Version</div>
                        <div class="stat-value">${springVersion}</div>
                        <div class="stat-badge badge-info">Spring MVC 6</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-label">System Time</div>
                        <div class="stat-value">${serverTime}</div>
                        <div class="stat-badge badge-info">Controller Rendered</div>
                    </div>
                </div>
            </div>
        </section>

        <section class="cta-section">
            <button class="btn btn-primary" onclick="showMigrationInfo()">마이그레이션 다음 안내</button>
            <a href="https://maven.apache.org" target="_blank" class="btn btn-secondary">Maven 정보 더보기</a>
        </section>
    </main>

    <footer>
        &copy; 2026 KTLMS Portal Migration Project. Built with Antigravity AI.
    </footer>

</body>
</html>
