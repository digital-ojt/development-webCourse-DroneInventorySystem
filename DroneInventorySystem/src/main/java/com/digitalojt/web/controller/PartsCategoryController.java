package com.digitalojt.web.controller;

import java.util.List;
import java.util.Locale;

import jakarta.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.UrlConsts;
import com.digitalojt.web.entity.CategoryInfo;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.PartsCategoryForm;
import com.digitalojt.web.service.PartsCategoryService;

import lombok.RequiredArgsConstructor;


//部品カテゴリー管理画面コントローラークラス
@Controller
@RequiredArgsConstructor
public class PartsCategoryController extends AbstractController {
	
	// 部品カテゴリーサービス
	private final PartsCategoryService service;
	
	 // メッセージソースを定義
    private final MessageSource messageSource;
    
    	
	/**	
	 * 部品カテゴリー情報テーブルから取得したデータを使用して画面の初期表示を実行する。
	 * @param model
	 * @return String(Viewの名前：分類情報管理画面)
	*/
		
	@GetMapping(UrlConsts.CATEGORY_LIST_INDEX)
	public String index(Model model) {
		
		logStart(LogMessage.HTTP_GET);
	    logEnd(LogMessage.HTTP_GET);
	    
		//部品カテゴリー情報　取得処理（部品カテゴリー管理画面に表示するデータを取得）
		List<CategoryInfo> searchResults = service.getCategoryInfoData();
		
		//画面表示用に商品情報をセット
		model.addAttribute(ModelAttributeContents.CATEGORY_LIST,searchResults);		

		return UrlConsts.CATEGORY_LIST_INDEX;
	}

	
	@GetMapping(UrlConsts.PARTS_CATEGORY_SEARCH)
	public String search(@RequestParam @Valid String categoryName, Model model, PartsCategoryForm form, 
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		System.out.println("検索処理実行");
		//System.out.println("UrlConsts.PARTS_CATEGORY_SEARCH: " + UrlConsts.PARTS_CATEGORY_SEARCH);
		//System.out.println("categoryName: " + categoryName);

		if(bindingResult.hasErrors()) {
			
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
				getValidationErrorMessage(bindingResult, redirectAttributes));

			//部品カテゴリー管理画面にリダイレクト ERROR_MSGを渡す
			return "redirect:" + UrlConsts.CATEGORY_LIST_INDEX; //部品カテゴリー管理画面にリダイレクト
		
		}

		//部品カテゴリー名から、部品カテゴリー情報取得(部品カテゴリー管理画面のテーブルに表示する検索結果を取得)
		List<CategoryInfo> searchResults = service.getCategoryInfoData(form.getCategoryName());

		// モデルに追加した CATEGORY_LIST の内容を確認
		if (searchResults != null) {
	        searchResults.forEach(category -> {
	            System.out.println("CategoryID: " + category.getCategoryId());
	            System.out.println("CategoryName: " + category.getCategoryName());
	        });
	    } else {
	        System.out.println("CATEGORY_LIST is null or empty.");
	    }
		
		if(searchResults.isEmpty()) {
			
			//バリデーションエラーメッセージ取得をredirectAttributeに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
					messageSource.getMessage(ErrorMessage.DATA_EMPTY_ERROR_MESSAGE, null, Locale.getDefault()));

