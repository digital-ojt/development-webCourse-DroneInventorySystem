package com.digitalojt.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
import com.digitalojt.web.entity.CenterInfo;
import com.digitalojt.web.entity.StockInfo;
import com.digitalojt.web.form.StockInfoForm;
import com.digitalojt.web.service.StockInfoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;

/**
 * 在庫一覧画面コントローラークラス
 * 
 * @author lin
 *
 */
@Controller
@RequiredArgsConstructor
public class StockListController extends AbstractController {

	/** 在庫一覧 サービス */
	private final StockInfoService service;

	/** メッセージソース */
	private final MessageSource messageSource;
	/**
	 * 初期表示
	 * 
	 * @return String(path)
	 */
	@GetMapping(UrlConsts.STOCK_LIST)
	public String index(Model model, HttpSession session) {
		
		logStart(LogMessage.HTTP_GET);
		
		// カテゴリーリスト 取得
		List<CategoryInfo> categoryList = service.getCategoryListData();
		
		// センターリスト 取得
		List<CenterInfo> centerList = service.getCenterListData();
		
		// 在庫一覧情報 全件取得
		List<StockInfo> stockList = service.getStockInfoData();
		
		// カテゴリーリストをsessionにセット
		session.setAttribute("categoryList", categoryList);
		

		// 在庫センターリストをsessionにセット
		session.setAttribute("centerList", centerList);


		// 画面表示用に在庫情報リストをセット
		model.addAttribute(ModelAttributeContents.STOCK_LIST, stockList);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.STOCK_LIST_INDEX;
	}
	
	/**
	 * 検索結果表示
	 * 
	 * @param model Modelオブジェクト
	 * @param form フォームオブジェクト
	 * @param bindingResult バリデーション結果
	 * @param redirectAttributes リダイレクト時にフラッシュスコープでデータを渡すためのオブジェクト
	 * @return 部品在庫一覧画面（エラー時はリダイレクト）
	 */
	@GetMapping(UrlConsts.STOCK_LIST_SEARCH)
	public String search(Model model, @Valid StockInfoForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		logStart(LogMessage.HTTP_GET);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {

			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(
					ModelAttributeContents.ERROR_MSG
					,getValidationErrorMessage(bindingResult, form)
			);
			
			// 部品在庫一覧画面にリダイレクト
			return "redirect:" + UrlConsts.STOCK_LIST; 
		}
		
		// 部品在庫一覧情報取得
		List<StockInfo> stockList = service.getStockInfoData(form);
		
		// 検索結果が空の場合、エラーメッセージを表示しリダイレクト
		if (stockList.isEmpty()) {

			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(
					ModelAttributeContents.ERROR_MSG
					,messageSource.getMessage(
							ErrorMessage.DATA_EMPTY_ERROR_MESSAGE
							, null
							, Locale.getDefault()
					 )
			);
			
			// 部品在庫一覧情画面にリダイレクト
			return "redirect:" + UrlConsts.STOCK_LIST; 
		}

		// 在庫情報リストをセット
		model.addAttribute(ModelAttributeContents.STOCK_LIST, stockList);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.STOCK_LIST_INDEX;
	}

	/**
	 * 部品在庫一覧 登録画面表示
	 *
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前：部品在庫一覧 登録画面)
	 */
	@GetMapping(UrlConsts.STOCK_LIST_REGISTER)
	public String register(Model model) {
		
		logStart(LogMessage.HTTP_GET);

		// 新規登録用のフォームオブジェクトを作成
		StockInfoForm form = new StockInfoForm();

		// フォームをモデルに追加し、画面で使用できるようにする
		model.addAttribute(ModelAttributeContents.STOCK_UPDATE_FORM, form);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.STOCK_LIST_REGISTER;
	}

	/**
	 * 入力フォームの情報を部品在庫一覧情報テーブルへ登録
	 * 
	 * @param model Modelオブジェクト
	 * @param form フォームオブジェクト
	 * @param bindingResult バリデーション結果
	 * @param redirectAttributes リダイレクト時にフラッシュスコープでデータを渡すためのオブジェクト
	 * @return String リダイレクト先のURL（入力エラー時は登録画面、成功時は管理画面）
	 */
	@PostMapping(UrlConsts.STOCK_LIST_REGISTER)
	public String register(Model model, 
			@Validated({Default.class, StockInfoForm.RegisterValidation.class}) StockInfoForm form, 
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		logStart(LogMessage.HTTP_POST);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
					getValidationErrorMessage(bindingResult, form));
			
			return "redirect:" + UrlConsts.STOCK_LIST_REGISTER; // 部品在庫一覧 登録画面にリダイレクト
		}

		// 部品在庫を登録
		service.registerStockInfo(form);

		// 登録成功メッセージをフラッシュスコープに設定
		redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG, "success.register");
		
		logEnd(LogMessage.HTTP_POST);

		return "redirect:" + UrlConsts.STOCK_LIST; // 部品在庫一覧画面にリダイレクト
	}

	/**
	 * 部品在庫一覧 更新/削除画面表示
	 *
	 * @param stockId 部品カテゴリーID（更新/削除対象）
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前：部品在庫管理画面)
	 */
	@GetMapping(UrlConsts.STOCK_LIST_UPDATE + "/{stockId}")
	public String update(@PathVariable int stockId, Model model) {
		
		logStart(LogMessage.HTTP_GET);

		// 在庫IDから、部品カテゴリー情報取得
		StockInfo stockInfo = service.getStockInfoData(stockId);

		// 取得したデータを画面表示用にモデルへ追加
		model.addAttribute(ModelAttributeContents.STOCK_UPDATE_FORM, stockInfo);
		
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.STOCK_LIST_UPDATE;
	}

	/**
	 * 部品在庫情報 更新処理
	 *
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前：部品在庫一覧管理画面)
	 */
	@PatchMapping(UrlConsts.STOCK_LIST_UPDATE)
	public String update(Model model, 
			@Validated({Default.class, StockInfoForm.RegisterValidation.class}) StockInfoForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		logStart(LogMessage.HTTP_PATCH);

		// 入力値のバリデーションチェック
		if (bindingResult.hasErrors()) {

			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(
					ModelAttributeContents.ERROR_MSG
					,getValidationErrorMessage(bindingResult, form)
			);
			
			// 部品在庫情報 更新/削除画面にリダイレクト
			return "redirect:" + UrlConsts.STOCK_LIST_UPDATE + "/" + form.getStockId(); 
		}

		// 部品カテゴリー情報を登録
		service.updateStockInfo(form);

		// 更新成功メッセージをフラッシュスコープに設定
		redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG, "success.update");
		
		logEnd(LogMessage.HTTP_PATCH);

		return "redirect:" + UrlConsts.STOCK_LIST; // 部品カテゴリー管理画面にリダイレクト
	}
	
	/**
	 * バリデーションエラーメッセージ取得
	 * 
	 * @param bindingResult
	 * @return
	 */
	private String getValidationErrorMessage(BindingResult bindingResult, StockInfoForm form) {

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
