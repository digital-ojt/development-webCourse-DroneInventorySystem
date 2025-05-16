package com.digitalojt.web.service;

import java.util.List;

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
	 * @return
	 */
	public List<CenterInfo> getCenterInfoData(String centerName, String region,Integer storageCapacityFrom,Integer storageCapacityTo) {
		return repository.findActiveCenters(centerName, region, storageCapacityFrom, storageCapacityTo);
	}
}
