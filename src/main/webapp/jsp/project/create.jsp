<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Create New Project</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <main class="container">
            <jsp:include page="../includes/logout.jsp" />
            <div >
                <h1>Create New Project</h1>
            </div>
            <sf:form  action="${pageContext.request.contextPath}/project/create" method="post" modelAttribute="commandModel">
                <div >
                    <label for="title" >Title:</label>
                    <div >
                        <sf:input type="text"  spellcheck="true" path="title" />
                        <sf:errors path="title" />
                    </div>
                </div>
                <div >
                    <div >
                        <a href="${pageContext.request.contextPath}/project/list" role="button" class="secondary" role="button">Cancel</a>
                        <button type="submit" role="button">Submit</button>
                    </div>
                </div>
            </sf:form>
        </main>
    </body>
</html>
