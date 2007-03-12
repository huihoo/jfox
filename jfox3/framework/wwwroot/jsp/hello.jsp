<html>
<body>
<h1>The Famous JSP Hello Program</h1>
<% {
    String s = "Jasper"; %>
The following line should contain the text "Hello Jasper World!".
<br>If thats not the case start debugging ...
<p>
    Hello <%= s %> World!
    <%}%>
</body>
</html>
 
