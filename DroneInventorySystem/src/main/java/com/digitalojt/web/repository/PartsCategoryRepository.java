package com.digitalojt.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalojt.web.entity.CategoryInfo;

/**
 * 
 * 部品カテゴリー情報テーブルリポジトリー
 *
 * @author dotlife
 * 
 */
@Repository
public interface PartsCategoryRepository extends JpaRepository<CategoryInfo, Integer> {
	
	/**
	 * 論理削除フラグが 0 の部品カテゴリー情報を取得（ID 昇順）
	 * 
	 * @param deleteFlag
	 * @return 部品カテゴリーリスト
	 */
    List<CategoryInfo> findByDeleteFlagOrderByCategoryIdAsc(int deleteFlag);

    /**
     * 論理削除フラグが 0 かつ 部品カテゴリー名に合致する部品カテゴリー情報を取得（ID 昇順）
     * 
     * @param categoryName
     * @param active
     * @return 部品カテゴリーリスト
     */
	List<CategoryInfo> findByCategoryNameAndDeleteFlagOrderByCategoryIdAsc(String categoryName, int deleteFlag);

	/**
	 * 部品カテゴリー名に合致する部品カテゴリー情報を取得
	 * 
	 * @param categoryName
	 * @return 部品カテゴリー
	 */
	CategoryInfo getByCategoryName(String categoryName);
}
