package com.grpc.test.grpcstarterclienttest.config;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

/**
 * Demo class
 *
 * @author junqiang.xiao@hand-china.com
 * @date 2018/5/22
 */
@Data
public class CustomTenantList {
    String tenantCode;
    private Map<String, String> serviceList = new TreeMap<>(
            String.CASE_INSENSITIVE_ORDER);


}
