# Spring Boot全局异常处理
开发中我们希望只关心业务核心代码，不希望在每个地方都做相同的异常处理，所以
需要一个大家都遵守的开发规范和一个全局的异常处理。

#### 创建响应状态码枚举类HttpStatusTypeEnum
```java
public enum HttpStatusTypeEnum {
    SUCCESS(200, "成功"),
    USER_INPUT_ERROR(400, "用户输入异常"),
    SYSTEM_ERROR (500,"系统服务异常"),
    OTHER_ERROR(-1,"未知异常");

    private int code;
    private String desc;

    HttpStatusTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
```
定义了4种不用的状态，200：成功；400：用户输入异常；500：系统内部异常；-1：其他未知异常
#### 创建异常类ResponseException
```java
public class ResponseException extends RuntimeException {
    // 异常状态码
    private int code;
    // 异常信息
    private String message;

    public ResponseException() {
        super();
    }
    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ResponseException(HttpStatusTypeEnum enmu, String message) {
        super(message);
        this.code = enmu.getCode();
        this.message = message;
    }

    // get和set
}
```
异常类包括异常状态码code和异常信息message，message尽量使用语义化的语言来描述，
这样返回给浏览器也能根据message迅速定位到异常原因。

#### 创建响应类HttpResponse
```java
public class HttpResponse {
    // 是否成功
    private Boolean success;
    // 响应状态码
    private int code;
    // 成功或失败的提示信息
    private String message;
    // 响应返回的数据
    private Object data;

    // 请求异常处理
    public static HttpResponse error(ResponseException e) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(false);
        httpResponse.setCode(e.getCode());
        httpResponse.setMessage(e.getMessage());
        httpResponse.setData(null);
        return httpResponse;
    }

    // 请求异常处理
    public static HttpResponse error(int code, String message) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(false);
        httpResponse.setCode(code);
        httpResponse.setMessage(message);
        httpResponse.setData(null);
        return httpResponse;
    }

    // 成功
    public static HttpResponse success() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(true);
        httpResponse.setCode(200);
        httpResponse.setMessage("success");
        return httpResponse;
    }

    // 成功 有data数据
    public static HttpResponse success(Object data) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(true);
        httpResponse.setCode(200);
        httpResponse.setMessage("success");
        httpResponse.setData(data);
        return httpResponse;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
```
统一响应返回类型，主要包括success：是否成功状态；code：状态码；
message：失败或者成功提示信息；data：需要传给浏览器的数据。
还有success()和error()方法

#### 创建全局异常处理类WebExceptionHandler
```java

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(ResponseException.class)
    @ResponseBody
    public HttpResponse responseException(ResponseException e) {
        System.out.println("发生业务异常，原因是：" + e.getMessage());
       return HttpResponse.error(e);
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public  HttpResponse exception(Exception e) {
        System.out.println("发生未知异常，原因是：" + e);
        return HttpResponse.error(HttpStatusTypeEnum.OTHER_ERROR.getCode(), "未知异常");
    }
}
```
@ControllerAdvice注解：作用是监听所有的controller，针对Controller抛出的异常进行处理
@ExceptionHandler注解：作用是根据Controller抛出的异常的类型分别处理，不用的异常类型使用不同的处理方法

可以根据情况在WebExceptionHandler中继续添加其他类型异常的处理。

#### 创建控制层HelloConTroller
```java
@RestController
public class HelloController {
    @GetMapping("/normal")
    public HttpResponse success() throws ResponseException {
        return HttpResponse.success();
    }
    @GetMapping("/err/match")
    public HttpResponse errMatch(int id) throws ResponseException {
        if(id == 0) {
            throw new ResponseException(HttpStatusTypeEnum.USER_INPUT_ERROR, "id不符合标准");
        } else {
            Map<String, Integer> data = new HashMap<>();
            data.put("id", id);
            return HttpResponse.success(data);
        }
    }
    @GetMapping("/err/system")
    public HttpResponse sysError() throws ResponseException {
        try{
            int i = 1/0;
        } catch(Exception e) {
            throw new ResponseException(HttpStatusTypeEnum.SYSTEM_ERROR.getCode(), "系统异常");
        }
        return HttpResponse.success();
    }
}
```
在Controller中写了三个路由，"/normal"是一个正常的路由，
"/err/match"会根据用户传的id的值判断，如果id=0，则认为是一个异常的参数，
"/err/system"会自动抛出一个系统异常。

启动项目，浏览器访问[http://localhost:8080/normal](http://localhost:8080/normal)
会获取正常的响应；

访问[http://localhost:8080/err/match?id=0](http://localhost:8080/err/match?id=0)
会获取"id不符合标准"的异常响应，同时控制台也会打印出异常信息；

访问[http://localhost:8080/err/match?id=1](http://localhost:8080/err/match?id=1)则会
获取正常的响应；

访问[http://localhost:8080/err/system](http://localhost:8080/err/system)会获取到
"系统异常"的异常响应。

