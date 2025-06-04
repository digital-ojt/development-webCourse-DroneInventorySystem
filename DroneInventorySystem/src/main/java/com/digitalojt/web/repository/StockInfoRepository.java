package com.digitalojt.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitalojt.web.entity.StockInfo;

/**
 * 
 * 部品カテゴリー情報テーブルリポジトリー
 *
 * @author lin
 * 
 */
@Repository
public interface StockInfoRepository extends JpaRepository<StockInfo, Integer> {

	/**
	 * 論理削除フラグが 0 の在庫情報リストを取得（ID 昇順）
	 * 
	 * @param deleteFlag
	 * @return 在庫情報リスト
	 */
    List<StockInfo> findByDeleteFlagOrderByStockIdAsc(int deleteFlag);

    
    /**
     * 論理削除フラグが 0 
     * かつ 部品カテゴリーに合致 
     * かつ 名称で曖昧検索 
     * かつ 数量条件にに合致する在庫情報リストを取得（ID 昇順）
     * 
     * @param category
     * @param name
     * @param amount
     * @param amountCondition
     * @param deleteFlag
     * @return 在庫情報リスト
     */
    @Query("SELECT s "
    		+ "FROM StockInfo s "
    		+ "WHERE (:categoryId IS NULL OR s.categoryInfo.categoryId = :categoryId) "
    		+ "AND s.name LIKE %:name% "
    		+ "AND (:amount IS NULL "
    		+ "OR (:amountCondition = 'greater' AND s.amount >= :amount OR :amountCondition = 'less' "
    		+ "AND s.amount <= :amount)) "
    		+ "AND s.deleteFlag = :deleteFlag "
    		+ "ORDER BY s.stockId ASC")
    List<StockInfo> findStockList(
            @Param("categoryId") Integer categoryId,
            @Param("name") String name,
            @Param("amount") Integer amount,
            @Param("amountCondition") String amountCondition,
    		@Param("deleteFlag") int deleteFlag);
	
}
