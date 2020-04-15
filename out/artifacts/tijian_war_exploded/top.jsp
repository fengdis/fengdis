<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD id=Head1>
<TITLE></TITLE>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<SCRIPT src="images/Top.files/Clock.js" type=text/javascript></SCRIPT>
<STYLE type=text/css> 
*{
	FONT-SIZE: 12px; COLOR: white
}
#logo {
	COLOR: white
}
#logo A {
	COLOR: white
}
FORM {
	MARGIN: 0px
}
</STYLE>

<META content="MSHTML 6.00.2900.5848" name=GENERATOR>
</HEAD>
<BODY 
style="BACKGROUND-IMAGE: url(images/bg.gif); MARGIN: 0px; BACKGROUND-REPEAT: repeat-x">
<form id="form1">
  <DIV>
  <div align="center">
   <span style="font-size: 40px;color: white;font-weight: bold;">体检预约管理系统
   </span>
  </div>  
    <DIV style="DISPLAY: block; HEIGHT: 40px"></DIV>
    <DIV 
style="BACKGROUND-IMAGE: url(images/bg_nav.gif); BACKGROUND-REPEAT: repeat-x; HEIGHT: 30px">
      <TABLE cellSpacing=0 cellPadding=0 width="100%">
        <TBODY>
          <TR>
            <TD>
              <DIV><IMG src="images/Top.files/nav_pre.gif" align=absMiddle>
              欢迎<SPAN style="font-size: 18px;font-weight: bold;color: red;" >${user.truename }</SPAN>使用本系统
              
               </DIV>
            </TD>
            <TD align=right width="70%">
            <SPAN style="PADDING-RIGHT: 50px">
           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <A 
      href="method!loginout.action" target=_top><IMG 
      src="images/Top.files/nav_changePassword.gif" align=absMiddle border=0><SPAN style="font-size: 18px;font-weight: bold;" >退出系统</SPAN></A>
      <A href="method!changepwd.action" 
      target=mainFrame><IMG src="images/Top.files/nav_resetPassword.gif" 
      align=absMiddle border=0><SPAN style="font-size: 18px;font-weight: bold;" >修改密码</SPAN></A> <IMG 
      src="images/Top.files/menu_seprator.gif" align=absMiddle> 
      <SPAN id="clock" style="font-size: 18px;font-weight: bold;" ></SPAN></SPAN></TD>
          </TR>
        </TBODY>
      </TABLE>
    </DIV>
  </DIV>
  <SCRIPT type=text/javascript>
    var clock = new Clock();
    clock.display(document.getElementById("clock"));
</SCRIPT>
</form>
</BODY>
</HTML>


