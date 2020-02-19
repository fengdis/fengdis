function initTest() {
    $.ajax({
        type : "GET",
        async : false,
        url : '/api/sysUser/4028aa9d6502a5b6016502a5c3c20001',
        success : function(data) {
            data = data;
            console.log(data);
        }
    });
}