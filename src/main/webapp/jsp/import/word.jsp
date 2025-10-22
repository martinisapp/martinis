<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Import Word Document</title>
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
                <h1>Import Word Document</h1>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    ${error}
                </div>
            </c:if>

            <div class="panel panel-default">
                <div class="panel-body">
                    <p>Import a screenplay from a Word document (.docx format).</p>
                    <p><strong>Formatting Guidelines:</strong></p>
                    <ul>
                        <li>Scene headings should be in ALL CAPS (e.g., "INT. HOUSE - DAY")</li>
                        <li>Character names should be in ALL CAPS before their dialogue</li>
                        <li>Action and dialogue should be in regular text</li>
                        <li>Only .docx files are supported (not .doc)</li>
                    </ul>
                </div>
            </div>

            <form  action="${pageContext.request.contextPath}/import/word" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="projectTitle" >Project Title:</label>
                    
                        <input type="text" "" id="projectTitle" name="projectTitle" required placeholder="Enter a title for your project" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="file" >Word File:</label>
                    
                        <input type="file" "" id="file" name="file" accept=".docx" required />
                        <p class="help-block">Select a .docx file (max 50MB)</p>
                    </div>
                </div>
                <div class="form-group">
                    
                        <a href="${pageContext.request.contextPath}/project/list" role="button" class="secondary" role="button">Cancel</a>
                        <button type="submit" role="button">Import</button>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
