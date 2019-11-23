# Spring Boot 基础配置
### 全局配置文件
全局配置文件支持properties和yml两种格式

默认在src/main/resources目录下，Spring Boot提供了一个名为application.properties的全局配置文件，可对一些默认配置的配置值进行修改。
#### 优先级
properties优先级大于yml，如果同时存在application.properties和application.yml文件，只有application.properties文件生效
#### properties和yml区别
- yml支持树形结构
- 在properties文件中是以”.”进行分割的， 在yml中是用”:”进行分割
- yml的数据格式是K-V格式，并且通过”:”进行赋值
- yml比properties对中文对支持更友好

**使用yml注意点**
- key后面的冒号，后面一定要跟一个空格
- 在yml中缩进不要使用TAB
- 把原有的application.properties删掉。然后执行 maven -X clean install

```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
```

yml
```yaml
server: 
  port: 8081
 
spring: 
  datasource: 
    url: jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
    username: root
    passwoord: root
```

### 自定义配置文件
我们开发的服务会部署在不同的环境中，如开发、测试，生产等，
不同环境需要不同的配置，例如连接不同的 Redis、数据库、第三方服务等等。
Spring Boot 默认的配置文件是 application.properties(或yml)。我们需要根据
不同的环境配置不同的配置文件。

首先在application.properties同级目录下创建以下文件：
- application-dev.properties    开发环境
```properties
server.port=8081
```
- application-test.properties   测试环境
```properties
server.port=8082
```
- application-prod.properties   生产环境
```properties
server.port=8083
```

然后在application.properties指定spring.profiles.active使用哪个配置文件
以下示例指定使用test环境配置文件：
```properties
spring.profiles.active=test
```
此时项目会在test环境配置文件指定的8082端口运行。

熟悉前端开发的同学会比较熟悉webpack配置文件，和webpack的配置文件非常相似，
通常情况webpack会有一个webpack.base.config.js作为基础配置，然后还有webpack.test.config.js、webpack.prod.config.js,
在执行npm scripts命令(npm run dev/build)时指定process.env.NODE_ENV环境变量的值，
在webpack.base.config.js中通过判断process.env.NODE_ENV的值来加载对应的配置文件。

### 热加载
为了提高开发效率，我们需要配置热加载，不然开发速度很鸡肋。
#### 一、使用Jrebel插件
Jrebel是Idea提供的插件，是收费的，网上有许多很简单的破解方法，有条件的同学可以购买正版使用。
##### 第一步：安装插件，需要重启Idea生效

![image](https://github.com/zhuqitao/spring-boot-examples/blob/master/springboot-base-config/src/main/resources/static/images/1.png)

##### 第二步：激活
##### 第三步：设置当前项目为Jrebel热更新

![image](https://github.com/zhuqitao/spring-boot-examples/blob/master/springboot-base-config/src/main/resources/static/images/3.png)

点击左侧Jrebel小功能按钮，勾选需要Jrebel热更新的项目
到此就可以使用Jrebel热更新启动项目了

![image](https://github.com/zhuqitao/spring-boot-examples/blob/master/springboot-base-config/src/main/resources/static/images/4.png)

#### 二、devtools实现热加载
##### 第一步：引入devtools依赖
```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-devtools</artifactId>
   <optional>true</optional>
</dependency>
```
##### 第二步：设置Idea
按"Command+Option+Shift+/"选择Registry，选中compiler.automake.allow.when.app.running
，由于我已经选中，compiler.automake.allow.when.app.running在顶部显示，如果没选中需要下拉寻找

![image](https://github.com/zhuqitao/spring-boot-examples/blob/master/springboot-base-config/src/main/resources/static/images/6.png)

IntelliJ IDEA -> Preferences -> Build,Execution,Deplyment -> Compiler
选中Build project automatically

![image](https://github.com/zhuqitao/spring-boot-examples/blob/master/springboot-base-config/src/main/resources/static/images/5.png)

![image](https://github.com/zhuqitao/spring-boot-examples/blob/master/springboot-base-config/src/main/resources/static/images/7.png)
##### 第三步：application.properties配置
```properties
#热加载生效
spring.devtools.restart.enabled=true
#额外新增的热加载目录
spring.devtools.restart.additional-paths= src/main/java
#热加载排除目录
#spring.devtools.restart.exclude= 
```

### 常用插件
##### [Lombok 提高开发效率的插件](https://github.com/mplushnikov/lombok-intellij-plugin)
##### [GsonFormat 快速的将JSON转换为实体类](https://github.com/zzz40500/GsonFormat)
##### [Maven Helper 解决Maven插件冲突](http://plugins.jetbrains.com/plugin/7179-maven-helper)

