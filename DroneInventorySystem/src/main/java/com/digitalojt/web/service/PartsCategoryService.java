package com.digitalojt.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalojt.web.consts.DeleteFlagConsts;
import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.entity.CategoryInfo;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.PartsCategoryForm;
import com.digitalojt.web.repository.PartsCategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 部品カテゴリー管理画面のサービスクラス
 *
 * @author dotlife
 * 
 */
@Service
@RequiredArgsConstructor
public class PartsCategoryService {

	/** 部品カテゴリー情報テーブル リポジトリー */
	private final PartsCategoryRepository repository;

	/** メッセージソース */
	private final MessageSource messageSource;

	/**
	 * 論理削除フラグが 0 の部品カテゴリー情報を取得（ID 昇順）
	 * 
	 * @return
	 */
	public List<CategoryInfo> getCategoryInfoData() {
		return repository.findByDeleteFlagOrderByCategoryIdAsc(DeleteFlagConsts.ACTIVE);
	}

	/**
	 * 部品カテゴリーIDに合致する部品カテゴリー情報を取得
	 * 
	 * @param categoryId
	 * @return
	 */
	public CategoryInfo getCategoryInfoData(int categoryId) {
		return repository.findById(categoryId).get();
	}

	/**
	 * 論理削除フラグが 0 かつ 部品カテゴリー名に合致する部品カテゴリー情報を取得（ID 昇順）
	 * 
	 * @param categoryName
	 * @return
	 */
	public List<CategoryInfo> getCategoryInfoData(String categoryName) {
		return repository.findByCategoryNameAndDeleteFlagOrderByCategoryIdAsc(categoryName, DeleteFlagConsts.ACTIVE);
	}

	/**
	 * 部品カテゴリー情報を新規登録する
	 * 
	 * @param form
	 */
	@Transactional
	public void registerPartsCategory(@Valid PartsCategoryForm form) {

		// カテゴリー名重複チェック
		categoryDuplicationCheck(form.getCategoryName());

		// 部品カテゴリー情報テーブルのIDは、自動採番であるため、IDのセットは行わない。
		// また、フォームからIDが送られてきた場合は不正な操作の可能性があるため、登録処理を行わないようにする。
		if (form.getCategoryId() != null) {
			throw new InvalidInputException(messageSource
					.getMessage(ErrorMessage.ININVALID_REGISTRATION_ERROR_MESSAGE, null, Locale.getDefault()));
		}

		CategoryInfo registerEntity = new CategoryInfo();
		registerEntity.setCategoryName(form.getCategoryName());
		registerEntity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		registerEntity.setCreateDate(currentTimestamp);
		registerEntity.setUpdateDate(currentTimestamp);

		repository.save(registerEntity);
	}

	/*
	 * 部品カテゴリー情報を更新する
	 * 
	 * @param form
	 */
	public void updatePartsCategory(@Valid PartsCategoryForm form) {

		// 存在しない場合は例外をスロー
		CategoryInfo entity = repository.findById(form.getCategoryId()).orElseThrow(() -> 
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
			// 重複チェック
			categoryDuplicationCheck(form.getCategoryName());

			// 更新の場合
			entity.setCategoryName(form.getCategoryName());
			entity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		}

	    // 更新日付をセット
	    entity.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
		repository.save(entity);
	}
	
	/**
	 * カテゴリー名重複チェック処理
	 * @param categoryName カテゴリー名
	 */
	private void categoryDuplicationCheck(String categoryName) {

		// 存在する場合は、重複登録例外をスロー
		CategoryInfo duplicate = repository.getByCategoryName(categoryName);
		if (duplicate != null) {
			throw new DataIntegrityViolationException(
					messageSource.getMessage(
							ErrorMessage.DATA_DUPLICATE_ERROR_MESSAGE
							, null
							, Locale.getDefault()
					)
			);
		}
	}
}