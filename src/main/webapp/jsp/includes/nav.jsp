<nav class="navbar navbar-inverse navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${pageContext.request.contextPath}">Martinis</a>
        </div>
        <div class="collapse navbar-collapse" id="navbar-collapse">
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
    </div>
</nav>