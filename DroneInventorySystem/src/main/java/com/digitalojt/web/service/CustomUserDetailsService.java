package com.digitalojt.web.service;

import com.digitalojt.web.consts.RoleConstants;
import com.digitalojt.web.repository.AdminInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * ユーザー情報生成
 *
 * @author dotlife
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	// 管理者情報リポジトリー
	private final AdminInfoRepository repository;

	/**
	 * ユーザー情報生成
	 *
	 * @param ログインID
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!isValidAdminId(username)) {
			log.error("ログインIDが不正です: {}", username);
			throw new UsernameNotFoundException(username);
		}

		return repository
			.findById(username)
			.map(adminInfo ->
				User
					.withUsername(adminInfo.getAdminId())
					.password(adminInfo.getPassword())
					.roles(RoleConstants.ROLE_USER)
					.build()
			)
			.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	/**
	 * adminIdのバリデーションチェック
	 *
	 * @param 入力されたadminId
	 * @return 条件をすべてクリアした場合はtrue、条件に該当する文字が含まれていればfalse
	 */
	private boolean isValidAdminId(String adminId) {
		if (adminId == null || adminId.trim().isEmpty()) {
			return false;
		}

		// 半角スペースチェック
		if (adminId.contains(" ")) {
			return false;
		}

		// 禁止文字チェック
		String invalidChars = "{}()'*;$=&";
		for (char c : invalidChars.toCharArray()) {
			if (adminId.indexOf(c) >= 0) {
				return false;
			}
		}

		return true;
	}
}
