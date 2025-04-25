package com.digitalojt.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.digitalojt.web.consts.RoleConstants;
import com.digitalojt.web.consts.UrlConsts;

import lombok.RequiredArgsConstructor;

/**
 * WebSecurityConfig
 * Spring Security の設定クラス
 * 
 * @author dotlife
 * 
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    /** パスワードエンコーダー */
    private final PasswordEncoder passwordEncoder;

    /** ユーザー情報取得Service */
    private final UserDetailsService userDetailsService;

    /**
     * Spring Securityのフィルタチェーン定義
     * 
     * @param http HttpSecurityオブジェクト
     * @return セキュリティフィルタチェーン
     * @throws Exception 設定エラー
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(UrlConsts.NO_AUTHENTICATION).permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage(UrlConsts.LOGIN)
                        .usernameParameter(RoleConstants.ADMIN_ID)
                        .defaultSuccessUrl(UrlConsts.STOCK_LIST, true) // 追加: 成功時に強制遷移
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl(UrlConsts.LOGIN)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .authenticationProvider(daoAuthenticationProvider()) // Providerを適用
                .build();
    }

    /**
	 * Provider定義
     * 
	 * @return カスタマイズProvider情報
     */
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
