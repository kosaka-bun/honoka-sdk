# 更新日志

## 2.1.1
#### honoka-utils 1.1.2
- 实现了`NewThreadFirstQueue`，并新增了`ThreadPoolUtils`，以创建先达到最大线程数再将任务放入阻塞队列中的线程池。
- 优化了`ImageUtils`中关于文字换行的策略。
- 优化了`FileUtils.getMainClasspath()`方法。
- 新增了`LockUtils`，以在若干个对象上启用`synchronized`并执行代码块。

#### honoka-kotlin-utils 1.1.1
- 移除了`net.proxy`与`net.socket`包下除了`SocketUtils`以外的所有工具类。
- 新增了`ScheduledTask`，以快速创建基于`ScheduledThreadPoolExecutor`的具有固定延迟（`scheduleWithFixedDelay`）的计划任务。
- 为`Result<T>`新增了一些有关日志记录的扩展方法。

#### honoka-spring-boot-starter 1.0.2
- 重构了`GlobalExceptionHandler`及其相关的方法。
- 优化了`MybatisPlusConfig`中关于判断数据库类型的逻辑。
- 新增了`QueryWrapper.updateChainWrapper()`。

## 2.1.0
#### honoka-utils 1.1.1
- 将code包重命名为basic。
- 新增了一些文档注解（`@ThreadSafe`、`@NotThreadSafe`）。

#### honoka-kotlin-utils 1.1.0
- 将code包重命名为basic。
- 实现了`ProxyPool`，可自动搜集、更新、检查多个来源的免费HTTPS代理。
- 实现了`SocketForwarder`，可实现Socket连接中转，作为中间服务交换客户端与远程目标间传输的数据，支持在客户端连接时从多个远程目标服务中随机选择其中一个。
- 实现了`NioSocketClient`，可快速实现基于非阻塞IO并支持发起多个连接的Socket客户端。
- 实现了`ByteBufferIoStream`，可作为中间缓存数据流使用，既可向其中写入字节数据，也可从中读取指定数量的字节数据，同时也支持转换为`InputStream`或`OutputStream`（均由`ByteBufferIoStream`直接代理读写）供其他类使用。
- 实现了`PropertyValueContainer`，用于支持在对象外部存储对象的某些属性值，通常用于在interface中定义可直接赋予初始值的属性。

#### honoka-spring-boot-starter 1.0.1
- 优化了security模块的一些实现。
- 新增了mybatis模块。

## 2.0.0
#### 工程
- 移除honoka-framework-utils与honoka-json子项目。
- 引入honoka-spring-boot-starter子项目，最低支持Java 17。
- 需要引入Kotlin的项目统一采用Kotlin 1.8.10与Kotlin Coroutines 1.6.4版本。

#### honoka-utils 1.1.0
- 更改了一些类的包路径。
- 移除了一些工具包依赖，使用hutool作为代替。
- 适配hutool 5.8.25版本。

#### honoka-kotlin-utils 1.0.1
- 新增了一些扩展函数。
- 实现`JsonWrapper`，用于快速根据JSON路径获取指定类型的值。

#### honoka-spring-boot-starter 1.0.0
- 初始版本，最低支持Spring Boot 3.2.5版本。
- 迁移原honoka-framework-utils项目中的大部分功能作为core包，并新增了一些工具类。
- 实现security包，用于快速实现基于JWT的单体应用的登录认证功能。

## 1.3.0
#### 工程
- 移除honoka-android-utils子项目，改造为独立的工程。

#### honoka-utils 1.0.11
- 调整工程依赖项配置，解决xml-apis库与pull-parser库的依赖冲突问题。

#### honoka-kotlin-utils 1.0.0
- 起始版本。包含`PartialAbstract`、`XmlUtils`，以及对hutool库与Collection的一些扩展方法。

## 1.2.2
原有子项目honoka-android-utils 1.0.2版本相关更新。

## 1.2.1
原有子项目honoka-android-utils 1.0.1版本相关更新。

## 1.2.0
原有子项目honoka-android-utils 1.0.0版本相关更新。

## 1.1.10
#### honoka-utils 1.0.10
- 移除`DefaultEnvironmentPathUtils`。
- 优化`FileUtils`中的部分注释和方法定义。

#### honoka-framework-utils 1.0.4
- 添加`AbstractEmbeddedDatabaseUtils`，借助`AbstractEnvironmentPathUtils`快速拼接嵌入式数据库的JDBC URL。
- 移除过时的Hibernate相关类。

## 1.1.9
#### honoka-utils 1.0.9
- `FileUtils`
  - 支持判断当前运行的Java应用程序是否位于JAR包中。
  - `getClasspath`方法更名为`getMainClasspath`，并优化了当Java应用程序在JAR包中被运行时获取主classpath的逻辑。
