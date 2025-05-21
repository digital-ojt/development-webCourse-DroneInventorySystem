package com.digitalojt.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.digitalojt.web.entity.CenterInfo;
import com.digitalojt.web.repository.CenterInfoRepository;

import lombok.RequiredArgsConstructor;
/**
 * 在庫センター情報画面のサービスクラス
 *
 * @author dotlife
 * 
 */
@Service
@RequiredArgsConstructor
public class CenterInfoService {
	
	/** 2025/05/21 ロガー定義 */
	private static final Logger logger = LoggerFactory.getLogger(CenterInfoService.class);

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
	 * 2025/05/16 機能改修 現在容量範囲が検索できるため、検索項目引数を追加する
	 * 
	 * @param centerName
	 * @param region 
	 * @return 条件に一致した在庫センター情報のリストを返す
	 */
	public List<CenterInfo> getCenterInfoData(
			String centerName,
			String region,
			// 2025/05/21 現在容量Fromと現在容量Toのパラメータを追加する
			Integer storageCapacityFrom,
			Integer storageCapacityTo) {
		
		// 2025/05/21 ログ出力：現在容量の範囲（From-To）が指定された場合にログ出力
	    // 2025/05/21 null チェックを行うことで、未指定の場合でもログに「未指定」と記録される
	    // 2025/05/21 検索条件が正しく渡されているか、あとからログで検証しやすくするため
		if (storageCapacityFrom != null || storageCapacityTo != null) {
			logger.info("検索パラメータ - 現在容量範囲: From={}, To={}",
					storageCapacityFrom != null ? storageCapacityFrom : "未指定",
					storageCapacityTo != null ? storageCapacityTo : "未指定");
		}else {
			
			// 2025/05/21 現在容量の条件が完全に未指定の場合も、ログに明示的に出力しておく
			logger.info("検索パラメータ - 現在容量範囲: 指定なし");
		}
		
		
		return repository.findActiveCenters(
				centerName,
				region,
				// 2025/05/21 現在容量Fromと現在容量Toのパラメータを追加する
				storageCapacityFrom,
				storageCapacityTo);
	}
}
