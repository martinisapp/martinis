<nav>
    <ul>
        <li><strong><a href="${pageContext.request.contextPath}">Martinis</a></strong></li>
    </ul>
    <ul>
        <li><a href="${pageContext.request.contextPath}/" class="${param.page == 'index' ? 'active' : ''}">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/project/list" class="${param.page == 'projects' ? 'active' : ''}">Projects</a></li>
        <li><a href="${pageContext.request.contextPath}/actor/list" class="${param.page == 'casting' ? 'active' : ''}">Casting</a></li>
    </ul>
</nav>