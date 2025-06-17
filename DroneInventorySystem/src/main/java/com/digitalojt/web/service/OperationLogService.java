package com.digitalojt.web.service;

import com.digitalojt.web.consts.OperationStatus;
import com.digitalojt.web.consts.OperationType;
import com.digitalojt.web.consts.ScreenTitle;
import com.digitalojt.web.entity.OperationLog;
import com.digitalojt.web.repository.OperationLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作履歴画面のサービスクラス
 *
 * @author dotlife
 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

	/** 操作履歴テーブル リポジトリー */
	private final OperationLogRepository repository;

	/**
	 * 操作履歴情報を全件検索で取得
	 *
	 * @return
	 */
	public List<OperationLog> getOperationLogList() {
		// 1ヶ月前の開始日時を取得
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime oneMonth = now.minusMonths(1);

		// 操作履歴情報の取得
		List<OperationLog> operationLogList = repository.findOperationLogsForOneMonth(oneMonth, now);

		// 画面表示用にデータ加工した結果を返却
		return convertOperationLogList(operationLogList);
	}

	/**
	 * 引数に合致する操作履歴情報を取得
	 *
	 * @param userId
	 * @param operateType
	 * @param status
	 * @param operationStart
	 * @param operationEnd
	 * @return 引数と一致する値を取得した結果
	 */
	public List<OperationLog> getOperationLogData(
		String userId,
		Integer operateType,
		Integer status,
		LocalDateTime createDate,
		LocalDateTime updateDate
	) {
		List<OperationLog> operationLogList = repository.findOperationLogs(
			userId,
			operateType,
			status,
			createDate,
			updateDate
		);
		return convertOperationLogList(operationLogList);
	}

	/**
	 * 画面表示用にデータ加工
	 *
	 * @param operationLogList
	 * @return
	 */
	private List<OperationLog> convertOperationLogList(List<OperationLog> operationLogList) {
		return operationLogList
			.stream()
			.map(log -> {
				String screenName = convertTableKey(log.getTableKey());
				String operateType = convertOperateType(log.getOperateType());
				String operationStatus = convertOperationStatus(log.getStatus());

				log.setTableKey(screenName);
				log.setOperateTypeStr(operateType);
				log.setStatusStr(operationStatus);

				return log;
			})
			.collect(Collectors.toList());
	}

	/**
	 * テーブルキーを画面表示用にデータ加工
	 *
	 * @param tableKey
	 * @return
	 */
	private String convertTableKey(String tableKey) {
		return ScreenTitle.fromTableKey(tableKey);
	}

	/**
	 * 操作種類を画面表示用にデータ加工
	 *
	 * @param operateType
	 * @return
	 */
	private String convertOperateType(Integer operateType) {
		return OperationType.fromTypeCode(operateType);
	}

	/**
	 * 操作ステータスを画面表示用にデータ加工
	 *
	 * @param operateStatus
	 * @return
	 */
	private String convertOperationStatus(Integer operateStatus) {
		return OperationStatus.fromStatusCode(operateStatus);
	}
}
