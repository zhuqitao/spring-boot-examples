# Spring Boot整合JPA
### 介绍
#### 什么是JPA
JPA(Java Persistence API)，也叫Java 持久化 API，是一个 Java 应用程序接口 规范，描述了使用 Java标准版平台（Java SE）
 和 Java企业版平台（Java EE）的应用中的 关系数据 的管理。
 
 它的出现主要是为了简化现有的持久化开发工作和整合 ORM 技术，实现 Hibernate，TopLink，JDO 等 ORM 框架大一统局面。
 #### 什么是Spring Data Jpa
 Spring Data Jpa 是基于ORM框架、Jpa规范封装的一套Jpa应用框架，底层使用了 Hibernate 的 JPA 技术实现，
 它提供了包括增删改查等在内的常用功能，且易于扩展！学习并使用 Spring Data Jpa 可以极大提高开发效率！
 
 ### Spring Boot继承Spring Data Jpa
 #### 数据库
 启动本地MySql，创建数据库jpa_test
 #### 添加依赖
 ```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
#### 配置application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jpa_test?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# jpa配置
spring.jpa.database=mysql
# 自动创建|更新|验证数据库表结构的方式
# 其中update是最常用的属性，首次加载hibernate会根据model自动创建表结构，以后再加载hibernate会根据model自动更新表结构
spring.jpa.hibernate.ddl-auto=update
# 输出所有SQL语句到控制台.
spring.jpa.show-sql=true
```

#### 创建实体类User
```java
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 32)
    private String name;
    @Column(nullable = false)
    private String password;

    public User() {

    }
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    // get、set和toString方法...
    
```
- @Entity：表明这是一个接受JPA管理的实体类，对应数据库中一张表，如果没有该表，则会自动创建表
- @Table(name="user")：指明这个实体类对应库中表的名字，如果不使用该注解，则会根据实体类名字与表明
按照驼峰及下划线规则匹配。
- @Id：指定这个字段为表的主键
- @GeneratedValue(strategy = GenerationType.IDENTITY)：指定主键生成方式为自增方式
    - GenerationType.TABLE：使用一个特定的数据库表格来保存主键
    - GenerationType.SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列
    - GenerationType.IDENTITY：主键由数据库自动生成（主要是自动增长型）
    - GenerationType.AUTO：默认值，持久化引擎会根据数据库在以上三种主键生成策略中选择其中一种
- @Column：指定某个属性对应表中哪个字段，nullable=falseb表示字段不能为空，unique=true表示字段唯一不能重复，length=32表示最大长度不能超过32
### 创建JPA仓库
继承JpaRepository<T, ID>接口实现数据操作
```java
public interface UserRepository extends JpaRepository<User, Integer> {
   
}
```
JpaRepository<T,ID>T代表实体类，ID是主键id,JpaRepository已经为我们提供了单表的基础操作方法（findById、findAll、save）等方法。
添加数据和修改数据都是使用save方法，会根据主键id查询是否已经存在实现增加或修改数据。

#### Service接口
```java
public interface UserService {

    Optional<User> getUser(Integer id);

    List<User> getAllUser();

    Boolean addUser(User user);

    Boolean updateUser(Integer id, String name, String password);

    Boolean deleteUser(Integer id);
}
```

#### Service接口实现
```java
@Service("UserService")
public class UserServiceTemplate implements UserService {
    // 注入JPA仓库对象
    @Resource
    UserRepository userRepository;

    @Override
    public Optional<User> getUser(Integer id) {

        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Boolean addUser(User user) {
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean updateUser(Integer id, String name, String password) {
        User user = userRepository.findById(id).get();
        user.setName(name);
        user.setPassword(password);
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean deleteUser(Integer id) {
        userRepository.deleteById(id);
        return true;
    }
}
```

