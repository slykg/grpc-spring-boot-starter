package net.devh.springboot.autoconfigure.grpc.server;

import com.netflix.appinfo.EurekaInstanceConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnBean(EurekaInstanceConfig.class)
@AutoConfigureAfter(name = "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration")
public class GrpcMetedataEurekaConfiguration {

    @Autowired
    private EurekaInstanceConfig instance;

    @Autowired
    private GrpcServerProperties grpcProperties;

    @PostConstruct
    public void init() {
        this.instance.getMetadataMap().put("grpc-port", String.valueOf(grpcProperties.getPort()));
    }
}