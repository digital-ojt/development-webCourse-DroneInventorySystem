package com.digitalojt.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalojt.web.entity.CategoryInfo;

/*
	部品本カテゴリー情報テーブルリポジトリー

*/

@Repository
public interface PartsCategoryInfoRepository extends JpaRepository<CategoryInfo,Integer>{

	/*	
	 * 論理フラグが 0 の部品カテゴリー情報を取得（ID 昇順）
	*/

	List<CategoryInfo> findByDeleteFlagOrderByCategoryIdAsc(int deleteFlag);

	/*	
	 * 論理フラグが 0 かつ 部品カテゴリー名に合致する部品カテゴリー情報を取得（ID 昇順）
	 * @param categoryName
	 * @return
	*/	
	List<CategoryInfo> findByCategoryNameAndDeleteFlagOrderByCategoryIdAsc(String categoryName, int active);

	/*	
	 * 部品カテゴリー名に合致する部品カテゴリー情報を取得
	 * @param categoryName
	 * @return
	*/	
	
	CategoryInfo getByCategoryName(String categoryName);

}
