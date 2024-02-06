# 更新日志

## 1.2.2
#### honoka-android-utils 1.0.2
- 引入OrmLite库，以支持通过ORM方式使用SQLite数据库。
- 添加`DatabaseHelper`与`BaseDao`对OrmLite进行封装，通过继承`BaseDao`类，并传入一个实体类的`Class`对象的方式，即可快速创建基于该实体类的一个数据库访问对象，不必考虑数据表的创建、升级和降级。

## 1.2.1
#### honoka-android-utils 1.0.1
- 支持快速创建和启动内部HTTP服务器，根据请求URL匹配和获取`[assets]/web`目录下的静态资源，也支持获取应用私有目录下的图片。
- 支持异步JavaScript Interface，避免WebView中的JS代码调用Android层方法时产生线程阻塞和UI无响应。
- `GlobalData`更名为`GlobalComponents`。

## 1.2.0
#### honoka-android-utils 1.0.0
- 初始版本。包含`GlobalData`与`java.beans.Transient`。

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
- 起始版本

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
- 起始版本

#### honoka-json-gson 1.0.0
- 起始版本

#### honoka-json-api 1.0.0
- 起始版本

#### honoka-utils 1.0.0
- 起始版本
