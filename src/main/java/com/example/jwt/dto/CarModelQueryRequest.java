package com.example.jwt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarModelQueryRequest {

    private RequestHead head;
    private RequestData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestHead {
        private String channelCode;
        private String comCode;
        private String transDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestData {
        private String comCode;
        private String resourceTypeCode;
        private String vin;
        private String plateNumber;
        private String newCarFlag;
        private String plateNoType;
        private String plateNoColor;
        private String vehicleModels;
        private String operatorCode;
    }
}