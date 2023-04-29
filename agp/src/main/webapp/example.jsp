<html>
<HEAD>
    <TITLE>AJAX with JSON resource</TITLE>
</HEAD>
<BODY>
<input type="hidden" name="csrftoken" value="${csrfToken}">
<script type="text/javascript">
    var ajaxUrl = "${ajaxUrl}"
</script>

<script type="text/javascript" src="${pageContext.request.contextPath}/example.js"></script>
<div>Check Javascript console after pressing buttons</div>
<button onclick="json()">Send and receive JSON</button>
<button onclick="redirect()">Send JSON and receive redirect URL to new jsp</button>
<button onclick="file()">Send JSON and receive a file</button>
</BODY>
</HTML>