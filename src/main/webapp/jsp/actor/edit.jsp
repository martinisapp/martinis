<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Edit Actor</title>
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
                <h1>Edit Actor</h1>
            </div>
            <sf:form  action="${pageContext.request.contextPath}/actor/edit" method="post" modelAttribute="commandModel">
                <sf:hidden path="id" />
                <div class="form-group">
                    <label for="first" >First Name:</label>
                    
                        <sf:input type="text" "" path="first" />
                        <sf:errors path="first" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="last" >Last Name:</label>
                    
                        <sf:input type="text" "" path="last" />
                        <sf:errors path="last" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="phone" >Phone:</label>
                    
                        <sf:input type="text" "" path="phone" />
                        <sf:errors path="phone" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="email" >Email:</label>
                    
                        <sf:input type="text" "" path="email" />
                        <sf:errors path="email" />
                    </div>
                </div>
                <div class="form-group">
                    
                        <a href="${pageContext.request.contextPath}/actor/show?id=${viewModel.id}" role="button" class="secondary" role="button">Cancel</a>
                        <button type="submit" role="button">Submit</button>
                    </div>
                </div>
            </sf:form>
        </div>
    </body>
</html>
