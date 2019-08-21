package kubernetes;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by $(xiliangMa) on 2019-08-21
 */

// 这里需要注意的是  prefix = "config" 对应configmap 中定义的变量
@Configuration
@ConfigurationProperties(prefix = "config")
@RestController
public class MyConfigMap {

    private String message = "update your message...";

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @GetMapping("/config")
    public String GetConfigMap(){
        return this.getMessage();
    }
}