			//部品カテゴリー管理画面にリダイレクト ERROR_MSGを渡す
			return "redirect:" + UrlConsts.CATEGORY_LIST_INDEX;  //部品カテゴリー管理画面にリダイレクト
		}


		//画面表示用に部品カテゴリーリストをセット
		model.addAttribute(ModelAttributeContents.CATEGORY_LIST,searchResults);		

		System.out.println("searchResults: " + searchResults);
		System.out.println("searchResults size: " + searchResults.size());

		return UrlConsts.CATEGORY_LIST_INDEX; //部品カテゴリー管理画面にリダイレクト

	}
	
	/**
	 * 	部品カテゴリー 登録画面表示
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前:部品カテゴリー管理 登録画面)
	*/

	/**	
	 * 	入力フォームの情報を部品カテゴリーテーブルへ登録
	 * 			
	 * 	@param model Modelオブジェクト
	 * 	@param form フォームオブジェクト
	 * 	@param bindingResult バリデーション結果
	 * 	@param redirectAttributesリダイレクト時にフラッシュスコープでデータを渡すためのオブジェクト
	 * 	@param string リダイレクト先のURL（入力エラー時は登録画面,成功時は管理画面）
	*/
	
	   
	// 登録画面を表示する
	@GetMapping(UrlConsts.PARTS_CATEGORY_REGISTER)
	public String viewRegister(Model model,PartsCategoryForm form) {
		
		System.out.println("登録画面表示");
				 
		//model.addAttribute(ModelAttributeContents.PARTS_CATEGORY_UPDATE_FORM, form);
	    
		return  UrlConsts.PARTS_CATEGORY_REGISTER;
	}
		
	// 登録処理
	@PostMapping(UrlConsts.PARTS_CATEGORY_REGISTER)
	public String register(Model model, @Valid PartsCategoryForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		System.out.println("登録処理実行");
		
		// 入力時のバリデーションチェック
		if(bindingResult.hasErrors()) {

			String errorMessage = getValidationErrorMessage(bindingResult, redirectAttributes);
			System.out.println("errorMsg: " + errorMessage);
			
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
				getValidationErrorMessage(bindingResult, redirectAttributes));
		
			// 部品カテゴリー管理画面にリダイレクト ERROR_MSGを渡す
			return "redirect:" + UrlConsts.PARTS_CATEGORY_REGISTER; //部品カテゴリー管理 登録画面にリダイレクト
		
		}
		
		
		// 登録データに重複あればエラーメッセージを返す
	    try {
	    	
	        service.registerPartsCategory(form);  // 登録処理

	        //正常処理メッセージ
	        redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG,
	        		messageSource.getMessage(ErrorMessage.SUCCESS_REGISTER_MESSAGE,
	        		new Object[]{form.getCategoryName()}, 		// プレースホルダーにカテゴリ名を渡す
	        		Locale.getDefault()));

				
	        return "redirect:" + UrlConsts.CATEGORY_LIST_INDEX; //部品カテゴリー管理 登録画面にリダイレクト

	    } catch (DataIntegrityViolationException e) {
	        // 重複があればここに到達
	        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());  // フラッシュスコープにエラーメッセージを追加
	        return "redirect:" + UrlConsts.PARTS_CATEGORY_REGISTER; // 部品カテゴリー管理 登録画面にリダイレクト
	    } catch (InvalidInputException e) {
	        // 無効な入力の場合
	        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
	        return "redirect:" + UrlConsts.PARTS_CATEGORY_REGISTER;
	    }

	}	
	
	private String getValidationErrorMessage(BindingResult bindingResult, RedirectAttributes redirectAttributes) {

	    StringBuilder errorMessage = new StringBuilder();

	    // フィールドごとのエラーメッセージを取得し、リストに格納
	    bindingResult.getFieldErrors().forEach(error -> {
	        errorMessage.append(error.getDefaultMessage()).append("<br>"); // メッセージを改行で区切って追加 (HTML表示を考慮)
	    });

	    // グローバルエラーメッセージを取得
	    bindingResult.getGlobalErrors().forEach(error -> {
	        errorMessage.append(error.getDefaultMessage()).append("<br>");
	    });

	    return errorMessage.toString();
	    
	}
	
	/**	
	 *	部品カテゴリー 更新/削除画面表示
	 * 	
	 *	@param categoryId 部品カテゴリーID（更新/削除対象）
     *  @param model Modelオブジェクト
	 *	@return String(Viewの名前：部品カテゴリー管理画面)
	
	*/

	/**	
	 *	部品カテゴリー 更新処理
	 * 	
     *  @param model Modelオブジェクト
	 *	@return String(Viewの名前：部品カテゴリー管理画面)
	
	*/
		
	// 更新/削除画面を表示する
	@GetMapping(UrlConsts.PARTS_CATEGORY_UPDATE + "/{categoryId}")
	public String viewUpdate(Model model, @PathVariable("categoryId") Integer categoryId, PartsCategoryForm form) {

	    System.out.println("更新/削除画面表示 (categoryId: " + categoryId + ")");

	    CategoryInfo categoryInfo = service.getCategoryInfoData(categoryId); // サービス層の既存メソッドを使用

	    if (categoryInfo != null) {
	        form.setCategoryId(categoryInfo.getCategoryId());
	        form.setCategoryName(categoryInfo.getCategoryName());
	        form.setDeleteFlag(categoryInfo.getDeleteFlag() == 1); // int の deleteFlag を boolean の form に設定
	        model.addAttribute("partsCategoryForm", form);
	        return UrlConsts.PARTS_CATEGORY_UPDATE;
	    } else {
	        model.addAttribute("errorMsg", "指定された部品カテゴリーが見つかりませんでした。");
	        return "redirect:" + UrlConsts.CATEGORY_LIST_INDEX;
	    }
	}
	
	
	@PatchMapping(UrlConsts.PARTS_CATEGORY_UPDATE)
	public String update(Model model, @Valid PartsCategoryForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		// 入力値のバリデーションチェック
		if(bindingResult.hasErrors()) {
			
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
					getValidationErrorMessage(bindingResult, redirectAttributes));

			// フォームデータも渡す
		    redirectAttributes.addFlashAttribute("partsCategoryForm", form);

			// 部品カテゴリー管理 更新/削除画面にリダイレクト
			return "redirect:" + UrlConsts.PARTS_CATEGORY_REGISTER;
 		}
		
		// 部品カテゴリー情報を登録
		service.updatePartsCategory(form);
		
		// 正常処理メッセージ
		// 更新成功メッセージをフラッシュスコープに設定		
 		redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG,
				messageSource.getMessage(ErrorMessage.SUCCESS_UPDATE_MESSAGE,
                new Object[]{form.getCategoryName()}, // プレースホルダーにカテゴリ名を渡す
                Locale.getDefault()));
		
		
		
		return "redirect:" + UrlConsts.CATEGORY_LIST_INDEX; // 部品カテゴリー管理画面にリダイレクト
	}
}
