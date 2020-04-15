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
<HEAD>
<TITLE></TITLE>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<STYLE type=text/css> 
{
	FONT-SIZE: 12px
}
.gridView {
	BORDER-RIGHT: #bad6ec 1px; BORDER-TOP: #bad6ec 1px; BORDER-LEFT: #bad6ec 1px; COLOR: #566984; BORDER-BOTTOM: #bad6ec 1px; FONT-FAMILY: Courier New
}
.gridViewHeader {
	BORDER-RIGHT: #bad6ec 1px solid; BORDER-TOP: #bad6ec 1px solid; BACKGROUND-IMAGE: url(images/bg_th.gif); BORDER-LEFT: #bad6ec 1px solid; LINE-HEIGHT: 27px; BORDER-BOTTOM: #bad6ec 1px solid
}
.gridViewItem {
	BORDER-RIGHT: #bad6ec 1px solid; BORDER-TOP: #bad6ec 1px solid; BORDER-LEFT: #bad6ec 1px solid; LINE-HEIGHT: 32px; BORDER-BOTTOM: #bad6ec 1px solid; TEXT-ALIGN: center
}
.cmdField {
	BORDER-RIGHT: 0px; BORDER-TOP: 0px; BACKGROUND-IMAGE: url(images/bg_rectbtn.png); OVERFLOW: hidden; BORDER-LEFT: 0px; WIDTH: 67px; COLOR: #364c6d; LINE-HEIGHT: 27px; BORDER-BOTTOM: 0px; BACKGROUND-REPEAT: repeat-x; HEIGHT: 27px; BACKGROUND-COLOR: transparent; TEXT-DECORATION: none
}
.buttonBlue {
	BORDER-RIGHT: 0px; BORDER-TOP: 0px; BACKGROUND-IMAGE: url(images/bg_button_blue.gif); BORDER-LEFT: 0px; WIDTH: 78px; COLOR: white; BORDER-BOTTOM: 0px; BACKGROUND-REPEAT: no-repeat; HEIGHT: 21px
}
</STYLE>
<META content="MSHTML 6.00.2900.5848" name=GENERATOR>

<script language="javascript" type="text/javascript">

function checkform()
{
	 
	

	if (document.getElementById('baogaoid').value=="")
	{
		alert("体检报告不能为空");
		return false;
	}


	
	

	return true;
	
}


</script>
<script language="javascript" type="text/javascript" src="js/showdate.js"></script>
</HEAD>
<BODY style="BACKGROUND-POSITION-Y: -120px; BACKGROUND-IMAGE: url(images/bg.gif); BACKGROUND-REPEAT: repeat-x">





