package com.digitalojt.web.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;

/**
 * 操作履歴Entity
 * 
 * @author dotlife
 *
 */
@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {

	/**
	 * ログID
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
	private int logId;

	/**
	 * 管理者ID
	 */
	@ManyToOne
	@JoinColumn(name = "admin_id", nullable = false)
	private AdminInfo adminInfo;

	/**
	 * 操作対象のテーブルキー
	 */
    @Column(name = "table_key", length = 255, nullable = false)
	private String tableKey;

	/**
	 * 操作の種類 (1:作成, 2:更新, 3:削除)
	 */
    @Column(name = "operate_type", nullable = false)
	private Integer operateType;

	/**
	 * 操作のステータス (0:失敗, 1:成功)
	 */
    @Column(name = "status",  nullable = false)
	private Integer status;
    
    /**
     * 操作内容詳細
     */
    @Column(name = "operation_details", length = 255)
    private String operationDetails;

	/**
	 * 論理削除フラグ (0:未削除, 1:削除済)
	 */
    @Column(name = "delete_flag", nullable = false)
	private Integer  deleteFlag;
    /**
     * 作成日付
     */
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    /**
     * 更新日付
     */
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
    
    /**
     * 画面表示用にデータ加工された操作種類
     */
    @Transient
    private String operateTypeStr;

    /**
     * 画面表示用にデータ加工された操作ステータス
     */
    @Transient
    private String statusStr;
    
    /**
     * 初期表示用の操作時刻の表示を整形
     */
    public String getFormattedCreateDate() {
        return createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
}
