package com.grpc.test.grpcstarterclienttest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

/**
 * 配置
 *
 * @author junqiang.xiao@hand-china.com
 * @date 2018/5/22
 */
@Data
@Component
@ConfigurationProperties("customtenant")
public class TenantProperties {

    private Map<String, CustomTenantList> customList = new TreeMap<>(
            String.CASE_INSENSITIVE_ORDER);
}


