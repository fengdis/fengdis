<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="tijian.util.Util"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Util.init(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>体检预约管理系统</TITLE>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<LINK 
href="images/public.css" type=text/css rel=stylesheet>
<LINK 
href="images/login.css" type=text/css rel=stylesheet>
<STYLE type=text/css>
</STYLE>

  <META content="MSHTML 6.00.2900.5848" name=GENERATOR>

  <script type="text/javascript" src="js/jquery-3.3.1.min.js"></script>

</HEAD>
<BODY>
<DIV id=div1>


  <form action="method!login.action" method="post" id="loginForm">
  <TABLE id=login height="100%" cellSpacing=0 cellPadding=0 width=800 
align=center>
    <TBODY>
      <TR id=main>
        <TD>
          <TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%">
            <TBODY>
              <TR>
                <TD colSpan=4>&nbsp;</TD>
              </TR>
              <TR height=30>
                <TD width=380>&nbsp;</TD>
                <TD>&nbsp;</TD>
                <TD>&nbsp;<span style="font-size: 25px;color: white;">体检预约管理系统</span></TD>
                <TD>&nbsp;</TD>
              </TR>
              <TR height=40>
                <TD rowSpan=4>&nbsp;</TD>
                <TD>用户名：</TD>
                <TD>
                  <INPUT class=textbox id=txtUserName name=username >
                </TD>
                <TD width=120>&nbsp;</TD>
              </TR>
              <TR height=40>
                <TD>密　码：</TD>
                <TD>
                  <INPUT class=textbox id=txtUserPassword type=password 
            name= password >
                </TD>
                <TD width=120>&nbsp;</TD>
              </TR>
              <TR height=40>
                <TD>用户角色：</TD>
                <TD vAlign=center colSpan=2>
                 <select name="role">
                 	<option value="3">体检用户</option>
                	<option value="2">医护人员</option>
                	<option value="1">管理员</option>
              	</select>
                 </TD>
              </TR>
              
              
              <TR height=40>
                <TD></TD>
                <TD align=right>

                  <INPUT id=btnLogin type=submit value=" 登 录 " name=btnLogin style="float: left;margin-left: 100px">
                  <INPUT id=btnReg type=button value=" 注 册 " name=btnReg style="float: left">

                  <script>
                      $('select[name="role"]').change(function(){
                          if($("select[name='role'] option:selected").val() == 3){
                              $('#btnReg').css('display','block');
                          }else {
                              $('#btnReg').css('display','none');
                          }
                      })

                      $('#btnReg').click(function () {
                          $('#loginForm').css('display','none');
                          $('#regForm').css('display','block');
                      })
                  </script>
                </TD>
                <TD width=120>&nbsp;</TD>
              </TR>
              <TR height=110>
                <TD colSpan=4>&nbsp;</TD>
              </TR>
            </TBODY>
          </TABLE>
        </TD>
      </TR>
      <%--<TR id=root height=104>
        <TD>&nbsp;</TD>
      </TR>--%>
    </TBODY>
  </TABLE>
  </form>

  <form action="method!register.action" method="post" id="regForm" style="display: none;">
    <TABLE id=login height="100%" cellSpacing=0 cellPadding=0 width=800
           align=center>
      <TBODY>
      <TR id=main>
        <TD>
          <TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%">
            <TBODY>
            <TR>
              <TD colSpan=4>&nbsp;</TD>
            </TR>
            <TR height=30>
              <TD width=380>&nbsp;</TD>
              <TD>&nbsp;</TD>
              <TD>&nbsp;<span style="font-size: 25px;color: white;">体检预约管理系统</span></TD>
              <TD>&nbsp;</TD>
            </TR>
            <TR height=40>
              <TD rowSpan=5>&nbsp;</TD>
              <TD>用户名：</TD>
              <TD>
                <INPUT class=textbox id=username name=username >
              </TD>
              <TD width=120>&nbsp;</TD>
            </TR>
            <TR height=40>
              <TD>密　码：</TD>
              <TD>
                <INPUT class=textbox id=password type=password
                       name= password >
              </TD>
              <TD width=120>&nbsp;</TD>
            </TR>
            <TR height=40>
              <TD>身份证号：</TD>
              <TD vAlign=center colSpan=2>
                <INPUT class=textbox id=sfz type=text
                       name= sfz >
              </TD>
            </TR>
            <TR height=40>
              <TD>联系方式：</TD>
              <TD vAlign=center colSpan=2>
                <INPUT class=textbox id=lianxifangshi type=text
                       name= lianxifangshi >
              </TD>
            </TR>
            <TR height=40>
              <TD>真实姓名：</TD>
              <TD vAlign=center colSpan=2>
                <INPUT class=textbox id=truename type=text
                       name= truename >
              </TD>
            </TR>


            <TR height=40>
              <TD></TD>
              <TD align=right>

                <INPUT id=btnRegister type=submit value=" 注 册 " name=btnReg>

              </TD>
              <TD width=120>&nbsp;</TD>
            </TR>
            <TR height=110>
              <TD colSpan=4>&nbsp;</TD>
            </TR>
            </TBODY>
          </TABLE>
        </TD>
      </TR>
      <%--<TR id=root height=104>
        <TD>&nbsp;</TD>
      </TR>--%>
      </TBODY>
    </TABLE>
  </form>
</DIV>
<DIV id=div2 style="DISPLAY: none"></DIV>
</CONTENTTEMPLATE>
</BODY>
</HTML>

