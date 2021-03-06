<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page session="false" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>JFox3 manager Module
    </title>
    <style type="text/css">
        /*<![CDATA[*/
        body {
            color: #000000;
            background-color: #FFFFFF;
            font-family: Arial, "Times New Roman", Times, serif;
            margin: 10px 0;
        }

        img {
            border: none;
        }

        a:link, a:visited {
            color: blue
        }

        th {
            font-family: Verdana, "Times New Roman", Times, serif;
            font-size: 110%;
            font-weight: normal;
            font-style: italic;
            background: #D2A41C;
            text-align: left;
        }

        td {
            color: #000000;
            font-family: Arial, Helvetica, sans-serif;
        }

        td.menu {
            background: #FFDC75;
        }

        .center {
            text-align: center;
        }

        .code {
            color: #000000;
            font-family: "Courier New", Courier, monospace;
            font-size: 110%;
            margin-left: 2.5em;
        }

        #banner {
            margin-bottom: 12px;
        }

        p#congrats {
            margin-top: 0;
            font-weight: bold;
            text-align: center;
        }

        p#footer {
            text-align: right;
            font-size: 80%;
        }

        /*]]>*/
    </style>
</head>

<body>

<!-- Header -->
<table id="banner" width="100%">
    <tr>
        <td align="left">
            <a href="http://code.google.com/p/jfox">
                <img src="images/jfox.jpg" alt="JFox Java EE Application Server!"/>
            </a>
        </td>
    </tr>
</table>

<table>
<tr>

<!-- Table of Contents -->
<td valign="top" width ="200">
    <table width="100%" border="1" cellspacing="0" cellpadding="3">
        <tr>
            <th>Manager</th>
        </tr>
        <tr>
            <td class="menu">
                <a href="console.sysinfo.do" title="view status">Web Console</a><br/>
                &nbsp;
            </td>
        </tr>
    </table>

    <br/>
    <table width="100%" border="1" cellspacing="0" cellpadding="3">
        <tr>
            <th>Examples</th>
        </tr>
        <tr>
            <td class="menu">
                <a href="demoAction.html">MVC Action Examples</a><br/>
                <a href="demoJSP.html">JSP Examples</a><br/>
                <a href="demoEJB.html">EJB Examples</a><br/>
                &nbsp;
            </td>
        </tr>
    </table>

</td>

<td style="width:20px">&nbsp;</td>

<!-- Body -->
<td align="left" valign="top">
    <p id="congrats">
        If you're seeing this page via a web browser, it means you've embedded JFox3 into Tomcat successfully. Congratulations!
    </p>

    <p>
        If you want to test whether jfox default module worked, you need to visit <a href="modules/manager/index.jsp">jfox manager module</a>!
    </p>

    <p>
        Also, if you have deploied jfox petstore module, its default url is: <a href="modules/petstore/page.index.do">jfox petstore</a> <br/>
        <b>NOTE:</b> if you want to run throughout petstore, you need do some configure, you can find jfox petstore installation help in document <a href="docs/installation.html">jfox installation guide</a>!
    </p>

    <p>
        JFox3 is designed to be a lightweight but robust Java EE Application Server support EJB3 & JPA, to ensure application based on Java EE/EJB can be developed rapidly and simply!<br/>
    </p>
    <p>
        JFox3 can be embedded in any Web Server to enable EJB3 component support, and JFox3 is a full application platform by providing MVC Framework!</br>
    </p>

    <p>
        You can visit jfox3 poject at google code, <a href="http://code.google.com/p/jfox">http://code.google.com/p/jfox</a>, <br/>
        and huihoo.org: <a href="http://www.huihoo.org/jfox">http://www.huihoo.org/jfox</a>.
    </p>
    <p>
        Thanks for using JFox3, any suggestion is welcomed!
    </p>
    <ul>
        <li><b><a href="mailto:jfox.peter@gmail.com">jfox.peter@gmail.com</a></b> for project management,collaboration!</li>
        <li><b><a href="mailto:jfox.young@gmail.com">jfox.young@gmail.com</a></b> for technology, architecture!</li>
    </ul>

    <p id="footer">
        &nbsp;
        Powered by JFox3!<br/>
        Copyright &copy; 2002-2007 JFox Open Source Java EE Project!<br/>
        All Rights Reserved, licensed under GNU LGPL!
    </p>
</td>

</tr>
</table>

</body>
</html>
