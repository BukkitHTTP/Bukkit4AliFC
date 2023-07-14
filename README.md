# Bukkit4AliFC 帮助文档

将BukkitHTTP项目无缝迁移到阿里云函数计算。

## 入门教程

1，请从Release中下载最新版本的jar包。  
2，使用本jar包替代您原版的BukkitHTTP作为项目依赖。  
3，按照BukkitHTTP的标准进行开发和编写描述文件。  
4，将描述文件添加到jar包的根目录下，并将jar包命名为task.jar。  
5，将task.jar添加到Bukkit4AliFC.jar的根目录下。  
6，选择通过 JAR 包上传代码，上传修改后的Bukkit4AliFC.jar。  
至此，您的函数计算项目已经完成了部署。

## 变化列表

您可能需要注意，以下基本组件在FC中不可用：

- 数据库组件。阿里云函数计算不提供持久化的文件系统，因此数据库组件无法使用。
    - 替代方案：使用MySQL或者Mongo等云数据库。
- Console组件。阿里云函数计算不提供控制台，因此Console组件无法使用。
    - 替代方案：无。
- Debug组件。为了与FC接轨，本版本的Bukkit不包含HTTP协议栈的实现。
    - 替代方案：先使用标准版本的Bukkit进行开发，然后再使用本工具进行部署。

以下组件的行为虽然没有变化，但是由于阿里云函数计算的特性，可能会有一些不同的表现：

- Session组件。Session组件可能会在函数计算的生命周期内一直保持有效。
    - 但是，Session组件不会在函数计算的生命周期外保持有效。
    - 如果开启了负载均衡，那么Session组件可能会因为负载均衡的特性而失效甚至异常。

尽管文档中注明了不要使用，但是仍然在以下列出内部组件的变更供高级用户查阅。

- Core部分除了Response类以外的所有类都已经被删除。
- 取消了SocksHook和FileHook。

## 其他特性说明

1，您可能已经注意到，阿里云的日志功能并不是免费的。  
因此，本工具提供了备选的日志方案：访问/logs即可查看日志。  
但是，日志存储在内存中，因此请务必不要产生不必要的日志。

2，onDisable事件在函数计算中不会被触发。

## 编译说明

请使用mvn clean package进行编译。

## 问题反馈

请打开一个Issue进行反馈。