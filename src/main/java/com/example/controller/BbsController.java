package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Article;
import com.example.service.ArticleService;

import jakarta.servlet.http.HttpSession;

/**
 * 掲示板アプリケーションのコントローラ.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/bbs")
public class BbsController {
	@Autowired
	private HttpSession session;

	/**
	 * 投稿画面を表示します.
	 * 
	 * @return 掲示板画面
	 */
	@GetMapping("/show")
//	public String showBbs() {
//		@SuppressWarnings("unchecked")
//		List<Article> articleList = (List<Article>) session.getAttribute("articleList");
//		if (articleList == null) {
//			articleList = new ArrayList<Article>();
//		}
//		session.setAttribute("articleList", articleList);
//
//		// 投稿画面へ遷移
//		return "bbs";
//	}

	public String showBbs(HttpSession session, Model model) {
		@SuppressWarnings("unchecked")
		List<Article> articleList = (List<Article>) session.getAttribute("articleList");
		if (articleList == null) {
			articleList = new ArrayList<Article>();
			session.setAttribute("articleList", articleList);
		}
		model.addAttribute("articleList", articleList);

		// CSRF対策用のランダムなトークンを生成
		String csrfToken = UUID.randomUUID().toString();
		// トークンをセッションに保存（後で検証用に使用）
		session.setAttribute("csrfToken", csrfToken);
		// トークンを画面に渡す（フォームのhidden項目で利用）
		model.addAttribute("csrfToken", csrfToken);

		// 投稿画面へ遷移
		return "bbs";
	}

//	/**
//	 * 記事を投稿します.
//	 *
//	 * @param name リクエストパラメータのname
//	 * @param body リクエストパラメータのbody
//	 * @return 掲示板画面
//	 */
//	@PostMapping("/postArticle")
//	public String postArticle(String name, String body) {
//		@SuppressWarnings("unchecked")
//		List<Article> articleList = (List<Article>) session.getAttribute("articleList");
//		Article article = new Article(name, body);
//		ArticleService articleService = new ArticleService();
//		articleService.postArticle(articleList, article);
//		return "redirect:/bbs/show";
//	}

	/**
	 * 記事を投稿します.
	 *
	 * @param name リクエストパラメータのname
	 * @param body リクエストパラメータのbody
	 * @param csrfToken CSRF対策用トークン
	 * @return 掲示板画面
	 */
	public String postArticle(String name, String body, String csrfToken, Model model) {
		@SuppressWarnings("unchecked")
		// セッションから記事リストを取得
		List<Article> articleList = (List<Article>) session.getAttribute("articleList");
		// 記事リストがnullの場合は新規作成
		if (articleList == null) {
			articleList = new ArrayList<Article>();
			session.setAttribute("articleList", articleList);
		}

		// CSRFトークンの検証
		String sessionCsrfToken = (String) session.getAttribute("csrfToken");
		// セッションに保存されたCSRFトークンとリクエストパラメータのトークンを比較
		if (sessionCsrfToken == null || !sessionCsrfToken.equals(csrfToken)) {
			model.addAttribute("error", "不正なリクエストです。");
			return "bbs";
		}

		// CSRFトークンをセッションから削除（再利用防止）
		Article article = new Article(name, body);
		// 記事オブジェクトを作成
		ArticleService articleService = new ArticleService();
		// 記事を投稿
		articleService.postArticle(articleList, article);

		return "redirect:/bbs/show";
	}

	/**
	 * 記事を削除します.
	 * 
	 * @param index リクエストパラメータのindex
	 * @return 掲示板画面
	 */
	@PostMapping("/deleteArticle")
	public String deleteArticle(String index) {
		@SuppressWarnings("unchecked")
		List<Article> articleList = (List<Article>) session.getAttribute("articleList");
		ArticleService articleService = new ArticleService();
		int intIndex = Integer.parseInt(index); // indexをStringからintに変換
		articleService.deleteArticle(articleList, intIndex);
		return "redirect:/bbs/show";
	}
}
