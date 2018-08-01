package net.devh.springboot.autoconfigure.grpc.client;

import com.grpc.test.grpcstarterclienttest.config.TenantProperties;
import com.grpc.test.grpcstarterclienttest.loadbalance.CustomRoundRobinLoadBalancerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.LoadBalancer;
import io.grpc.util.RoundRobinLoadBalancerFactory;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({GrpcChannelFactory.class,Tracer.class})
public class GrpcClientAutoConfiguration {

    @Autowired
    TenantProperties tenantProperties;

    @ConditionalOnMissingBean
    @Bean
    public GrpcChannelsProperties grpcChannelsProperties() {
        return new GrpcChannelsProperties();
    }

    @Bean
    public GlobalClientInterceptorRegistry globalClientInterceptorRegistry() {
        return new GlobalClientInterceptorRegistry();
    }

    @ConditionalOnMissingBean
    @Bean
    public LoadBalancer.Factory grpcLoadBalancerFactory() {
        return CustomRoundRobinLoadBalancerFactory.getInstance(tenantProperties);
    }

    @ConditionalOnMissingBean(value = GrpcChannelFactory.class, type = "org.springframework.cloud.client.discovery.DiscoveryClient")
    @Bean
    public GrpcChannelFactory addressChannelFactory(GrpcChannelsProperties channels, LoadBalancer.Factory loadBalancerFactory, GlobalClientInterceptorRegistry globalClientInterceptorRegistry) {
        return new AddressChannelFactory(channels, loadBalancerFactory, globalClientInterceptorRegistry);
    }

    @Bean
    @ConditionalOnClass(GrpcClient.class)
    public GrpcClientBeanPostProcessor grpcClientBeanPostProcessor() {
        return new GrpcClientBeanPostProcessor();
    }

    @Configuration
    @ConditionalOnBean(DiscoveryClient.class)
    protected static class DiscoveryGrpcClientAutoConfiguration {

        @ConditionalOnMissingBean
        @Bean
        public GrpcChannelFactory discoveryClientChannelFactory(GrpcChannelsProperties channels, DiscoveryClient discoveryClient, LoadBalancer.Factory loadBalancerFactory,
                                                                GlobalClientInterceptorRegistry globalClientInterceptorRegistry) {
            return new DiscoveryClientChannelFactory(channels, discoveryClient, loadBalancerFactory, globalClientInterceptorRegistry);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "spring.sleuth.enabled", matchIfMissing = true)
    @ConditionalOnClass(Tracer.class)
    protected static class TraceClientAutoConfiguration {
        @Autowired
        Tracer tracer;

        @Bean
        public BeanPostProcessor clientInterceptorPostProcessor(GlobalClientInterceptorRegistry registry) {
            System.out.println(this.tracer);
            //解决调用链问题
            registry.addClientInterceptors(new TraceClientInterceptor(tracer, new MetadataInjector()));

            return new ClientInterceptorPostProcessor(registry);
        }

        private static class ClientInterceptorPostProcessor implements BeanPostProcessor {

            private GlobalClientInterceptorRegistry registry;

            public ClientInterceptorPostProcessor(
                    GlobalClientInterceptorRegistry registry) {
                this.registry = registry;
            }

            @Override
            public Object postProcessBeforeInitialization(Object bean,
                                                          String beanName)
                    throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean,
                                                         String beanName)
                    throws BeansException {
                if (bean instanceof Tracer) {
                    this.registry.addClientInterceptors(new TraceClientInterceptor((Tracer) bean, new MetadataInjector()));
                }

                return bean;
            }
        }
    }

}
