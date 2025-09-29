package com.example.jwt.service;

import com.example.jwt.dto.CarModelQueryRequest;
import com.example.jwt.dto.CarModelQueryResponse;
import com.example.jwt.util.ApiTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CarModelQueryService {

    private final WebClient webClient;
    private final ApiTestUtil apiTestUtil;

    @Autowired
    public CarModelQueryService(WebClient webClient, ApiTestUtil apiTestUtil) {
        this.webClient = webClient;
        this.apiTestUtil = apiTestUtil;
    }

    /**
     * 查询车辆模型信息
     * @param request 查询请求
     * @return 查询结果
     */
    public Mono<CarModelQueryResponse> queryCarModelInfo(CarModelQueryRequest request) {
        String url = "https://esales.bpic.com.cn/app-carmodelinfo/netins/jyPreCarModelFill/queryJYPreCarModelFill";

        // 设置Cookie头
        String cookie = "HTTPOnly; Cookie=!0VkxXrX6EGD8RKm1yv2qnldjEk8Kb4nbpNTUAWt1e2/qKNUYrDRuqgB5+CW9eS3wgIIaojCa2nOlY6E=; JSESSIONID=0001wM7umNbFG8YByUvwoQOteLh:1OKKVUUFVE";

        log.info("开始调用车辆模型查询接口，URL: {}", url);
        log.info("请求参数: {}", request);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.COOKIE, cookie)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                //.bodyValue(request)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(CarModelQueryResponse.class)
                .doOnSuccess(response -> {
                    log.info("接口调用成功，响应码: {}, 消息: {}", response.getCode(), response.getMsg());
                    // 记录完整的响应结构用于调试
                    //apiTestUtil.logResponseStructure(response);
                })
                .doOnError(error -> {
                    log.error("接口调用异常: {}", error.getMessage(), error);
                });
    }

    /**
     * 便捷方法：使用默认参数查询车辆模型信息
     * @param vin 车辆识别码
     * @param plateNumber 车牌号
     * @return 查询结果
     */
    public Mono<CarModelQueryResponse> queryCarModelInfo(String vin, String plateNumber) {
        CarModelQueryRequest request = createDefaultRequest(vin, plateNumber);
        return queryCarModelInfo(request);
    }

    /**
     * 创建默认请求对象
     * @param vin 车辆识别码
     * @param plateNumber 车牌号
     * @return 请求对象
     */
    private CarModelQueryRequest createDefaultRequest(String vin, String plateNumber) {
        CarModelQueryRequest.RequestHead head = new CarModelQueryRequest.RequestHead("", "", "");
        CarModelQueryRequest.RequestData data = new CarModelQueryRequest.RequestData(
                "14070201",
                "004",
                vin,
                plateNumber,
                "0",
                "02",
                "01",
                "长安牌SC7143C",
                "114050281"
        );
        return new CarModelQueryRequest(head, data);
    }
}