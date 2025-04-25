package com.digitalojt.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.digitalojt.web.consts.UrlConsts;

/**
 * 在庫一覧画面コントローラークラス
 * 
 * @author your name
 *
 */
@Controller
public class StockListController extends AbstractController {
	
	/**
	 * 初期表示
	 * 
	 * @return String(path)
	 */
	@GetMapping(UrlConsts.STOCK_LIST)
	public String index(Model model) {
		return UrlConsts.STOCK_LIST_INDEX;
	}
}
