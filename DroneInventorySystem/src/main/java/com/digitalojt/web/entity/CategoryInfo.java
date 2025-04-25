package com.digitalojt.web.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * カテゴリー情報Entity
 * 
 * @author dotlife
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "category_info") 
public class CategoryInfo {
	
	/**
	 * カテゴリーID
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
	
	/**
	 * 名前
	 */
    @Column(name = "category_name")
	private String categoryName;
	
	/**
	 * 削除フラグ
	 */
	private int deleteFlag;
	
	/**
	 * 作成日付
	 */
	private Timestamp createDate;
	
	/**
	 * 更新日付
	 */
	private Timestamp updateDate;
}