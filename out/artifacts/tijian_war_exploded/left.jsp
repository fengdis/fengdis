<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
#menuTree A {
	COLOR: #566984; TEXT-DECORATION: none
}
</STYLE>
<SCRIPT src="images/Left.files/TreeNode.js" type=text/javascript></SCRIPT>
<SCRIPT src="images/Left.files/Tree.js" type=text/javascript></SCRIPT>
<META content="MSHTML 6.00.2900.5848" name=GENERATOR>
</HEAD>
<BODY 
style="BACKGROUND-POSITION-Y: -120px; BACKGROUND-IMAGE: url(images/bg.gif); BACKGROUND-REPEAT: repeat-x">
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%">
  <TBODY>
    <TR>
      <TD width=10 height=29><IMG src="images/Left.files/bg_left_tl.gif"></TD>
      <TD 
    style="FONT-SIZE: 18px; BACKGROUND-IMAGE: url(images/bg_left_tc.gif); COLOR: white; FONT-FAMILY: system">主菜单</TD>
      <TD width=10><IMG src="images/Left.files/bg_left_tr.gif"></TD>
    </TR>
    <TR>
      <TD style="BACKGROUND-IMAGE: url(images/bg_left_ls.gif)"></TD>
      <TD id=menuTree 
    style="PADDING-RIGHT: 10px; PADDING-LEFT: 10px; PADDING-BOTTOM: 10px; PADDING-TOP: 10px; HEIGHT: 100%; BACKGROUND-COLOR: white" 
    vAlign=top></TD>
      <TD style="BACKGROUND-IMAGE: url(images/bg_left_rs.gif)"></TD>
    </TR>
    <TR>
      <TD width=10><IMG src="images/Left.files/bg_left_bl.gif"></TD>
      <TD style="BACKGROUND-IMAGE: url(images/bg_left_bc.gif)"></TD>
      <TD width=10><IMG 
src="images/Left.files/bg_left_br.gif"></TD>
    </TR>
  </TBODY>
</TABLE>

<c:if  test="${user.role==1}">
<SCRIPT type=text/javascript>
var tree = null;

var root = new TreeNode('系统菜单');

var fun1 = new TreeNode('医护人员管理');
var fun2 = new TreeNode('医护人员列表', 'method!userlist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun1.add(fun2);
root.add(fun1);

tree = new Tree(root);tree.show('menuTree');

</SCRIPT>
</c:if>



<c:if  test="${user.role==2}">
<SCRIPT type=text/javascript>
var tree = null;

var root = new TreeNode('系统菜单');

var fun1 = new TreeNode('体检套餐管理');
var fun2 = new TreeNode('体检套餐列表', 'method!taocanlist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun1.add(fun2);
root.add(fun1);

var fun3 = new TreeNode('单人体检管理');

var fun4 = new TreeNode('预约体检管理', 'method!jilulist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun4);

var fun5 = new TreeNode('取消预约管理', 'method!jilulist2.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun5);

var fun6 = new TreeNode('正式登记管理', 'method!jilulist3.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun6);

var fun7 = new TreeNode('缴费管理', 'method!jilulist4.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun7);

var fun7 = new TreeNode('完成体检管理', 'method!jilulist5.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun7);

var fun8 = new TreeNode('体检报告管理', 'method!jilulist6.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun8);

root.add(fun3);


var fun9 = new TreeNode('团体体检管理');

var fun10 = new TreeNode('预约体检管理', 'method!tuantilist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun9.add(fun10);
var fun11 = new TreeNode('取消预约管理', 'method!tuantilist2.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun9.add(fun11);
var fun12 = new TreeNode('正式登记管理', 'method!tuantilist3.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun9.add(fun12);

var fun13 = new TreeNode('缴费管理', 'method!tuantilist4.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun9.add(fun13);

var fun14 = new TreeNode('完成体检管理', 'method!tuantilist5.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun9.add(fun14);

var fun15 = new TreeNode('体检报告管理', 'method!tuantilist6.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun9.add(fun15);

root.add(fun9);


var fun21 = new TreeNode('咨询台');
var fun22 = new TreeNode('咨询台', 'method!zixunlist2.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun21.add(fun22);

root.add(fun21);



var fun23 = new TreeNode('健康库');
var fun24 = new TreeNode('健康库', 'method!jiankanglist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun23.add(fun24);

root.add(fun23);


tree = new Tree(root);tree.show('menuTree');

</SCRIPT>
</c:if>


<c:if  test="${user.role==3}">
<c:if  test="${user.leixing=='个人'}">
<SCRIPT type=text/javascript>
var tree = null;

var root = new TreeNode('系统菜单');


var fun1 = new TreeNode('体检预约管理');
var fun2 = new TreeNode('体检预约管理', 'method!jilulist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun1.add(fun2);

root.add(fun1);

var fun3 = new TreeNode('体检缴费管理');
var fun4 = new TreeNode('体检缴费管理', 'method!jilulist4.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun4);

root.add(fun3);

var fun5 = new TreeNode('体检报告管理');
var fun6 = new TreeNode('体检报告管理', 'method!jilulist6.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun5.add(fun6);

root.add(fun5);


var fun7 = new TreeNode('咨询台');
var fun8 = new TreeNode('咨询台', 'method!zixunlist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun7.add(fun8);

root.add(fun7);

tree = new Tree(root);tree.show('menuTree');

</SCRIPT>
</c:if>
</c:if>




<c:if  test="${user.role==3}">
<c:if  test="${user.leixing=='团体'}">
<SCRIPT type=text/javascript>
var tree = null;

var root = new TreeNode('系统菜单');


var fun1 = new TreeNode('体检预约管理');
var fun2 = new TreeNode('体检预约管理', 'method!tuantilist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun1.add(fun2);

root.add(fun1);

var fun3 = new TreeNode('体检缴费管理');
var fun4 = new TreeNode('体检缴费管理', 'method!tuantilist4.action?sfz=${user.sfz}', 'tree_node.gif', null, 'tree_node.gif', null);
fun3.add(fun4);

root.add(fun3);

var fun5 = new TreeNode('体检报告管理');
var fun6 = new TreeNode('体检报告管理', 'method!tuantilist7.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun5.add(fun6);

root.add(fun5);


var fun7 = new TreeNode('咨询台');
var fun8 = new TreeNode('咨询台', 'method!zixunlist.action', 'tree_node.gif', null, 'tree_node.gif', null);
fun7.add(fun8);

root.add(fun7);

tree = new Tree(root);tree.show('menuTree');

</SCRIPT>
</c:if>
</c:if>

</BODY>
</HTML>



