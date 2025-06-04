package com.digitalojt.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalojt.web.consts.DeleteFlagConsts;
import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.entity.CategoryInfo;
import com.digitalojt.web.entity.CenterInfo;
import com.digitalojt.web.entity.StockInfo;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.StockInfoForm;
import com.digitalojt.web.repository.CenterInfoRepository;
import com.digitalojt.web.repository.PartsCategoryRepository;
import com.digitalojt.web.repository.StockInfoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 在庫一覧画面のサービスクラス
 *
 * @author lin
 * 
 */
@Service
@RequiredArgsConstructor
public class StockInfoService {
	
	/** 部品カテゴリー情報テーブル リポジトリー */
	private final PartsCategoryRepository partsCategoryrepository;
	
	/** センター情報テーブル リポジトリー */
	private final CenterInfoRepository centerInforepository;
	
	/** 在庫情報テーブル リポジトリー */
	private final StockInfoRepository stockInforepository;


	/** メッセージソース */
	private final MessageSource messageSource;

	/**
	 * 論理削除フラグが 0 の部品カテゴリーリストを取得（ID 昇順）
	 * 
	 * @return
	 */
	public List<CategoryInfo> getCategoryListData() {
		return partsCategoryrepository.findByDeleteFlagOrderByCategoryIdAsc(DeleteFlagConsts.ACTIVE);
	}

	/**
	 * 論理削除フラグが 0 のセンター情報リストを取得（ID 昇順）
	 * 
	 * @param categoryId
	 * @return
	 */
	public List<CenterInfo> getCenterListData() {
		return centerInforepository.findByDeleteFlagOrderByCenterIdAsc(DeleteFlagConsts.ACTIVE);
	}
	
	
	/**
	 * 論理削除フラグが 0 の在庫情報リストを取得（ID 昇順）
	 * 
	 * @return
	 */
	public List<StockInfo> getStockInfoData() {
		return stockInforepository.findByDeleteFlagOrderByStockIdAsc(DeleteFlagConsts.ACTIVE);
	}
	
	/**
	 * 在庫IDに合致する部品カテゴリー情報を取得
	 * 
	 * @param stockId
	 * @return
	 */
	public StockInfo getStockInfoData(int stockId) {
		return stockInforepository.findById(stockId).get();
	}

	/**
     * 論理削除フラグが 0 
     * かつ 部品カテゴリーに合致 
     * かつ 名称で曖昧検索 
     * かつ 数量条件にに合致する在庫情報リストを取得（ID 昇順）
	 * 
     * @param form
	 * @return
	 */
	public List<StockInfo> getStockInfoData(@Valid StockInfoForm form) {
		
		return stockInforepository.findStockList(
				form.getCategoryId(), form.getName(), form.getAmount(), form.getAmountCondition(), DeleteFlagConsts.ACTIVE);
	}

	/**
	 * 在庫情報を新規登録する
	 * 
	 * @param form
	 */
	@Transactional
	public void registerStockInfo(@Valid StockInfoForm form) {

		// 在庫情報テーブルのIDは、自動採番であるため、IDのセットは行わない。
		// また、フォームからIDが送られてきた場合は不正な操作の可能性があるため、登録処理を行わないようにする。
		if (form.getStockId() != null) {
			throw new InvalidInputException(messageSource
					.getMessage(ErrorMessage.ININVALID_REGISTRATION_ERROR_MESSAGE, null, Locale.getDefault()));
		}

		StockInfo registerEntity = new StockInfo();
	    // カテゴリーID に対応するカテゴリー情報を取得
		CategoryInfo newCategory =  partsCategoryrepository.findById(form.getCategoryId()).get();
	    // センターID に対応するセンター情報を取得
		CenterInfo newCenter =  centerInforepository.findById(form.getCenterId()).get();

		registerEntity.setCategoryInfo(newCategory);
		registerEntity.setName(form.getName());
		registerEntity.setCenterInfo(newCenter);
		registerEntity.setDescription(form.getDescription());
		registerEntity.setAmount(form.getAmount());
		registerEntity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		registerEntity.setCreateDate(currentTimestamp);
		registerEntity.setUpdateDate(currentTimestamp);

		stockInforepository.save(registerEntity);
	}

	/*
	 * 在庫情報を更新する
	 * 
	 * @param form
	 */
	public void updateStockInfo(@Valid StockInfoForm form) {

		// 存在しない場合は例外をスロー
		StockInfo entity = stockInforepository.findById(form.getStockId()).orElseThrow(() -> 
					new EntityNotFoundException(messageSource.getMessage(
							ErrorMessage.ININVALID_UPDATE_ERROR_MESSAGE
							, null
							, Locale.getDefault()))
					);

		// 削除か更新かで処理を分ける
		if (form.getDeleteFlag()) {
			// 削除の場合、削除フラグを更新
			entity.setDeleteFlag(DeleteFlagConsts.DELETED);
		} else {
			// 更新の場合
		    // カテゴリーID に対応するカテゴリー情報を取得
			CategoryInfo newCategory =  partsCategoryrepository.findById(form.getCategoryId()).get();
		    // センターID に対応するセンター情報を取得
			CenterInfo newCenter =  centerInforepository.findById(form.getCenterId()).get();
			entity.setCategoryInfo(newCategory);
			entity.setName(form.getName());
			entity.setCenterInfo(newCenter);
			entity.setDescription(form.getDescription());
			entity.setAmount(form.getAmount());
			entity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		}

	    // 更新日付をセット
	    entity.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
	    stockInforepository.save(entity);
	}
}