package com.digitalojt.web.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 在庫情報Entity
 * 
 * @author lin
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "stock_info") 
public class StockInfo {
	
	/**
	 * 在庫ID
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Integer stockId;
    
	/**
	 * 分類ID
	 * カテゴリー情報テーブルテーブルの外部キーを指定
	 */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryInfo categoryInfo;
	
	/**
	 * 名前
	 */
    @Column(name = "name", length = 255, nullable = false)
	private String name;
	
	/**
	 * 在庫センターID
	 * 在庫センター情報テーブルの外部キーを指定
	 */
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private CenterInfo centerInfo;
    
	/**
	 * 説明
	 */
    @Column(name = "description", length = 255)
	private String description;
    
	/**
	 * 数量
	 */
    @Column(name = "amount")
	private Integer amount;
    
	/**
	 * 論理削除フラグ (0:未削除, 1:削除済)
	 */
    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    /**
     * 作成日付
     */
    @Column(name = "create_date", nullable = false)
    private Timestamp createDate;

    /**
     * 更新日付
     */
    @Column(name = "update_date", nullable = false)
    private Timestamp updateDate;
}