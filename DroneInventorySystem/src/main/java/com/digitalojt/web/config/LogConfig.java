package com.digitalojt.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digitalojt.web.filter.LogSettingFilter;

/**
 * ログセッティングフィルターを使用可能にするための設定
 * 
 * @author dotlife
 */
@Configuration
public class LogConfig {
	
	@Bean
	public LogSettingFilter logSettingFilter() {
		return new LogSettingFilter();
	}
}