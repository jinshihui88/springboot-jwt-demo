package com.example.jwt.controller;

import com.example.jwt.dto.ApiResponse;
import com.example.jwt.dto.CarModelQueryRequest;
import com.example.jwt.dto.CarModelQueryResponse;
import com.example.jwt.service.CarModelQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/car-model")
@Tag(name = "车辆模型查询", description = "车辆模型信息查询接口")
@Slf4j
public class CarModelQueryController {

    private final CarModelQueryService carModelQueryService;

    @Autowired
    public CarModelQueryController(CarModelQueryService carModelQueryService) {
        this.carModelQueryService = carModelQueryService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询车辆模型信息", description = "调用外部接口查询车辆模型详细信息")
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<ResponseEntity<ApiResponse>> queryCarModelInfo(@RequestBody CarModelQueryRequest request) {
        log.info("接收到车辆模型查询请求: {}", request);

        return carModelQueryService.queryCarModelInfo(request)
                .map(response -> {
                    ApiResponse apiResponse = ApiResponse.success("查询成功", response);
                    return ResponseEntity.ok(apiResponse);
                })
                .onErrorResume(error -> {
                    log.error("车辆模型查询失败: {}", error.getMessage(), error);
                    ApiResponse apiResponse = ApiResponse.error("查询失败: " + error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                });
    }

    @GetMapping("/query/simple")
    @Operation(summary = "简化查询车辆模型信息", description = "使用车牌号和VIN码查询车辆模型信息")
    public Mono<ResponseEntity<ApiResponse>> queryCarModelInfoSimple(
            @RequestParam String vin,
            @RequestParam String plateNumber) {
        log.info("接收到简化车辆模型查询请求 - VIN: {}, 车牌号: {}", vin, plateNumber);

        return carModelQueryService.queryCarModelInfo(vin, plateNumber)
                .map(response -> {
                    ApiResponse apiResponse = ApiResponse.success("查询成功", response);
                    return ResponseEntity.ok(apiResponse);
                })
                .onErrorResume(error -> {
                    log.error("简化车辆模型查询失败: {}", error.getMessage(), error);
                    ApiResponse apiResponse = ApiResponse.error("查询失败: " + error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                });
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查车辆模型查询服务是否正常")
    public Mono<ResponseEntity<ApiResponse>> healthCheck() {
        log.info("车辆模型查询服务健康检查");

        // 创建一个简单的测试请求来检查服务是否正常
        CarModelQueryRequest testRequest = new CarModelQueryRequest();
        CarModelQueryRequest.RequestHead head = new CarModelQueryRequest.RequestHead("", "", "");
        CarModelQueryRequest.RequestData data = new CarModelQueryRequest.RequestData(
                "14070201",
                "004",
                "LS5A33LR9GB320680",
                "晋AP535C",
                "0",
                "02",
                "01",
                "长安牌SC7143C",
                "114050281"
        );
        testRequest.setHead(head);
        testRequest.setData(data);

        return carModelQueryService.queryCarModelInfo(testRequest)
                .map(response -> {
                    ApiResponse apiResponse = ApiResponse.success("服务正常", "车辆模型查询服务运行正常");
                    return ResponseEntity.ok(apiResponse);
                })
                .onErrorResume(error -> {
                    ApiResponse apiResponse = ApiResponse.error("服务异常: " + error.getMessage());
                    return Mono.just(ResponseEntity.status(503).body(apiResponse));
                });
    }
}