<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Edit Character</title>
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
                <h1>Edit Character</h1>
            </div>
            <sf:form  action="${pageContext.request.contextPath}/character/edit" method="post" modelAttribute="commandModel">
                <sf:hidden path="id" />
                <sf:hidden path="projectId" />
                <div class="form-group">
                    <label for="name" >Name:</label>
                    
                        <sf:input type="text" "" path="name" />
                        <sf:errors path="name" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="fullName" >Full Name:</label>
                    
                        <sf:input type="text" "" path="fullName" />
                        <sf:errors path="fullName" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="actorId" >Actor:</label>
                    
                        <sf:select "" path="actorId">
                            <sf:option value="" label="No actor" />
                            <sf:options items="${viewModel.actors}" itemValue="id" itemLabel="name" />
                        </sf:select>
                        <sf:errors path="actorId" />
                    </div>
                </div>
                <div class="form-group">
                    
                        <a href="${pageContext.request.contextPath}/character/show?id=${viewModel.id}" role="button" class="secondary" role="button">Cancel</a>
                        <button type="submit" role="button">Submit</button>
                    </div>
                </div>
            </sf:form>
        </div>
    </body>
</html>
