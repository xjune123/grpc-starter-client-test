package com.grpc.test.grpcstarterclienttest.controller;

import com.grpc.test.grpcstarterclienttest.service.FeignTestClient;
import com.grpc.test.grpcstarterclienttest.service.TestPerformanceGrpcService;
import com.test.grpc.performance.TestReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Demo class
 *
 * @author junqiang.xiao@hand-china.com
 * @date 2018/5/7
 */
@RestController
public class TestController {

    @Autowired
    TestPerformanceGrpcService testPerformanceGrpcService;
    @Autowired
    FeignTestClient feignTestClient;

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping("/test")
    ResponseEntity test(HttpServletRequest request) {
        String tenantId = request.getHeader("X-TenantId");
        TestReply testReply = testPerformanceGrpcService.sendMessage("Hello",tenantId);

        if (testReply != null) {
            return new ResponseEntity<TestReply>(testReply, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

    }

    @RequestMapping("/sendHystrixMessage")
    ResponseEntity sendHystrixMessage(HttpServletRequest request) {
        String tenantId = request.getHeader("X-TenantId");
        TestReply testReply = testPerformanceGrpcService.sendHystrixMessage("Hello",tenantId);

        if (testReply != null&& !Objects.equals(testReply.getField2(),-1) ) {

            return new ResponseEntity<TestReply>(testReply, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping("/testFeign")
    ResponseEntity testFeign() {
        String response = feignTestClient.requestMessage("world");
        if (response != null) {
            return new ResponseEntity<String>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

    }
}
