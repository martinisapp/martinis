<nav class="navbar navbar-inverse navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="${pageContext.request.contextPath}">Martinis</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="${param.page == 'index' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/">Home</a>
            </li>
            <li class="${param.page == 'projects' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/project/list">Projects</a>
            </li>
            <li class="${param.page == 'casting' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/actor/list">Casting</a>
            </li>
        </ul>
    </div>
</nav>