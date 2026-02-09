<%@ page import="com.wincomplm.wex.kernel.api.invoke.WexInvoker"%>
<%
    String result = WexInvoker.invoke("com.wincomplm.wex-license-report", "license-report-api.get-license-data-json");
    out.write(result);
%>
