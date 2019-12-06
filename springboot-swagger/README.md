# Spring Boot 整合Swagger2
前后端分离开发模式已成大势，一份可读性强、简单易用、实时性强的API文档对于提高开发效率显得尤为重要。相比后端人员手写word作为API文档，要想保证API文档实时有效性，后端人员修改接口，必须同时找到对应的API文档并对其修改，无形中增加了后端人员开发工作量。Swagger 给我们提供了一个全新的维护 API 文档的方式。

### 引入依赖
```
 <dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.6.1</version>
</dependency>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.6.1</version>
</dependency>
```
### 创建User实体类
```
public class User {
    private String name;
    private String password;
    // 省略get、set、构造方法...
}
```
### 创建Controller类，编写接口
创建UserCoontroller和TestController
```
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/get")
    public User getUserByName(@RequestParam String name) {
        return new User(name, "123456");
    }
    @PostMapping("/add")
    public Boolean addUser(@RequestBody User user) {
        return true;
    }
    @PutMapping("/update")
    public Boolean updateUser(@RequestBody User user) {
        return true;
    }
    @DeleteMapping("/delete")
    public Boolean deleteUserByName(@RequestParam String name) {
        return true;
    }
}
```
```
@RestController
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "Only for test controller";
    }
}
```

### 添加Swagger配置
Springfox 提供了一个 Docket 对象，让我们可以灵活的配置 Swagger 的各项属性。
创建SwaggerConfig
```
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.love.example"))
                .paths(PathSelectors.any())
                .build();
    }
}
```
启动服务，访问[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)，可以看到下图效果：
![image](http://q21ledx2j.bkt.clouddn.com/example:spring-boot-swagger:1.png)

### 添加文档相关配置

（1）给整个文档添加配置信息，支持文档的版本号、联系人邮箱、网站、版权、开源协议等等信息
修改SwaggerConfig
```
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.love.example"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot集成Swagger2实例")
                .description("简约Restful风格接口文档")
                .version("1.1.0")
                .build();
    }
}
```
（2）@Api 和@ApiOperation注解给 Controller和接口方法 添加描述信息
修改UserController
```
@RestController
@Api(tags = "用户相关接口", description = "用户的增删改查")
@RequestMapping("/user")
public class UserController {
    @ApiOperation("获取用户")
    @GetMapping("/get")
    public User getUserByName(@RequestParam String name) {
        return new User(name, "123456");
    }
    @ApiOperation("添加用户")
    @PostMapping("/add")
    public Boolean addUser(@RequestBody User user) {
        return true;
    }
    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Boolean updateUser(@RequestBody User user) {
        return true;
    }
    @ApiOperation("删除用户")
    @DeleteMapping("/delete")
    public Boolean deleteUserByName(@RequestParam String name) {
        return true;
    }
}
```

（3） @ApiModel 和 @ApiModelProperty注解给实体类Model和实体类属性添加配置信息
修改实体类User
```
@ApiModel("用户实体类")
public class User {
    @ApiModelProperty("用户名字")
    private String name;
    @ApiModelProperty("用户密码")
    private String password;
    // 省略get、set、构造方法...
}
```

重新启动项目，访问[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)可看到下图效果：

![image](http://q21ledx2j.bkt.clouddn.com/example:spring-boot-swagger:2.png)
可以看到我们添加的配置说明信息都已经显示。

### 接口过滤
有时候某些敏感接口或者后端开发内部测试使用接口不想暴露在Swagger文档，swagger提供了两种过滤接口的方法。
#### 方式一：使用@ApiIgnore 注解
直接在想要屏蔽的接口方法添加@ApiIgnore 注解就可以屏蔽掉该接口。
修改UserController
```
@RestController
@Api(tags = "用户相关接口", description = "用户的增删改查")
@RequestMapping("/user")
public class UserController {
    @ApiOperation("获取用户")
    @GetMapping("/get")
    public User getUserByName(@RequestParam String name) {
        return new User(name, "123456");
    }
    @ApiOperation("添加用户")
    @PostMapping("/add")
    public Boolean addUser(@RequestBody User user) {
        return true;
    }
    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Boolean updateUser(@RequestBody User user) {
        return true;
    }
    
    @ApiIgnore
    @ApiOperation("删除用户")
    @DeleteMapping("/delete")
    public Boolean deleteUserByName(@RequestParam String name) {
        return true;
    }
}
```
上述代码就在swagger屏蔽了删除用户的方法

#### 方式二：在 Docket 上增加筛选
Docket 类提供了 apis() 和 paths()两 个方法过滤接口。
- apis()：指定包名，Swagger只扫描指定包下的接口
- paths()：通过匹配URL路径规则进行过滤

修改SwaggerConfig
```
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.love.example.UserController"))
                .paths(PathSelectors.regex("/user/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot集成Swagger2实例")
                .description("简约Restful风格接口文档")
                .version("1.1.0")
                .build();
    }
}
```
重新启动项目，访问[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)可看到下图效果：

![image](http://q21ledx2j.bkt.clouddn.com/example:spring-boot-swagger:3.png)

可以看到过滤掉了TestController所有接口，UserController类下的删除用户接口也过滤掉了

### 最佳实践方式
最好的实现方式就是不写@Api、@ApiOperation、@ApiModel、@ApiModelProperty等各种说明注解，团队严格遵循RESTful接口的设计原则和统一的交互规范，尽量采用语义化英文名词，这样前端人员在看接口文档时，根据语义就能知道每个接口的作用。

### 生产环境下禁用swagger
#### 方式一：使用@@Profile注解
创建application-dev.properties
```
server.port=8081
```
创建application-test.properties
```
server.port=8081
```
创建application-prod.properties
```
server.port=8082
```

修改SwaggerConfig， 添加@Profile({"dev","test"})，指明只在开发和测试环境才启用swagger
```
@Configuration
@EnableSwagger2
@Profile({"dev","test"})
public class SwaggerConfig {
    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.love.example.UserController"))
                .paths(PathSelectors.regex("/user/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot集成Swagger2实例")
                .description("简约Restful风格接口文档")
                .version("1.1.0")
                .build();
    }
}
```

修改application.properties
```
spring.profiles.active=dev
# 或者 spring.profiles.active=test
```
开发或者测试环境启动项目，浏览器访问[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html),依然可以看到swagger接口文档

再次修改修改application.properties
```
spring.profiles.active=prod
```
生产环境再次重启项目，浏览器访问[http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html),发现浏览器可以正常访问，但是看不到任何文档：
![image](http://q21ledx2j.bkt.clouddn.com/example:spring-boot-swagger:4.png)

#### 方式二： 使用注解@ConditionalOnProperty
修改application-dev.properties
```
server.port=8081
swagger.enable=true
```
修改application-test.properties
```
server.port=8081
swagger.enable=true
```
修改application-prod.properties
```
server.port=8082
swagger.enable=false
```

修改SwaggerConfig，去除@Profile({"dev","test"})，添加@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")

修改application.properties，如果spring.profiles.active=dev或者spring.profiles.active=test，重启项目，浏览器访问[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)，可以看到swagger接口文档，如果修改spring.profiles.active=prod，重启项目，浏览器访问[http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)，则看不到任何接口文档。

### 方式三：使用@Value和Docket提供的enable方法
application-dev.properties、application-test.properties、application-prod.properties这三个文件与方式二中保持一致

修改SwaggerConfig

```
@Configuration
@EnableSwagger2

public class SwaggerConfig {

    @Value("${swagger.enable}")
    private Boolean enable;

    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.love.example.UserController"))
                .paths(PathSelectors.regex("/user/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot集成Swagger2实例")
                .description("简约Restful风格接口文档")
                .version("1.1.0")
                .build();
    }
}
```

修改application.properties，如果spring.profiles.active=dev或者spring.profiles.active=test，重启项目，浏览器访问[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)，可以看到swagger接口文档，如果修改spring.profiles.active=prod，重启项目，浏览器访问[http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)，则看不到任何接口文档。

#### 具体代码可查看[源码地址](https://github.com/zhuqitao/spring-boot-examples/tree/master/springboot-swagger)，欢迎star