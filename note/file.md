## 文件上传
Springmvc为文件上传提供了直接支持，这种支持是通过即插即用的MultipartResolver实现的。MultipartResovler实现类是：CommonsMultipartResolver。
在Spring mvc上下文中默认没有配置MultipartResolver，因此默认情况下不能处理文件上传工作。需要先在上下文中配置MultipartResolver。

可以这样配置（我用spring boot 不用这样配置）：
```
<bean id="multipartResolver"  
    class="org.springframework.web.multipart.commons.CommonsMultipartResolver"
    p:defaultEncoding="utf-8"
    p:maxUploadSize="52428800" 
    p:uploadTempDir="file:/User/temp"/>
```
defaultEncoding  请求的编码格式，默认为ISO-8859-1
maxUploadSize  上传文件大小上限，单位为字节（5MB）
uploadTempDir  上传文件的临时路径

### 编写控制器和文件上传表单页面
```
public String handleFileUpload(@RequestParam("file") MultipartFile file){
    if(!file.isEmpty()){
        file.transferTo(new File("......"));
        return "......"
    }else{
        ......
    }
}
```
Spring mvc会将上传文件绑定到MultipartFile对象中。transferTo()方法可以将文件存到硬盘里。
MultipartFile提供的方法有很多，获取文件数据，文件MIME类型，文件名字等。

表单也要做修改，表单的编码类型必须是multipart/form-data类型。
```
<form class="form-horizontal" role="form" method="post" enctype="multipart/form-data" th:action="@{'/profile/'+${user.id}}" th:object="${user}">
<!-- left column -->
<div class="col-md-3">
    <div class="text-center">
        <img th:src="@{'/avatar/'+${user.id}}" style="width:100px;height:100px;" class="avatar img-circle" alt="avatar">
        <h6>上传头像</h6>
        <input type="file" name="file" class="form-control">
    </div>
</div>
```

