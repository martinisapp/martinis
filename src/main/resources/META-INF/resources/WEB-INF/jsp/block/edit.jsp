<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Edit Block</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <main class="container">
            <jsp:include page="../includes/logout.jsp" />
            <hgroup>
                <h1>Edit Block</h1>
            </hgroup>
            <sf:form action="${pageContext.request.contextPath}/block/edit" method="post" modelAttribute="commandModel">
                <sf:hidden path="id" />
                <sf:hidden path="sceneId" />
                <label for="content">
                    Content:
                    <sf:textarea spellcheck="true" rows="5" path="content" />
                    <sf:errors path="content" />
                </label>
                <label for="personId">
                    Character:
                    <sf:select path="personId">
                        <sf:option value="" label="No character" />
                        <sf:options items="${viewModel.persons}" itemValue="id" itemLabel="name" />
                    </sf:select>
                    <sf:errors path="personId" />
                </label>
                <div class="grid">
                    <a href="${pageContext.request.contextPath}/scene/show?id=${viewModel.sceneId}" role="button" class="secondary">Cancel</a>
                    <button type="submit">Submit</button>
                </div>
            </sf:form>
        </main>
    </body>
</html>
