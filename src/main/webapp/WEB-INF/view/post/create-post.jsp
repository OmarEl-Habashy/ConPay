<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Create New Post</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="container">
  <h1>Create New Post</h1>

  <c:if test="${not empty errorMessage}">
    <div class="error-message">
        ${errorMessage}
    </div>
  </c:if>

  <form action="${pageContext.request.contextPath}/create-post" method="post" enctype="multipart/form-data">
    <div class="form-group">
      <label for="content">What's on your mind?</label>
      <textarea id="content" name="content" rows="5" required>${content}</textarea>
    </div>

    <div class="form-group">
      <label for="image">Add an image (optional)</label>
      <input type="file" id="image" name="image" accept="image/*">
      <div class="file-preview" id="imagePreview"></div>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-primary">Post</button>
      <a href="${pageContext.request.contextPath}/feed" class="btn btn-secondary">Cancel</a>
    </div>
  </form>
</div>

<script>
  document.getElementById('image').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function(e) {
        const preview = document.getElementById('imagePreview');
        preview.innerHTML = `<img src="${e.target.result}" alt="Preview" style="max-width: 100%; max-height: 200px;">`;
      }
      reader.readAsDataURL(file);
    }
  });
</script>
</body>
</html>