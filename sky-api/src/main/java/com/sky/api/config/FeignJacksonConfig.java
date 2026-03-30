package com.sky.api.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.sky.json.JacksonObjectMapper;

import feign.codec.Decoder;

@Configuration
public class FeignJacksonConfig {

  @Bean
  /**
   * 作用: 执行feignDecoder相关逻辑。
   * 输入: 无。
   * 输出: Decoder。
   */
  public Decoder feignDecoder() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new JacksonObjectMapper());
    ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(converter);
    return new SpringDecoder(objectFactory);
  }
}
