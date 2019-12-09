# Spring Boot 监听器、过滤器、拦截器以及自定义事件配置

## 监听器
监听器是 Servlet 规范中定义的一种特殊类，用于监听 ServletContext、HttpSession
 和 ServletRequest 等域对象的创建与销毁事件，以及监听这些域对象中属性发生修改的事件。
 监听器具有异步的特性。
 - ServletContext： 应用于整个应用，一个应用只有一个ServletContext
 - HttpSession： 针对每一个会话，可用于统计会话和在线用户数
 - ServletRequest： 针对具体某个请求
 #### 实现监听器
 spring boot 配置监听器有两种方式，一种是使用@WebListener和@ServletComponentScan注解扫描方式，另一种是借助spring boot 提供的ServletListenerRegistrationBean方法注册。

 下面以ServletRequest为例实现一个请求监听器
##### 方式一：@WebListener和@ServletComponentScan注解扫描
创建MyListener
```
@WebListener
public class MyListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        System.out.println("ServletRequestEvent 销毁");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("ServletRequestEvent init");
    }
}
```
在启动类中添加@ServletComponentScan注解
```
@SpringBootApplication
@ServletComponentScan
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```
在启动类添加@ServletComponentScan，spring boot会自动扫描所有使用@WebListener声明的监听器并注册到应用中。

##### 方式二：使用ServletListenerRegistrationBean方法注册
启动类去除@ServletComponentScan注解
在Config目录创建配置文件ListenerConfig
```
@Configuration
public class ListenerConfig {
    @Bean
    public ServletListenerRegistrationBean<MyListener> myListenerServletListenerRegistrationBean() {
        ServletListenerRegistrationBean<MyListener> bean = new ServletListenerRegistrationBean<>(new MyListener());
        return bean;
    }
}
```

## 过滤器
过滤器在Listener之后以及请求到达servlet之前执行，过滤器可以在请求到达服务器之前，对请求进行预先处理，在响应内容到达客户端之前，对服务器做出的响应进行后置处理。通过过滤器，读取请求携带的cookie判断用户是否登录等。

#### 过滤器种类
Servlet 3.1 提供的几种常见的过滤器组件：
- 身份验证过滤器：Authentication filters
- 数据压缩过滤器：Data compression Filters
- 加密过滤器：Encryption Filters
- 图片转换过滤器：Image conversion filters
- 日志和安全审计过滤器：Logging and auditing filters
- 词法类过滤器：Tokenizing filters
- 触发资源访问事件过滤器：Filters that trigger resource access events
- XML文件转换过滤器：XSL/T filters that transform XML content
- 缓存类过滤器：Caching filters
- MIME-TYPE 链过滤器：MIME-TYPE Chain Filters

#### 实现过滤器
实现过滤器也有两种方式，一种是使用@WebFilter和@ServletComponentScan注解扫描，另一种是借助spring boot 提供的FilterRegistrationBean方法注册。

##### 方式一：@WebFilter和@ServletComponentScan注解扫描
使用@WebFilter这种方式，当有多个过滤器时，可以使用@Order注解指定过滤器优先级来确定他们的执行顺序。
@Order(x) x的值越小，优先级越高，优先执行。
> @Order有时好使有时不好使，如果希望正确的指定过滤器的执行顺序，可以使用第二种方式

创建MyFilter1
```
@Order(1)
@WebFilter(filterName = "MyFilter1", urlPatterns = {"/*"})
public class MyFilter1 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("MyFilter1 init");
    }

    @Override
    public void destroy() {
        System.out.println("MyFilter1 destroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 在请求到达servlet之前对request、response做一些预处理 比如设置请求编码
        System.out.println("MyFilter1 doFilter");
        // 传给下一个过滤器进行处理，如果该过滤器是最后一个过滤器，则直接交给servlet处理
        filterChain.doFilter(request, response);
    }
}
```
创建MyFilter2
```
@Order(2)
@WebFilter(filterName = "MyFilter2", urlPatterns = {"/hello"})
public class MyFilter2 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("MyFilter2 init");
    }

    @Override
    public void destroy() {
        System.out.println("MyFilter2 destroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 在请求到达servlet之前对request、response做一些预处理 比如设置请求编码
        System.out.println("MyFilter2 doFilter");
        // 传给下一个过滤器进行处理，如果该过滤器是最后一个过滤器，则直接交给servlet处理
        filterChain.doFilter(request, response);
    }
}
```
启动类添加@ServletComponentScan注解
```
@SpringBootApplication
@ServletComponentScan
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

##### 方式二：使用FilterRegistrationBean方法注册
启动类去除@ServletComponentScan注解
在Config目录创建FilterConfig
```
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean filterRegistrationBean1() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new MyFilter1());
        registrationBean.setName("MyFilter1");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean2() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new MyFilter2());
        registrationBean.setName("MyFilter2");
        registrationBean.addUrlPatterns("/hello");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
