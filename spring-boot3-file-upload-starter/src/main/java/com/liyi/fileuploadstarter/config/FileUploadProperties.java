package com.liyi.fileuploadstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liyi
 */
@ConfigurationProperties(prefix = "ly.file-upload")
public class FileUploadProperties {
    /**
     * 公钥路径, 默认是 classpath:file-upload-public-key.pem(classpath:可以不用带), 如果是jar包外的文件请传入绝对路径
     */
    private String publicKeyPath = "file-upload-public-key.pem";
    /**
     * 私钥路径, 默认是 classpath:file-upload-private-key.pem(classpath:可以不用带), 如果是jar包外的文件请传入绝对路径
     */
    private String privateKeyPath = "file-upload-private-key.pem";
    /**
     * controller 访问路径前缀, 默认是 ly, 配置时 / 可要可不要
     */
    private String urlPrefix = "ly";
    /**
     * 本地存储路径, 默认是用户的临时目录
     */
    private String localStoragePath = System.getProperty("java.io.tmpdir");
    /**
     * 是否启用, 默认是 false, 不激活启用
     */
    private boolean enabled = false;

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getLocalStoragePath() {
        return localStoragePath;
    }

    public void setLocalStoragePath(String localStoragePath) {
        this.localStoragePath = localStoragePath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
}
