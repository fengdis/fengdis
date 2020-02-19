<!DOCTYPE html>
<#assign base=request.contextPath />

<#--<#assign base="http://localhost:8081">-->
<html lang="en">
<head>
    <#--<base id="base" href="${base}">-->
    <meta charset="UTF-8">
    <title>Title</title>
    <script type="text/javascript"
            src="/js/common/jquery.min.js"></script>
    <script type="text/javascript"
            src="/js/test.js"></script>
        <script type="text/javascript" src="/wangEditor-3.1.1/release/wangEditor.min.js"></script>
</head>
<body>
    <div id="editor">
        <p>欢迎使用 <b>wangEditor</b> 富文本编辑器</p>
    </div>
    <button id="btn1">获取html</button>
    <!-- 注意， 只需要引用 JS，无需引用任何 CSS ！！！-->
    <script type="text/javascript">
        var E = window.wangEditor
        var editor = new E('#editor')
        editor.customConfig.menus = [
            'head',  // 标题
            'bold',  // 粗体
            'fontSize',  // 字号
            'fontName',  // 字体
            'italic',  // 斜体
            'underline',  // 下划线
            'strikeThrough',  // 删除线
            'foreColor',  // 文字颜色
            'backColor',  // 背景颜色
            'link',  // 插入链接
            'list',  // 列表
            'justify',  // 对齐方式
            'quote',  // 引用
            'emoticon',  // 表情
            'image',  // 插入图片
            'table',  // 表格
            'video',  // 插入视频
            'code',  // 插入代码
            'undo',  // 撤销
            'redo'  // 重复
        ]
        // 或者 var editor = new E( document.getElementById('editor') )
        /*editor.customConfig.onchange = function (html) {
            // 监控变化，同步更新到 textarea
            var json = editor.txt.getJSON()  // 获取 JSON 格式的内容
            var jsonStr = JSON.stringify(json)
            alert(jsonStr)
        }*/

        // 下面两个配置，使用其中一个即可显示“上传图片”的tab。但是两者不要同时使用！！！
        editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
        //editor.customConfig.uploadImgServer = '/api/test/upload'  // 上传图片到服务器

        // 隐藏“网络图片”tab
        //editor.customConfig.showLinkImg = false

        // 将图片大小限制为 5M
        editor.customConfig.uploadImgMaxSize = 5 * 1024 * 1024

        // 限制一次最多上传 5 张图片
        editor.customConfig.uploadImgMaxLength = 5

        editor.customConfig.uploadFileName = 'myFileName';

        // 上传图片时可自定义传递一些参数，例如传递验证的token等。参数会被添加到formdata中
        editor.customConfig.uploadImgParams = {
            // 如果版本 <=v3.1.0 ，属性值会自动进行 encode ，此处无需 encode
            // 如果版本 >=v3.1.1 ，属性值不会自动 encode ，如有需要自己手动 encode
            token: 'abcdef12345'
        }

        // 如果还需要将参数拼接到 url 中，可再加上如下配置
        //editor.customConfig.uploadImgParamsWithUrl = true

        editor.customConfig.uploadImgHooks = {
            customInsert: function (insertImg, result, editor) {
                // 图片上传并返回结果，自定义插入图片的事件（而不是编辑器自动插入图片！！！）
                // insertImg 是插入图片的函数，editor 是编辑器对象，result 是服务器端返回的结果

                // 举例：假如上传图片成功后，服务器端返回的是 {url:'....'} 这种格式，即可这样插入图片：
                var url = result.data;
                insertImg(url);

                // result 必须是一个 JSON 格式字符串！！！否则报错
            }
        }

        editor.customConfig.debug = true

        /*创建editor*/
        editor.create()
        editor.txt.append('<p>追加的内容</p>')
        document.getElementById('btn1').addEventListener('click', function () {
            // 读取 html
            alert(editor.txt.html())

            $.ajax({
                type: 'POST',
                url: "/api/article/saveArticle",
                data: {
                    str:editor.txt.html()
                },
                success: success,
                dataType: "json"
            });
        }, false)

    </script>



    <h1>123321</h1>
    <h2>${article.title}</h2>
    <a onclick="initTest()">123</a>
    <form method="POST" action="/api/test/upload" enctype="multipart/form-data">
        <input type="file" name="file" /><br/><br/>
        <input type="submit" value="Submit" />
    </form>



    <!-- 加载编辑器的容器 -->
    <script id="container" name="content" type="text/plain">
        这里写你的初始化内容
    </script>
    <!-- 配置文件 -->
    <script type="text/javascript" src="/ueditor1_4_3_3/ueditor.config.js"></script>
    <!-- 编辑器源码文件 -->
    <script type="text/javascript" src="/ueditor1_4_3_3/ueditor.all.js"></script>
    <!-- 实例化编辑器 -->
    <script type="text/javascript">
        var ue = UE.getEditor('container',{
            autoHeight: false
        });
    </script>

</body>
</html>