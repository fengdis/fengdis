// 消息提示
function MessgerBox(content) {
	if (confirm(content)) {
		return true;
	} else {
		return false;
	}
}
// 是否有选项
function IsCheckReturnCount() {
	var chkother = document.getElementsByTagName("input");
	var j = 0;

	for (var i = 0; i < chkother.length; i++) {
		if (chkother[i].type == 'checkbox'
				&& chkother[i].id.indexOf('chk_row') >= 0) {
			if (chkother[i].checked == true) {
				j++;
			}
		}
	}
	return j;
}

// 全选
function CheckAll() {
	var chkall = document.getElementById("chk_selall");
	var chkother = document.getElementsByTagName("input");

	for (var i = 0; i < chkother.length; i++) {
		if (chkother[i].type == 'checkbox'
				&& chkother[i].id.indexOf('chk_row') >= 0) {
			if (chkall.checked == true) {
				chkother[i].checked = true;
			} else {
				chkother[i].checked = false;
			}
		}
	}
}
// 不全选
function uncheckAll() {
	var chkall = document.getElementById("chk_selall");
	var chkother = document.getElementsByTagName("input");
	var j = 0;

	for (var i = 0; i < chkother.length; i++) {
		if (chkother[i].type == 'checkbox'
				&& chkother[i].id.indexOf('chk_row') >= 0) {
			if (chkother[i].checked == false) {
				j = 1;
			}
		}
	}
	if (j == 0) {
		chkall.checked = true;
	} else {
		chkall.checked = false;
	}
}
// 字数
function ShowWordsCount(obj) {
	return document.getElementById(obj).value.length;
}