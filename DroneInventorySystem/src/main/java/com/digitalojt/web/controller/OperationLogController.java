package com.digitalojt.web.controller;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.OperationStatus;
import com.digitalojt.web.consts.OperationType;
import com.digitalojt.web.consts.UrlConsts;
import com.digitalojt.web.entity.OperationLog;
import com.digitalojt.web.form.OperationLogForm;
import com.digitalojt.web.service.OperationLogService;

import lombok.RequiredArgsConstructor;

/**
 * 操作履歴画面のコントローラークラス
 * 
 * @author dotlife
 *
 */
@Controller
@RequiredArgsConstructor
public class OperationLogController extends AbstractController {

	/** 操作履歴 サービス */
	private final OperationLogService operationLogService;
    
    /**
     * 操作の種類Enumをリストに変換
     * 
     * @return
     */
    @ModelAttribute(ModelAttributeContents.TYPE)
    public List<OperationType> populateType() {
        return Arrays.asList(OperationType.values());
    }
    
    /**
     * 操作のステータスEnumをリストに変換
     * 
     * @return
     */
    @ModelAttribute(ModelAttributeContents.STATUS)
    public List<OperationStatus> populateStatus() {
        return Arrays.asList(OperationStatus.values());
    }

	/**
	 * 初期表示
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(UrlConsts.OPERATION_LOG)
	public String index(Model model) {

		logStart(LogMessage.HTTP_GET);
		
        // 操作履歴情報の取得（直近1か月分のみ）
        List<OperationLog> operationLogList = operationLogService.getOperationLogList();
		
        // 画面表示用に操作履歴情報リストをセット
		model.addAttribute(ModelAttributeContents.OPERATION_LOG_LIST, operationLogList);
	    
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.OPERATION_LOG_INDEX;
	}
	
    /**
     * 検索結果表示
     * 
     * @param model
     * @param form
     * @param bindingResult
     * @return
     */
    @GetMapping(UrlConsts.OPERATION_LOG_SEARCH)
    public String search(Model model, @Valid OperationLogForm form, BindingResult bindingResult) {
        logStart(LogMessage.HTTP_GET);
        
        // 入力値のバリデーションチェック
        if (bindingResult.hasErrors()) {
            handleValidationError(model, bindingResult, form);
    		return UrlConsts.OPERATION_LOG_INDEX;
        }
        
        Integer operateTypeCode = null;
        Integer statusCode = null;
        
        // 入力値「操作」をIntegerに変更
        if (form.getOperateTypeStr() != null && !form.getOperateTypeStr().isBlank()) {
            OperationType typeEnum = OperationType.valueOf(form.getOperateTypeStr());
            operateTypeCode = typeEnum.getTypeCode();
        }
        
        // 入力値「ステータス」をIntegerに変更
        if (form.getStatusStr() != null && !form.getStatusStr().isBlank()) {
            OperationStatus statusEnum = OperationStatus.valueOf(form.getStatusStr());
            statusCode = statusEnum.getStatusCode();
        }
        
        // 検索条件に基づいて操作履歴情報を取得
        List<OperationLog> operationLogList = operationLogService.getOperationLogData(
        		form.getUserId(), operateTypeCode, statusCode, form.getCreateDate(), form.getUpdateDate());

        // 画面表示用に操作履歴情報リストをセット
        model.addAttribute(ModelAttributeContents.OPERATION_LOG_LIST, operationLogList);
        
        logEnd(LogMessage.HTTP_GET);

		return UrlConsts.OPERATION_LOG_INDEX;
    }
    
    /**
     * バリデーションエラー処理
     * 
     * @param model
     * @param bindingResult
     * @param form
     */
    private void handleValidationError(Model model, BindingResult bindingResult, OperationLogForm form) {
        // エラーメッセージをリストに格納
        StringBuilder errorMsg = new StringBuilder();

        // フィールドごとのエラーメッセージを取得し、リストに追加
        bindingResult.getGlobalErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            errorMsg.append(message).append("\r\n"); // メッセージを改行で区切って追加
        });

        // エラーメッセージをモデルに追加
        model.addAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMsg.toString());

        logValidationError(LogMessage.HTTP_POST, form + " " + errorMsg.toString());
    }
    
}