<DIV>
  <TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%" border=0>
    
    <TBODY>
      <TR 
  style="BACKGROUND-IMAGE: url(images/bg_header.gif); BACKGROUND-REPEAT: repeat-x" 
  height=47>
  
        <TD width=10>
        <SPAN style="FLOAT: left; BACKGROUND-IMAGE: url(images/main_hl.gif); WIDTH: 15px; BACKGROUND-REPEAT: no-repeat; HEIGHT: 47px"></SPAN></TD>
        <TD>
        <SPAN style="FLOAT: left; BACKGROUND-IMAGE: url(images/main_hl2.gif); WIDTH: 15px; BACKGROUND-REPEAT: no-repeat; HEIGHT: 47px"></SPAN>
        <SPAN style="PADDING-RIGHT: 10px; PADDING-LEFT: 10px; FLOAT: left; BACKGROUND-IMAGE: url(images/main_hb.gif); PADDING-BOTTOM: 10px; COLOR: white; PADDING-TOP: 10px; BACKGROUND-REPEAT: repeat-x; HEIGHT: 47px; TEXT-ALIGN: center; 0px: ">
        ${title } </SPAN>
        <SPAN 
      style="FLOAT: left; BACKGROUND-IMAGE: url(images/main_hr.gif); WIDTH: 60px; BACKGROUND-REPEAT: no-repeat; HEIGHT: 47px"></SPAN></TD>
        <TD 
    style="BACKGROUND-POSITION: 50% bottom; BACKGROUND-IMAGE: url(images/main_rc.gif)" 
    width=10></TD>
      </TR>
      <TR>
        <TD style="BACKGROUND-IMAGE: url(images/main_ls.gif)">&nbsp;</TD>
        <TD 
    style="PADDING-RIGHT: 10px; PADDING-LEFT: 10px; PADDING-BOTTOM: 10px; COLOR: #566984; PADDING-TOP: 10px; BACKGROUND-COLOR: white" 
    vAlign=top align=middle>
          <DIV>
            
            
            <form action="${url }" method="post" onsubmit="return checkform()">
            <TABLE class=gridView id=ctl00_ContentPlaceHolder2_GridView1 
      style="WIDTH: 80%; BORDER-COLLAPSE: collapse" cellSpacing=0 rules=all 
      border=1>
                <TBODY>
                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">体检用户名字</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.mingzi }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">身份证</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.sfz }

                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">预约体检日期</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.tuanti.riqi }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">正式体检日期</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.tuanti.tijian }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">体检套餐	</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.tuanti.taocan.mingzi }

                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">套餐费用	</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.tuanti.taocan.feiyong }

                    </TH>

                </TR>



                <tr>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="4">体检指标	</TH>

                </tr>
                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col>身高	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.shengao }
                    </TH>
                    <TH class=gridViewHeader scope=col>体重	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.tizhong }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col>肝功	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.gangong }
                    </TH>
                    <TH class=gridViewHeader scope=col>血糖	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.xuetang }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col>血脂	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.xuezhi }
                    </TH>
                    <TH class=gridViewHeader scope=col>B超	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.bchao }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col>血尿常规	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.xueniaochanggui }
                    </TH>
                    <TH class=gridViewHeader scope=col>胸透	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.xiongtou }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col>心电图	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.xindiantu }
                    </TH>
                    <TH class=gridViewHeader scope=col>内科	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.neike }
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col>外科	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.waike }
                    </TH>
                    <TH class=gridViewHeader scope=col>耳鼻喉科	</TH>
                    <TH class=gridViewHeader scope=col>
                        ${bean.erbihouke }
                    </TH>

                </TR>





                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">体检报告总结</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.baogao }
                        <%--<textarea rows="7" cols="50" name="baogao" id="baogaoid" readonly="readonly">${bean.baogao }</textarea>--%>
                    </TH>

                </TR>


                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">医生建议</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        ${bean.jianyi }
                        <%--<textarea rows="7" cols="50"  readonly="readonly">${bean.jianyi }</textarea>--%>
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">体检报告附件</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        <a href="<%=basePath %>uploadfile/${bean.path }">点击下载体检报告附件</a>
                    </TH>

                </TR>

                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">报告信息表打印</TH>
                    <TH class=gridViewHeader scope=col colspan="2">
                        <a href="" onclick=javascript:window.print()>点击打印报告表</a>
                    </TH>

                </TR>



                <TR>
                    <TH class=gridViewHeader style="WIDTH: 50px" scope=col>&nbsp;</TH>
                    <TH class=gridViewHeader scope=col colspan="2">操作</TH>
                    <TH class=gridViewHeader scope=col colspan="2" align="center" >

                        <input  onclick="javascript:history.go(-1);" style="width: 60px" type="button" value="返回" />
                    </TH>


                </TR>



                </TBODY>
            </TABLE>
            </form>
            
          </DIV>
        </TD>
        <TD style="BACKGROUND-IMAGE: url(images/main_rs.gif)"></TD>
      </TR>
      <TR 
  style="BACKGROUND-IMAGE: url(images/main_fs.gif); BACKGROUND-REPEAT: repeat-x" 
  height=10>
        <TD style="BACKGROUND-IMAGE: url(images/main_lf.gif)"></TD>
        <TD style="BACKGROUND-IMAGE: url(images/main_fs.gif)"></TD>
        <TD 
style="BACKGROUND-IMAGE: url(images/main_rf.gif)"></TD>
      </TR>
    </TBODY>
  </TABLE>
</DIV>

</BODY>
</HTML>