#### 编写Controller
```java
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/add")
    public String addUser(String name, String password) {
        User user = new User(name, password);
        userService.addUser(user);
        return "1";
    }
    @GetMapping("/get")
    public User getUser(Integer id) {
        Optional<User> userOptional = userService.getUser(id);
        User user = userOptional.orElse(null);
        return user;
    }
    @GetMapping("/getAll")
    public List<User> getAllUser() {
        return userService.getAllUser();
    }
    @GetMapping("/update")
    public String updateUser(Integer id, String name, String password) {
        userService.updateUser(id, name, password);
        return "1";
    }
    @GetMapping("/delete")
    public String deleteUser(Integer id) {
        userService.deleteUser(id);
        return "1";
    }
}
```
启动项目，由于在配置文件中设置了spring.jpa.hibernate.ddl-auto=update，
如果数据库中没有实体类对应的表，则创建表，如果已经存在，则判断实体类结构是否与表结构一致，不一致则更新表结构。
一切正常情况下，会看到数据库jpa_test多了一张user表，浏览器访问
[http://localhost:8080/user/add?name=test&password=123456](http://localhost:8080/user/add?name=test&password=123456),
则会往user表中添加一条数据。
 
### 简单自定义查询
简单自定义查询就是根据方法名字自动生成SQL。比如findByName、findByNameAndPassword.

修改UserRepository，添加findByName和findByNameAndPassword方法
```java
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
    User findByNameAndPassword(String name, String password);
}
```

修改UserService，添加getUserByName和getUserByNameAndPassword
```java
public interface UserService {
    ...

    User getUserByName(String name);
    User getUserByNameAndPassword(String name, String password);

    ...
}
```
修改UserServiceTemplate，添加getUserByName和getUserByNameAndPassword的实现
```java
@Service("UserService")
public class UserServiceTemplate implements UserService {
    ...

    @Override
    public User getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User getUserByNameAndPassword(String name, String password) {
        return userRepository.findByNameAndPassword(name, password);
    }

    ...
}
```
修改UserController，添加"/getUserByName"和"/getUserByNameAndPassword"路由
```java
@RestController
@RequestMapping("/user")
public class UserController {
    ...
    
    @GetMapping("/getUserByName")
    public User getUserByName(String name) {
        return userService.getUserByName(name);
    }
    @GetMapping("/getUserByNameAndPassword")
    public User getUserByName(String name, String password) {
        return userService.getUserByNameAndPassword(name, password);
    }
    
    ...
}
```

项目重启，浏览器访问
[http://localhost:8080/user/getUserByName?name=test](http://localhost:8080/user/getUserByName?name=test)或者
[http://localhost:8080/user/getUserByNameAndPassword?name=test&password=123456](http://localhost:8080/user/getUserByNameAndPassword?name=test&password=123456)
可以查询到之前添加的那条数据。

更多关键字查询
<table>
    <thead>
        <tr>
            <th>关键字</th>
            <th>例子</th>
            <th>JPQL片段</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>And</td>
            <td>findByNameAndPassword</td>
            <td>… where x.name = ?1 and x.password = ?2</td>
        </tr>
        <tr>
            <td>Or</td>
            <td>findByNameOrPassword</td>
            <td>… where x.name = ?1 or x.password = ?2</td>
        </tr>
        <tr>
            <td>Is,Equals</td>
            <td>findByNameIs,findByNameEquals</td>
            <td>… where x.name = ?1</td>
        </tr>
        <tr>
            <td>Between</td>
            <td>findByStartDateBetween</td>
            <td>… where x.startDate between ?1 and ?2</td>
        </tr>
        <tr>
            <td>LessThan</td>
            <td>findByAgeLessThan</td>
            <td>… where x.age < ?1</td>
        </tr>
        <tr>
            <td>LessThanEqual</td>
            <td>findByAgeLessThanEqual</td>
            <td>… where x.age ⇐ ?1</td>
        </tr>
        <tr>
            <td>GreaterThan</td>
            <td>findByAgeGreaterThan</td>
            <td>… where x.age > ?1</td>
        </tr>
        <tr>
            <td>GreaterThanEqual</td>
            <td>findByAgeGreaterThanEqual</td>
            <td>… where x.age >= ?1</td>
        </tr>
        <tr>
            <td>After</td>
            <td>findByStartDateAfter</td>
            <td>… where x.startDate > ?1</td>
        </tr>
        <tr>
            <td>Before</td>
            <td>findByStartDateBefore</td>
            <td>… where x.startDate < ?1</td>
        </tr>
        <tr>
            <td>IsNull</td>
            <td>findByAgeIsNull</td>
            <td>… where x.age is null</td>
        </tr>
        <tr>
            <td>IsNotNull,NotNull</td>
            <td>findByAge(Is)NotNull</td>
            <td>… where x.age not null</td>
        </tr>
        <tr>
            <td>Like</td>
            <td>findByNameLike</td>
            <td>… where x.name like ?1</td>
        </tr>
        <tr>
            <td>NotLike</td>
            <td>findByNameNotLike</td>
            <td>… where x.name not like ?1</td>
        </tr>
        <tr>
            <td>StartingWith</td>
            <td>findByNameStartingWith</td>
            <td>… where x.name like ?1 (parameter bound with appended %)</td>
        </tr>
        <tr>
            <td>EndingWith</td>
            <td>findByNameEndingWith</td>
            <td>… where x.name like ?1 (parameter bound with prepended %)</td>
        </tr>
        <tr>
            <td>Containing</td>
            <td>findByNameContaining</td>
            <td>… where x.name like ?1 (parameter bound wrapped in %)</td>
        </tr>
        <tr>
            <td>OrderBy</td>
            <td>findByAgeOrderByNameDesc</td>
            <td>… where x.age = ?1 order by x.name desc</td>
        </tr>
        <tr>
            <td>Not</td>
            <td>findByNameNot</td>
            <td>… where x.name <> ?1</td>
        </tr>
        <tr>
            <td>In</td>
            <td>findByAgeIn(Collection ages)</td>
            <td>… where x.age in ?1</td>
        </tr>
        <tr>
            <td>NotIn</td>
            <td>findByAgeNotIn(Collection age)</td>
            <td>… where x.age not in ?1</td>
        </tr>
        <tr>
            <td>TRUE</td>
            <td>findByActiveTrue</td>
            <td>… where x.active = true</td>
        </tr>
        <tr>
            <td>FALSE</td>
            <td>findByActiveFalse</td>
            <td>… where x.active = false</td>
        </tr>
        <tr>
            <td>IgnoreCase</td>
            <td>findByNameIgnoreCase</td>
            <td>… where UPPER(x.name) = UPPER(?1)
            </td>
        </tr>
    </tbody>
</table>

### 分页排序查询
分页查询的意义就不多说了，大家都明白，如果确定数据量小的情况可以选择前端分页，
如果数据量比较大或者数据量一直增加并在未来可能会增加到比较大的数据量(比如用户量)，最好还是后端支持分页排序查询。

Spring Boot Jpa 已经帮我们实现了分页的功能，需要定义一个继承PagingAndSortingRepository的接口，传入参数Pageable就可以实现分页。

修改UserRepository
```java
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByName(String name, Pageable pageable);
}
```
修改UserService
```java

public interface UserService {
    List<User> getUserPage(Integer pageNumber, Integer pageSize);
    List<User> getUserPageBySort(Integer pageNumber, Integer pageSize);
}
```
修改UserServiceTemplate
```java
@Service("UserService")
public class UserServiceTemplate implements UserService {
    // 注入JPA仓库对象
    @Resource
    UserRepository userRepository;

    @Override
    public List<User> getUserPage(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> userPage = userRepository.findAll(pageable);
        
        int totalPage = userPage.getTotalPages();
        Long totalCount = userPage.getTotalElements();
        System.out.println("totalPage" + totalPage);
        System.out.println("totalCount" + totalCount);
        
        List<User> userList = userPage.toList();
        return userList;
    }

    @Override
    public List<User> getUserPageBySort(Integer pageNumber, Integer pageSize) {

        Order order1 = new Order(Sort.Direction.ASC, "password");
        Order order2 = new Order(Sort.Direction.DESC, "name");

        List<Order> orderList = new ArrayList<>();
        orderList.add(order1);
        orderList.add(order2);

        Sort sort = Sort.by(orderList);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> userList = userPage.toList();
        return userList;
    }
}
```
getUserPage：根据pageNumber和pageSize参数定义一个Pageable对象，Pageable对象
传给userRepository.findAll，最后使用Page提供的toList方法转为List对象返回Controller，
Page还提供了getTotalPages()方法和getTotalElements()方法，用来获取总页数和总数据条数。

getUserPageBySort：首先创建了两个Order对象，order1是根据password字段升序排序，
order2根据name字段降序排序，然后分别添加到一个List对象，注意添加顺序，先添加的优先权高，
最后会先根据order1排序规则然后再根据order2排序规则返回数据。

先往数据库user表中添加12条数据，重启项目，浏览器访问
[http://localhost:8080/user/getUserPage?pageNumber=0&pageSize=5](http://localhost:8080/user/getUserPage?pageNumber=0&pageSize=5)
看到下图没有排序的数据：
![image](http://q21ledx2j.bkt.clouddn.com/example:springboot-jpa:1.png)
访问[http://localhost:8080/user/getUserPageBySort?pageNumber=0&pageSize=5](http://localhost:8080/user/getUserPageBySort?pageNumber=0&pageSize=5)
会看到按照定义好的排序规则排序后的数据：
![image](http://q21ledx2j.bkt.clouddn.com/example:springboot-jpa:2.png)

### 自定义SQL查询

修改UserRepository，添加findUserByIdWithQuery方法
```java
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByName(String name, Pageable pageable);

    @Query("select u from User u where u.id = ?1")
    User findUserByIdWithQuery(Integer id);
    
    @Modifying
    @Query("delete from User where id = ?1")
    Boolean deleteUserByIdWithQuery(Long id);
}
```
使用@Query可以自定义查询语句，如果涉及到修改和删除数据，还需要再添加@Modifying注解

修改UserService，添加getUserByIdWithQuery方法
```java
public interface UserService {

    List<User> getUserPage(Integer pageNumber, Integer pageSize);
    List<User> getUserPageBySort(Integer pageNumber, Integer pageSize);

    User getUserByIdWithQuery(Integer id);
}
```

修改UserServiceTemplate，添加getUserByIdWithQuery实现
```java
@Service("UserService")
public class UserServiceTemplate implements UserService {
    ...

    @Override
    public User getUserByIdWithQuery(Integer id) {
        return userRepository.findUserByIdWithQuery(id);
    }
}
```

修改UserController，添加"/getUserByIdWithQuery"路由
```java
@RestController
@RequestMapping("/user")
public class UserController {
    ...

    @GetMapping("/getUserByIdWithQuery")
    public User getUserByIdWithQuery(Integer id) {
        return userService.getUserByIdWithQuery(id);
    }
}
```

重启项目，浏览器访问[http://localhost:8080/user/getUserByIdWithQuery?id=2](http://localhost:8080/user/getUserByIdWithQuery?id=2),
看到下图效果：
![image](http://q21ledx2j.bkt.clouddn.com/example:springboot-jpa:3.png)

#### 具体代码可查看[源码地址](https://github.com/zhuqitao/spring-boot-examples/tree/master/springboot-jpa)，欢迎star


