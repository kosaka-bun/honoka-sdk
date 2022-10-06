# 更新日志

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
