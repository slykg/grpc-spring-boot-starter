package net.devh.springboot.autoconfigure.grpc.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnBean(ConsulAutoRegistration.class)
@AutoConfigureAfter(ConsulAutoServiceRegistrationAutoConfiguration.class)
public class GrpcMetedataConsulConfiguration {

    @Autowired
    private GrpcServerProperties grpcProperties;

    @Bean
    public ConsulRegistrationCustomizer grpcConsulRegistrationCustomizer() {
        return new ConsulRegistrationCustomizer() {
            @Override
            public void customize(ConsulRegistration registration) {
                // put grpc port into registration service's tag list, which will later
                // be converted as service instance's metadata
                List<String> tags = registration.getService().getTags();
                if (tags == null) {
                    tags = new LinkedList<>();
                    registration.getService().setTags(tags);
                }

                tags.add("grpc-port=" + grpcProperties.getPort());
            }
        };
    }

}
