package com.example.jwt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarModelQueryResponse {

    private Integer code;
    private String msg;
    private Long time;
    //private Object data;

    private CarModelData data;  // 替代 Object data

}