package com.digitalojt.web.repository;

import com.digitalojt.web.entity.CenterInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * センター情報テーブルリポジトリー
 *
 * @author dotlife
 */
public interface CenterInfoRepository extends JpaRepository<CenterInfo, Integer> {
	/**
	 * 引数に合致する在庫センター情報を取得
	 *
	 * @param centerName
	 * @param region
	 * @return paramで検索した結果
	 */
	@Query(
		"SELECT s FROM CenterInfo s WHERE " +
		"(:centerName = '' OR s.centerName LIKE %:centerName%) AND " +
		"(:region = '' OR s.address LIKE %:region%) AND " +
		"(s.operationalStatus = 0)"
	)
	List<CenterInfo> findActiveCenters(String centerName, String region);
}
