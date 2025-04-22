package com.digitalojt.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalojt.web.consts.DeleteFlagConsts;
import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.entity.CategoryInfo;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.PartsCategoryForm;
import com.digitalojt.web.repository.PartsCategoryInfoRepository;

import lombok.RequiredArgsConstructor;

/*
	部品カテゴリー管理画面のサービスクラス

*/


@Service
@RequiredArgsConstructor
public class PartsCategoryService {
	
	// MessageSourceの依存性注入（DI）
	@Autowired
	private MessageSource messageSource;

	private final PartsCategoryInfoRepository repository;
	
	/*	
	 * 論理フラグが 0 の部品カテゴリー情報を取得（ID 昇順）
	 * @return
	*/
	public List<CategoryInfo> getCategoryInfoData() {
		
		return repository.findByDeleteFlagOrderByCategoryIdAsc(DeleteFlagConsts.ACTIVE);
		
	}
	
	/*	
	 * 論理フラグが 0 かつ 部品カテゴリー名に合致する部品カテゴリー情報を取得（ID 昇順）
	 * @param categoryName
	 * @return
	*/
	public List<CategoryInfo> getCategoryInfoData(String categoryName) {
		
		return repository.findByCategoryNameAndDeleteFlagOrderByCategoryIdAsc(categoryName, DeleteFlagConsts.ACTIVE);
		
	}
	
	/*	
	 * 部品カテゴリー情報を新規登録する
	 * @param form
	*/	

	@Transactional
	public void registerPartsCategory(@Valid PartsCategoryForm form) {
	
		// 存在する場合は、重複登録例外をスロー
		CategoryInfo entity = repository.getByCategoryName(form.getCategoryName());
		if(entity != null) {
			throw new DataIntegrityViolationException(
					//DATA_DUPLICATE_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
					messageSource.getMessage(ErrorMessage.DATA_DUPLICATE_ERROR_MESSAGE, null, Locale.getDefault()));
		}
		
		// 部品カテゴリー情報テーブルのIDは、自動採番であるため、IDのセットは行わない。
		// また、フォームからIDが送られてきた場合は不正な操作の可能性があるため、登録処理を行わないようにする。
		if(form.getCategoryId() != null) {
			throw new InvalidInputException(
					//INVALID_REGISTRATION_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
					messageSource.getMessage(ErrorMessage.INVALID_REGISTRATION_ERROR_MESSAGE, null, Locale.getDefault()));
			
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
	 * 部品カテゴリーIDに合致する部品カテゴリー情報を取得
	 * @param categoryId
	 * @return
	*/	

	public CategoryInfo getCategoryInfoData(int categoryId) {

		return repository.findById(categoryId).get();
		
	}
	
	/*	
	 * 部品カテゴリー情報を更新する
	 * 
	 * @pram form
	*/	
	
	public void updatePartsCategory(@Valid PartsCategoryForm form) {
		
		// 存在しない場合は例外をスロー
		CategoryInfo entity = repository.findById(form.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException(
						//ININVALID_UPDATE_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
						messageSource.getMessage(ErrorMessage.INVALID_UPDATE_ERROR_MESSAGE, null, Locale.getDefault())));

		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		entity.setUpdateDate(currentTimestamp);
		
		// 削除か更新かで処理を分ける
		
	if(form.getDeleteFlag()) {

		// 削除の場合
		entity.setDeleteFlag(DeleteFlagConsts.DELETED);
		
	} else {
		
		// 更新の場合
		entity.setCategoryName(form.getCategoryName());
		entity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		
	}

	repository.save(entity);
	}

	
}
