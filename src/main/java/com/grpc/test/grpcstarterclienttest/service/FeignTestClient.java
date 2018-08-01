package com.grpc.test.grpcstarterclienttest.service;

import com.test.grpc.performance.HeaderReply;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * feign class
 *
 * @author junqiang.xiao@hand-china.com
 * @date 2018/4/24
 */

@FeignClient(name="GRPC-SERVER-TEST")
public interface FeignTestClient {
    @RequestMapping(value = "/reply", method = POST)
    String requestMessage(@RequestParam("name") String name);
}