- `AbstractEnvironmentPathUtils`、`DefaultEnvironmentPathUtils`
  - 某些应用可能会预先定义一些用于存放外部文件的基础路径，比如某些应用会自定义一个采用相对路径的工作目录，用于存放程序在运行时要输出的文件。应用在IDE中直接运行时，我们可能希望这个相对路径是`./[工程编译目录（如target或build）]/data/out`，而在JAR包中被运行时，我们可能希望这个路径是`./data/out`。可以通过继承`AbstractEnvironmentPathUtils`的方式来定义一些在不同环境下会返回不同路径的方法，来实现上述需求。

## 1.1.8
#### honoka-framework-utils 1.0.3
- 添加`ApiException`。

## 1.1.7
#### honoka-utils 1.0.8
- 为`ConsoleWindow`添加新的`init`方法，以实现在不创建托盘图标的情况下，能够执行退出动作。
- 为`ConsoleWindowBuilder`添加一些意义更为明确的配置项。

#### honoka-framework-utils 1.0.2
- 添加`SpringBootConsoleWindow`类，可实现在Spring Boot应用启动前，配置、创建和显示`ConsoleWindow`，然后根据指定的主类启动Spring Boot应用，并管理相应的`ApplicationContext`。

## 1.1.6
#### honoka-json-api 1.1.3
- 为`JsonObject`与`JsonObjectService`增加新的`of(Object obj)`方法。
- 为`JsonArray`与`JsonArrayService`增加新的`of(Collection<?> collection, Class<T> clazz)`方法。

#### honoka-json-gson 1.1.3
- 适配honoka-json-api 1.1.3。

#### honoka-json-fastjson 1.1.3
- 适配honoka-json-api 1.1.3。

## 1.1.5
#### honoka-utils 1.0.7
- 更新`CsvTable`API，增加泛型功能。
- 增加`DefaultCsvTable`。
- `ReflectUtils`增加`newInstance`方法。

## 1.1.4
#### honoka-utils 1.0.6
- 增强和优化`ColorAttributeSets`，现在可以缓存各个ANSI代码的`AttributeSet`和`Color`。
- 增加`ColorfulOutputStream`，通过HTML格式保存控制台输出的彩色内容。
- 优化`ConsoleOutputStream`。

## 1.1.3
#### honoka-utils 1.0.5
- 修改`ConsoleWindow`的初始化逻辑，移除公共构造器。
- 修改`ConsoleWindow`部分字段的访问权限。
- 创建`ConsoleWindowBuilder`以构建`ConsoleWindow`实例。
- 移除`ConsoleWindow.setTrayIconMenuLocationOffset()`方法。

## 1.1.2
#### honoka-utils 1.0.4
- 添加`ThrowsConsumer`接口。

#### honoka-framework-utils 1.0.1
- 将模块依赖改为指定版本依赖。

#### honoka-json-api 1.1.2
- 将模块依赖改为指定版本依赖。

#### honoka-json-gson 1.1.2
- 将模块依赖改为指定版本依赖。

#### honoka-json-fastjson 1.1.2
- 将模块依赖改为指定版本依赖。

## 1.1.1
#### honoka-utils 1.0.3
- 移除`CodeUtils.doIgnoreExceptions()`，用`ActionUtils.doIgnoreException()`代替。
- 移除`web`与`framework`包，将其独立到honoka-framework-utils模块中。

#### honoka-framework-utils 1.0.0
- 起始版本。

#### honoka-json-api 1.1.1
- 更新了`JsonObject`类中部分方法所使用的内部API。

#### honoka-json-fastjson 1.1.1
- 更新版本号。

#### honoka-json-gson 1.1.1
- 更新版本号。

## 1.1.0
#### honoka-json-fastjson 1.1.0
- 使用SPI重写对API的实现。
- 实现公共配置类的回调接口，用户在通过公共配置类进行配置时，实现框架可收到回调。

#### honoka-json-gson 1.1.0
- 使用SPI重写对API的实现。
- 实现公共配置类的回调接口，用户在通过公共配置类进行配置时，实现框架可收到回调。

#### honoka-json-api 1.1.0
- 使用SPI重写`JsonObject`、`JsonArray`等接口。
- 添加了`JsonConfig`、`JsonConfigCallback`等公共配置类。

## 1.0.2
#### honoka-utils 1.0.2
- 优化`FileUtils`，去除Java 9+的API。
- `FileUtils.checkResources()`更名为`copyResourceIfNotExists()`。
- `FileUtils.copyResourceIfNotExists()`现在不会要求jar包内必须包含指定的资源。

## 1.0.1
#### honoka-utils 1.0.1
- 将原属于qqrobot-spring-boot-starter的`ImageUtils`类移到本库中。

## 1.0.0
#### honoka-json-fastjson 1.0.0
- 起始版本。

#### honoka-json-gson 1.0.0
- 起始版本。

#### honoka-json-api 1.0.0
- 起始版本。

#### honoka-utils 1.0.0
- 起始版本。