```

## 拦截器
拦截器（HandlerInterceptor）不是Servlet提供的，而是spring提供的一种基于Java的反射机制进行实现的，它是面向切面编程（AOP）的一种应用，触发时机在请求离开过滤器之后和到达servlet之前，处于过滤器和servlet之间，常用场景：日志记录、计算PV、权限检查、性能监控等。

#### 实现拦截器
创建MyInterceptor
```
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在请求到达controller之前调用
        System.out.println("请求之前preHandle");
        // 如果返回false，则会中断执行,不会进入到postHandle和afterCompletion
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在Controller方法处理完之后DispatcherServlet进行视图的渲染之前，在这里可以对ModelAndView进行操作
        System.out.println("请求之后postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // DispatcherServlet进行视图的渲染之后的回调方法，一般用于资源清理
        System.out.println("进行视图的渲染之后回调afterCompletion");
    }
}
```
在Config目录创建InterceptorConfig实现WebMvcConfigurer接口完成拦截器的注册
```
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    MyInterceptor myInterceptor = new MyInterceptor();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor).addPathPatterns("/*");
    }
}
```

## 自定义事件
实现一个自定义事件，需要三部分
- ApplicationEvent：继承ApplicationEvent编写构造函数实现一个自定义事件
- ApplicationListener：实现ApplicationListener接口onApplicationEvent方法对事件进行监听
- ApplicationEventPublisher：使用ApplicationEventPublisher提供的publishEvent方法发布事件

#### 实现自定义事件
创建自定义事件CustomEvent
```
public class CustomEvent extends ApplicationEvent {
    public CustomEvent(Object source) {
        super(source);
    }
}
```
监听自定义事件有四种方式：
- **方式一：使用ConfigurableApplicationContext上下文装载自定义事件**

创建CustomListener
```
public class CustomListener implements ApplicationListener<CustomEvent> {
    @Override
    public void onApplicationEvent(CustomEvent customEvent) {
        System.out.println("监听到事件：" + CustomListener.class.getName() + customEvent.getSource());
    }
}
```

然后在启动类中获取ConfigurableApplicationContext上下文，使用addApplicationListener方法进行自定义事件监听

```
@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleApplication.class, args);
        context.addApplicationListener(new CustomListener());
    }

}
```

- **方式二：在application.properties配置文件中配置监听**

创建CustomListener
```
public class CustomListener implements ApplicationListener<CustomEvent> {
    @Override
    public void onApplicationEvent(CustomEvent customEvent) {
        System.out.println("监听到事件：" + CustomListener.class.getName() + customEvent.getSource());
    }
}
```
在application.properties中添加一下配置
```
context.listener.classes=com.love.example.Config.CustomListener
```

- **方式三（推荐）：使用@Component注解把ApplicationListener注入到spring容器中**

创建CustomListener
```
@Component
public class CustomListener implements ApplicationListener<CustomEvent> {
    @Override
    public void onApplicationEvent(CustomEvent customEvent) {
        System.out.println("监听到事件：" + CustomListener.class.getName() + customEvent.getSource());
    }
}
```

- **方式四（推荐）: 使用@EventListener注解，无需实现ApplicationListener接口**

创建CustomListener
```
@Component
public class CustomListener{
    @EventListener
    public void listener(CustomEvent customEvent) {
        System.out.println("监听到事件：" + CustomListener.class.getName() + customEvent.getSource());
    }
}
```
*注意：这种方式仍然需要@Component注解将ApplicationListener注入到spring容器中*

##### 发布自定义事件（触发自定义事件）
在HelloController添加ApplicationContext.publishEvent方法发布自定义事件
```
@RestController
public class HelloController {
    @Resource private
    ApplicationContext applicationContext;

    @GetMapping("/hello")
    public String hello() {

        applicationContext.publishEvent(new CustomEvent("测试自定义事件"));
        return "hello";
    }
}
```

#### 具体代码可查看[源码地址](https://github.com/zhuqitao/spring-boot-examples/tree/master/springboot-listener-filter-interceptor)，欢迎star