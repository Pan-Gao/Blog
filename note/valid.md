spring mvc支持的处理方法的前面包括以下几种：
1.使用@RequestParam绑定请求参数值。
```
public String showBlogs(
            @RequestParam("tag") Optional<String> tag, 
            @RequestParam("page") Optional<Integer> page) {
        ……
}
```
2.使用@CookieValue绑定请求中的cookie值
```
public String handler(
            @CookieValue(value="sessionId") Optional<String> sessionId){
        ……
}
```

3.使用@RequestHeader绑定请求报文头的属性值
请求报文包含了若干个报文头属性，服务器可据此获知客户端的信息。
```
public String handler(@RequestHeader("Accet-Encoding") String encoding,
            @RequestHeader("Keep-Alive") long keepAlive){
        ……
}
```

4.使用命令/表单对象绑定请求参数值
需要一个拥有若干属性的POJO，Spring mvc会按请求参数名和命令/表单对象属性名匹配的方式，自动为该属性填充属性值。支持级联的属性名。
```
public String handler(Blog blog) {
    ……
}
```
比方说Blog对象中有两个字段title和content，Spring会将HTTP请求中的数据title和content自动注入（根据名字进行匹配）到Blog对象中。

5.其他
还可以使用原生的servlet api(HttpServletRequest, HttpSession等)，I/O对象(InputStream, OutputStream)。

### 关于数据校验
Sping拥有自己独立的数据校验框架，同时支持JSR-303标准的校验框架。Spring的DataBinder在进行数据绑定时，可同时调用校验框架完成数据校验工作。
校验框架接口：
```
package org.springframework.validation;

public abstract interface Validator
{
    //能够对class类型的对象校验
    public abstract boolean supports(Class<?> paramClass);

    //对目标类target进行校验，并将校验错误记录在errors中
    public abstract void validate(Object paramObject, Errors paramErrors);
}
```

LocalValidatorFactoryBean即实现了Spring的Validator接口，又实现了JSR-303的Validator接口。默认装配这个。

用法：
```
public class BlogCreateForm {

    @Size(min = 1, max = 50, message="文章标题长度须在1-50字之间")
    private String title;
    ......
}
```
控制器：
```
//提交新建的博文
@PutMapping("/blogs/create")
public String createBlog(@ModelAttribute ("blog") @Valid BlogCreateForm form, 
    BindingResult result){
    if(result.hasErrors()){
        return "create";
    }
    ......
}
```
渲染页面：
```
<div class="form-group">
    <label for="title">标题</label>
    <input type="text" class="form-control" name="title" th:field="*{title}" placeholder="文章的标题" autofocus="">
    <p th:if="${#fields.hasErrors('title')}" th:errors="*{title}">标题长度必须在2-30之间</p>
</div>
```

BindResult必须接在被@Valid标注的入参对象后面，否则会出错。
原因是：Spring mvc是通过对处理方法前面的规约来保存校验结果的：前一个表单对象的校验结果保存在其后的入参中，这个保存结果的入参必须是BindingResult或Errors类型，这两个类都在org.springframework.validation包中。就是说需校验的表单对象和绑定结果必须成对出现。

### Errors和BindingResult接口
Errors：存储和暴露关于数据绑定错误和验证错误相关信息的接口，提供了相关存储和获取错误消息的方法。
```
package org.springframework.validation;

public interface Errors {
    /**
    * Return if there were any errors.
    */
    boolean hasErrors();

    /**
    * Return the total number of errors.
    */
    int getErrorCount();

    /**
    * Get all errors, both global and field ones.
    * @return List of {@link ObjectError} instances
    */
    List<ObjectError> getAllErrors();

    ......

    /**
    * Are there any field errors?
    * @return {@code true} if there are any errors associated with a field
    * @see #hasGlobalErrors()
    */
    boolean hasFieldErrors();

    /**
    * Return the number of errors associated with a field.
    * @return the number of errors associated with a field
    * @see #getGlobalErrorCount()
    */
    int getFieldErrorCount();

    /**
    * Get all errors associated with a field.
    * @return a List of {@link FieldError} instances
    */
    List<FieldError> getFieldErrors();

    /**
    * Get the <i>first</i> error associated with a field, if any.
    * @return the field-specific error, or {@code null}
    */
    FieldError getFieldError();

    ......
}
```
BindingResult接口扩展了Errors接口。

