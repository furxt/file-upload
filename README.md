这是服务端通用的简单分片上传接口功能，支持springboot2和3；springboot2请使用[spring-boot-file-upload-starter](spring-boot-file-upload-starter)
，springboot3请使用
[spring-boot3-file-upload-starter](spring-boot3-file-upload-starter)。

git clone 到本地，然后自行使用maven进行编译，springboot2建议用JDK8进行编译安装到本地，springboot3建议用JDK17。

支持配置文件智能提示，请自行在application.yml中添加如下内容：

```yaml
ly:
  file-upload:
    enabled: true # 是否启用，默认false不启用；如果要启用，请设置为true
    local-storage-path: /xxx/xxx # 本地存储路径
    url-prefix: xxx # 接口路径的前缀，防止与项目其他接口路径地址冲突
```

具体的接口文档待后续完善...
