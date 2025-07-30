package com.regisx001.validationsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
    // This configuration ensures that Page objects are properly serialized
    // using DTOs instead of the internal PageImpl structure
}
