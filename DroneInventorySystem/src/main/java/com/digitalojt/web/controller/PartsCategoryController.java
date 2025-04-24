package com.digitalojt.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.UrlConsts;
import com.digitalojt.web.entity.CategoryInfo;
import com.digitalojt.web.form.PartsCategoryForm;
import com.digitalojt.web.service.PartsCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 部品カテゴリー管理画面コントローラークラス
 * 
 * @author dotlife
 *
 */
@Controller
@RequiredArgsConstructor
public class PartsCategoryController extends AbstractController {

	/** 部品カテゴリー サービス */
	private final PartsCategoryService service;

	/** メッセージソース */
	private final MessageSource messageSource;

	/**
	 * 部品カテゴリー情報画面の初期表示
	 *
	 * @param 
 Modelオブジェクト
	 * @return String(Viewの名前：部品カテゴリー管理画面)
	 */
	@GetMapping(UrlConsts.PARTS_CATEGORY)
	public String index(Model model) {
		
		logStart(LogMessage.HTTP_GET);

		// 部品カテゴリー情報 全件取得
		List<CategoryInfo> searchResults = service.getCategoryInfoData();

		// 画面表示用に部品カテゴリー情報リストをセット
		model.addAttribute(ModelAttributeContents.PARTS_CATEGORY_LIST, searchResults);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.PARTS_CATEGORY_INDEX;
	}

	/**
	 * 検索結果表示
	 * 
	 * @param model Modelオブジェクト
	 * @param form フォームオブジェクト
	 * @param bindingResult バリデーション結果
	 * @param redirectAttributes リダイレクト時にフラッシュスコープでデータを渡すためのオブジェクト
	 * @return 部品カテゴリー管理画面（エラー時はリダイレクト）
	 */
	@GetMapping(UrlConsts.PARTS_CATEGORY_SEARCH)
	public String search(Model model, @Valid PartsCategoryForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		logStart(LogMessage.HTTP_GET);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {

			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(
					ModelAttributeContents.ERROR_MSG
					,getValidationErrorMessage(bindingResult, form)
			);
			
			// 部品カテゴリー管理画面にリダイレクト
			return "redirect:" + UrlConsts.PARTS_CATEGORY; 
		}

		// 部品カテゴリー情報取得
		List<CategoryInfo> searchResults = service.getCategoryInfoData(form.getCategoryName());

		// 検索結果が空の場合、エラーメッセージを表示しリダイレクト
		if (searchResults.isEmpty()) {

			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(
					ModelAttributeContents.ERROR_MSG
					,messageSource.getMessage(
							ErrorMessage.DATA_EMPTY_ERROR_MESSAGE
							, null
							, Locale.getDefault()
					 )
			);
			
			// 部品カテゴリー管理画面にリダイレクト
			return "redirect:" + UrlConsts.PARTS_CATEGORY; 
		}

		// 商品情報リストをセット
		model.addAttribute(ModelAttributeContents.PARTS_CATEGORY_LIST, searchResults);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.PARTS_CATEGORY_INDEX;
	}

	/**
	 * 部品カテゴリー 登録画面表示
	 *
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前：部品カテゴリー管理 登録画面)
	 */
	@GetMapping(UrlConsts.PARTS_CATEGORY_REGISTER)
	public String register(Model model) {
		
		logStart(LogMessage.HTTP_GET);

		// 新規登録用のフォームオブジェクトを作成
		PartsCategoryForm form = new PartsCategoryForm();

		// フォームをモデルに追加し、画面で使用できるようにする
		model.addAttribute(ModelAttributeContents.PARTS_CATEGORY_UPDATE_FORM, form);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.PARTS_CATEGORY_REGISTER;
	}

	/**
	 * 入力フォームの情報を部品カテゴリー情報テーブルへ登録
	 * 
	 * @param model Modelオブジェクト
	 * @param form フォームオブジェクト
	 * @param bindingResult バリデーション結果
	 * @param redirectAttributes リダイレクト時にフラッシュスコープでデータを渡すためのオブジェクト
	 * @return String リダイレクト先のURL（入力エラー時は登録画面、成功時は管理画面）
	 */
	@PostMapping(UrlConsts.PARTS_CATEGORY_REGISTER)
	public String register(Model model, @Valid PartsCategoryForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		logStart(LogMessage.HTTP_POST);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
					getValidationErrorMessage(bindingResult, form));
			
			return "redirect:" + UrlConsts.PARTS_CATEGORY_REGISTER; // 部品カテゴリー管理 登録画面にリダイレクト
		}

		// 部品カテゴリー情報を登録
		service.registerPartsCategory(form);

		// 登録成功メッセージをフラッシュスコープに設定
		redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG, "success.register");
		
		logEnd(LogMessage.HTTP_POST);

		return "redirect:" + UrlConsts.PARTS_CATEGORY; // 部品カテゴリー管理画面にリダイレクト
	}

	/**
	 * 部品カテゴリー 更新/削除画面表示
	 *
	 * @param categoryId 部品カテゴリーID（更新/削除対象）
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前：部品カテゴリー管理画面)
	 */
	@GetMapping(UrlConsts.PARTS_CATEGORY_UPDATE + "/{categoryId}")
	public String update(@PathVariable int categoryId, Model model) {
		
		logStart(LogMessage.HTTP_GET);

		// 部品カテゴリーIDから、部品カテゴリー情報取得
		CategoryInfo categoryInfo = service.getCategoryInfoData(categoryId);

		// 取得したデータを画面表示用にモデルへ追加
		model.addAttribute(ModelAttributeContents.PARTS_CATEGORY_UPDATE_FORM, categoryInfo);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.PARTS_CATEGORY_UPDATE;
	}

	/**
	 * 部品カテゴリー 更新処理
	 *
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前：部品カテゴリー管理画面)
	 */
	@PatchMapping(UrlConsts.PARTS_CATEGORY_UPDATE)
	public String update(Model model, @Valid PartsCategoryForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		logStart(LogMessage.HTTP_PATCH);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {

			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(
					ModelAttributeContents.ERROR_MSG
					,getValidationErrorMessage(bindingResult, form)
			);
			
			// 部品カテゴリー管理 更新/削除画面にリダイレクト
			return "redirect:" + UrlConsts.PARTS_CATEGORY_UPDATE + "/" + form.getCategoryId(); 
		}

		// 部品カテゴリー情報を登録
		service.updatePartsCategory(form);

		// 更新成功メッセージをフラッシュスコープに設定
		redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG, "success.update");
		
		logEnd(LogMessage.HTTP_PATCH);

		return "redirect:" + UrlConsts.PARTS_CATEGORY; // 部品カテゴリー管理画面にリダイレクト
	}

	/**
	 * バリデーションエラーメッセージ取得
	 * 
	 * @param bindingResult
	 * @return
	 */
	private String getValidationErrorMessage(BindingResult bindingResult, PartsCategoryForm form) {

		// エラーメッセージをリストに格納
		StringBuilder errorMsg = new StringBuilder();

		// フィールドごとのエラーメッセージを取得し、リストに追加
		bindingResult.getFieldErrors().forEach(error -> {
			String message = error.getDefaultMessage();
			errorMsg.append(message).append("\r\n"); // メッセージを改行で区切って追加
		});
		
		logValidationError(LogMessage.HTTP_POST, form + " " + errorMsg.toString());

		return errorMsg.toString();
	}
}