<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../common/base.jsp"></jsp:include>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>评价</title>
</head>
<input type="radio" id="praise1" name="praise" value="1"/><label for="praise1"> 1星</label>
<input type="radio" id="praise2" name="praise" value="2"/> <label for="praise2"> 2星</label>
<input type="radio" id="praise3" name="praise" value="3" checked/> <label for="praise3"> 3星</label>
<input type="radio" id="praise4" name="praise" value="4"/> <label for="praise4"> 4星</label>
<input type="radio" id="praise5" name="praise" value="5"/> <label for="praise5"> 5星</label>
<input id="btnOK" type="button" value="提交评价"/>
</body>
</html>
<script type="text/javascript">

    $(function () {
        $("#btnOK").click(function () {
            $.ajax({
                type: "post",
                url: "<%=path%>/appraise/doPraise.htm",
                data: {pValue: $("input[name='praise']:checked").val()},
                dataType: "json",
                success: function (data) {
                    //显示返回值
                    alert(data);

                    //兼容关闭当前页面
                    var userAgent = navigator.userAgent;
                    if (userAgent.indexOf("Firefox") != -1 || userAgent.indexOf("Presto") != -1) {
                        window.location.replace("about:blank");
                    } else {
                        window.opener = null;
                        window.open("", "_self");
                        window.close();
                    }
                },
                error: function (data) {
                    //显示返回值
                    alert(data);
                }
            });
        });
    });
</script>