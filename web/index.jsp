<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String context = request.getContextPath(); %>
<html>
<head>
    <title>Moon Pay</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.min.css"/>
</head>
<body>
<header class="mui-bar mui-bar-nav">
    <h1 class="mui-title">欢迎访问</h1>
</header>

<div class="mui-content">
    <ul class="mui-table-view mui-grid-view mui-grid-9">
        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
            <a href="/moon/cashier/toPay.htm">
                <span class="mui-icon mui-icon-pengyouquan"></span>
                <div class="mui-media-body">收银台</div>
            </a>
        </li>
        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
            <a href="/moon/cashier/toTransfer.htm">
                <span class="mui-icon mui-icon-pengyouquan"></span>
                <div class="mui-media-body">企业付款</div>
            </a>
        </li>
        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
            <a href="/moon/cashier/toRefund.htm">
                <span class="mui-icon mui-icon-pengyouquan"></span>
                <div class="mui-media-body">退款</div>
            </a>
        </li>
    </ul>
</div>
</body>
</html>
