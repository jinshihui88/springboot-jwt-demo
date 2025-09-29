package com.example.jwt.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiTestUtil {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void logResponseStructure(Object response) {
        try {
            String jsonString = objectMapper.writeValueAsString(response);
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            
            log.info("=== 完整响应结构 ===");
            log.info(jsonString);
            
            if (jsonNode.has("data")) {
                JsonNode dataNode = jsonNode.get("data");
                log.info("=== data 字段结构 ===");
                log.info("data 类型: {}", dataNode.getNodeType());
                
                if (dataNode.isObject()) {
                    log.info("data 对象包含的字段:");
                    dataNode.fieldNames().forEachRemaining(fieldName -> {
                        JsonNode fieldValue = dataNode.get(fieldName);
                        log.info("  - {}: {} (类型: {})", fieldName, 
                            fieldValue.isNull() ? "null" : fieldValue.toString().substring(0, Math.min(50, fieldValue.toString().length())), 
                            fieldValue.getNodeType());
                    });
                }
            }
        } catch (Exception e) {
            log.error("解析响应结构失败", e);
        }
    }
}