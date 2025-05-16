package com.digitalojt.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digitalojt.web.entity.CenterInfo;

/**
 * センター情報テーブルリポジトリー
 *
 * @author dotlife
 * 
 */
public interface CenterInfoRepository extends JpaRepository<CenterInfo, Integer> {

	/**
	 * 引数に合致する在庫センター情報を取得
	 * 2025/05/16 機能改修 現在容量範囲が検索できるため、検索項目引数を追加する
	 * 2025/05/16 機能改修 現在容量範囲が検索できるため、検索検索条件を追加する
	 * 
	 * @param centerName
	 * @param region
	 * @return paramで検索した結果
	 */
	@Query("SELECT s FROM CenterInfo s WHERE " +
			"(:centerName = '' OR s.centerName LIKE %:centerName%) AND " +
			"(:region = '' OR s.address LIKE %:region%) AND " +
			"(s.currentStorageCapacity >= :storageCapacityFrom) AND " +
			"(s.currentStorageCapacity <= :storageCapacityTo) AND " +
			"(s.operationalStatus = 0)")
	List<CenterInfo> findActiveCenters(
			String centerName,
			String region,
			Integer storageCapacityFrom,
			Integer storageCapacityTo);

}
