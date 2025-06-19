package com.digitalojt.web.controller;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.Region;
import com.digitalojt.web.consts.UrlConsts;
import com.digitalojt.web.entity.CenterInfo;
import com.digitalojt.web.form.CenterInfoForm;
import com.digitalojt.web.service.CenterInfoService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 在庫センター情報画面のコントローラークラス
 *
 * @author dotlife
 */
@Controller
@RequiredArgsConstructor
public class CenterInfoController extends AbstractController {

	/** センター情報 サービス */
	private final CenterInfoService centerInfoService;

	/**
	 * 都道府県Enumをリストに変換
	 *
	 * @return
	 */
	@ModelAttribute(ModelAttributeContents.REGIONS)
	public List<Region> populateRegions() {
		return Arrays.asList(Region.values());
	}

	/**
	 * 初期表示
	 *
	 * @param model
	 * @retur
	 */
	@GetMapping(UrlConsts.CENTER_INFO)
	public String index(Model model) {
		logStart(LogMessage.HTTP_GET);

		// 在庫センター情報画面に表示するデータを取得
		List<CenterInfo> centerInfoList = centerInfoService.getCenterInfoData();

		// 画面表示用に商品情報リストをセット
		model.addAttribute(ModelAttributeContents.CENTER_INFO_LIST, centerInfoList);

		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.CENTER_INFO_INDEX;
	}

	/**
	 * 検索結果表示
	 *
	 * @param model
	 * @param form
	 * @return
	 */
	@GetMapping(UrlConsts.CENTER_INFO_SEARCH)
	public String search(Model model, @Valid CenterInfoForm form, BindingResult bindingResult) {
		logStart(LogMessage.HTTP_GET);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {
			handleValidationError(model, bindingResult, form);
			return UrlConsts.CENTER_INFO_INDEX;
		}

		// 検索条件に基づいて在庫センター情報を取得
		List<CenterInfo> centerInfoList = centerInfoService.getCenterInfoData(form.getCenterName(), form.getRegion());

		// 画面表示用に商品情報リストをセット
		model.addAttribute(ModelAttributeContents.CENTER_INFO_LIST, centerInfoList);

		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.CENTER_INFO_INDEX;
	}

	/**
	 * バリデーションエラー処理
	 *
	 * @param model
	 * @param bindingResult
	 * @param form
	 */
	private void handleValidationError(Model model, BindingResult bindingResult, CenterInfoForm form) {
		// エラーメッセージをリストに格納
		StringBuilder errorMsg = new StringBuilder();

		// フィールドごとのエラーメッセージを取得し、リストに追加
		bindingResult
			.getGlobalErrors()
			.forEach(error -> {
				String message = error.getDefaultMessage();
				errorMsg.append(message).append("\r\n"); // メッセージを改行で区切って追加
			});

		// エラーメッセージをモデルに追加
		model.addAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMsg.toString());

		logValidationError(LogMessage.HTTP_POST, form + " " + errorMsg.toString());
	}
}
