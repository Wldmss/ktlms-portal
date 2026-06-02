/**
 * KTLMS Portal Migration - Core JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    console.log('%c🚀 KTLMS Portal Migration - Initialized Successfully!', 'color: #6366f1; font-size: 16px; font-weight: bold;');
    console.log('%cEnvironment: JBoss WildFly 27.0.1.Final | Spring Framework 6.1.21 | Java 21', 'color: #10b981; font-size: 12px;');

    // Dynamic glow element mouse tracking (optional ambient micro-interactivity)
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('mousemove', (e) => {
            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            card.style.setProperty('--mouse-x', `${x}px`);
            card.style.setProperty('--mouse-y', `${y}px`);
        });
    });
});

/**
 * Handle CTA Migration Next Info click
 */
function showMigrationInfo() {
    alert(
        "🎉 스프링 뼈대 및 모던 리소스 구조 구축이 완료되었습니다!\n\n" +
        "다음 단계 가이드:\n" +
        "1. src/main/java 밑에 기존 Controller, Service, DAO 소스 코드를 이식해 보세요.\n" +
        "2. 와일드플라이 27을 사용하는 경우 javax.servlet 임포트를 jakarta.servlet으로 일괄 교체해야 합니다.\n" +
        "3. webapp/resources/css 및 js 폴더에 스타일과 스크립트를 추가하여 화면을 개발할 수 있습니다."
    );
}
