<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-edit">
    <form hx-post="${pageContext.request.contextPath}/block/updateInline"
          hx-target="closest .block-column-content"
          hx-swap="innerHTML"
          hx-trigger="submit">
        <input type="hidden" name="id" value="${block.id}">
        <input type="hidden" name="sceneId" value="${block.scene.id}">
        <div class="form-group">
            <label>Character:</label>
            <select class="form-control edit-person-select" name="personId">
                <option value="">-- No Character --</option>
                <c:forEach items="${persons}" var="person">
                    <option value="${person.id}" ${block.person != null && person.id == block.person.id ? 'selected' : ''}>${person.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Content:</label>
            <textarea class="form-control" name="content" rows="8"
                      hx-post="${pageContext.request.contextPath}/block/updateInline"
                      hx-include="closest form"
                      hx-target="closest .block-column-content"
                      hx-swap="innerHTML"
                      hx-trigger="keyup changed delay:1500ms, blur">${block.content}</textarea>
        </div>
        <div class="form-group">
            <div class="save-status">
                <span class="text-muted htmx-indicator">Saving...</span>
            </div>
            <div class="text-muted small">
                <i>Click outside or press Escape to finish editing</i>
            </div>
        </div>
    </form>
</div>
