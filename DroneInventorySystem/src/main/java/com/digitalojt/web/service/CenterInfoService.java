package com.digitalojt.web.service;

import com.digitalojt.web.entity.CenterInfo;
import com.digitalojt.web.repository.CenterInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 在庫センター情報画面のサービスクラス
 *
 * @author dotlife
 */
@Service
@RequiredArgsConstructor
public class CenterInfoService {

	/** センター情報テーブル リポジトリー */
	private final CenterInfoRepository repository;

	/**
	 * 在庫センター情報を全建検索で取得
	 *
	 * @return
	 */
	public List<CenterInfo> getCenterInfoData() {
		return repository.findAll();
	}

	/**
	 * 引数に合致する在庫センター情報を取得
	 *
	 * @param centerName
	 * @param region
	 * @return
	 */
	public List<CenterInfo> getCenterInfoData(String centerName, String region) {
		return repository.findActiveCenters(centerName, region);
	}
}
