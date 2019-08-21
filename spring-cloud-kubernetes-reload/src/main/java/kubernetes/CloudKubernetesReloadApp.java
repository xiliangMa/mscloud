package kubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Create by $(xiliangMa) on 2019-08-20
 */

@SpringBootApplication
public class CloudKubernetesReloadApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudKubernetesReloadApp.class, args);
    }
}