如何在页面中显示错误的？
Spring mvc会将校验结果保存到对应的BindingResult或Errors对象中，还会保存在“隐含模型”中，暴露给视图对象。
![](https://github.com/yuanrw/Blog/blob/master/note/pic/valid1.png)

表单验证分为客户端验证和服务器端验证。

## 表单验证
客户端验证。当用户填写的表单存在明显错误时，浏览器端Javascript提示错误信息，并阻止表单提交。这样既避免了页面跳转更加用户友好，又减少了服务器的负载。

服务器端验证可防止恶意的攻击。另外，有些验证过程客户端无法完成，比如用户名已存在、密码错误等。

### 服务端验证
针对上述第四种绑定参数方式。
可以使用spring的校验框架。
参见上面的方法。

### 客户端验证
登录，注册的页面，表单提交后，浏览器发送了一个POST请求到服务器，如果验证失败了，服务器重新渲染页面返回，但是页面上只是多出了一个错误消息。但是每一次完全刷新整个页面也是有代价的——从服务器上获得的完整页面内容会很大，传输过程需要时间，浏览器渲染也需要时间，这就会对用户体验造成巨大的影响。

用AJAX技术优化，不重新加载页面便能向服务器发送HTTP请求。
我用json作为浏览器和服务端之间的数据格式。

标注@RequestBody注解时，Spring MVC可以推测HTTP请求Body的类型并自动解析，解析JSON时用到了jackon库，(引入spring-boot-starter-web已经自动引入了jackson)。
标注@ResponseBody注解时，Spring MVC会将其JSON序列化并返回到浏览器。

```
//用户登录，通过email
@PostMapping("/login")
@ResponseBody
public LoginForm login(@RequestBody UserLoginForm form, 
            @RequestParam("remember-me") Optional<String> rememberMe, 
            @RequestParam("next") Optional<String> next, 
            HttpSession session) {

    LoginForm loginStatus = new LoginForm();		
    //登陆
    User user = userService.login(form.getEmail(), form.getPassword());

    if(user == null) {
        //登陆失败
        loginStatus.setStatus("failed");
        loginStatus.setMessage("登陆失败！");
        return loginStatus;
    }else {
        //登陆成功
        session.setAttribute("CURRENT_USER", user);

        //添加cookie

        //设置状态值，返回给浏览器
        loginStatus.setStatus("success");
        loginStatus.setMessage("登陆成功！");
        loginStatus.setInfo(next.isPresent()?next.get():"");
        return loginStatus;
    }
}
```
```
function validateForm(){
    var email = document.getElementById("email").value;
    var reg=/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
    if (!reg.test(email)){
        document.getElementById("emailMes").innerHTML = "邮箱格式不正确！";
        return false;
    }
    var password = document.getElementById("password").value;
    if(password.length<6 || password.length>30){
        document.getElementById("passwordMes").innerHTML = "密码长度在6-30位之间！";
        return false;
    }
    var data = {email: email, password: password};
    $.ajax({
        url: window.location.pathname,
        method: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        success:function(msg){
            if(msg.status == "success"){
            document.location.href = "http://localhost:8080/"+msg.info;
            }else if(msg.status == "failed"){
                document.getElementById("alert").innerHTML = '<div class="alert alert-warning"><a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a><strong>Warning!</strong>邮箱或密码不正确，请重试！</div>';
            }else{
                document.write(msg);
            }
        }
    });
}
```

