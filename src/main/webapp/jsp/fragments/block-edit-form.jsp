<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-edit">
    <form hx-post="${pageContext.request.contextPath}/block/updateInline"
          hx-target="closest .block-column-content"
          hx-swap="innerHTML"
          hx-trigger="submit">
        <input type="hidden" name="id" value="${block.id}">
        <input type="hidden" name="sceneId" value="${block.scene.id}">
        <div >
            <label>Character:</label>
            <select class="form-control edit-person-select" name="personId"
                    hx-post="${pageContext.request.contextPath}/block/updateInline"
                    hx-include="closest form"
                    hx-target="closest .block-column-content"
                    hx-swap="innerHTML"
                    hx-trigger="change">
                <option value="">-- No Character --</option>
                <c:forEach items="${persons}" var="person">
                    <option value="${person.id}" ${block.person != null && person.id == block.person.id ? 'selected' : ''}>${person.name}</option>
                </c:forEach>
            </select>
        </main>
        <div >
            <label>Content:</label>
            <textarea  name="content" rows="8"
                      hx-post="${pageContext.request.contextPath}/block/updateInline"
                      hx-include="closest form"
                      hx-target="closest .block-column-content"
                      hx-swap="innerHTML"
                      hx-trigger="keyup changed delay:1500ms, blur">${block.content}</textarea>
        </main>
        <div >
            <div class="save-status">
                <span class="text-muted htmx-indicator">Saving...</span>
            </div>
            <div style="opacity: 0.7;">
                <i>Click outside or press Escape to finish editing</i>
            </div>
        </main>
    </form>
</div>
