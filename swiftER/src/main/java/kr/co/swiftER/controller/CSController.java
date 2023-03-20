package kr.co.swiftER.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.service.CSService;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.FileVO;

@Controller
public class CSController {

	@Autowired
	private CSService service;
	
	@Autowired
	private SqlSession sqlSession;
	
	@GetMapping("cs/index")
	public String index() {
		return "cs/index";
	}
	
	@GetMapping("cs/notice")
	public String notice(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리
		int total = service.selectCountTotal("1", subcateCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
		
	    // notice 글들 불러오기(사용자가 subcate값까지 고른 경우 subcate로 필터링한 결과 가져옴)
	    List<CSQuestionsVO> noticeList = service.selectArticles("1", subcateCode, start);
	    
	    for(CSQuestionsVO notice : noticeList) {
			// rdate 날짜까지만 표시하기
			notice.setRdate(notice.getRdate().substring(0, 11));
		}
	    
	    // 화면에 출력할 글들 저장 
	    model.addAttribute("noticeList", noticeList);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	 	model.addAttribute("pg", pg);
	 	model.addAttribute("subcateCode", subcateCode);
		
	 	return "cs/notice";
	}
	
	@GetMapping("cs/notice/view")
	public String noticeView() {
		return "cs/notice_view";
	}
	
	@GetMapping("cs/faq")
	public String faq(@RequestParam(value="subcateCode", defaultValue = "10") String subcateCode, Model model) {
		// faq 글들 불러오기
		List<CSQuestionsVO> faqList = new ArrayList<>();
		
		// FAQ 글 들 중 사용자가 선택한 subCate에 해당하는 글들을 가져오기(페이지 첫 로드시 subCate값은 회원정보로 고정)
		faqList = service.selectArticles("2", subcateCode, 0);
		
		model.addAttribute("faqList", faqList);
		
		return "cs/faq";
	}
	
	@ResponseBody
	@GetMapping("cs/faqByCate")
	public Map<String, List<CSQuestionsVO>> faqByCate(@RequestParam(value="subcateCode", defaultValue = "10") String subcateCode) {
		// 사용자가 faq 페이지에서 subcate 버튼을 누르면 해당 subcate에 해당하는 글만 json 형식으로 리턴하는 메서드
		// faq 글들 불러오기
		List<CSQuestionsVO> faqList = service.selectArticles("2", subcateCode, 0);
		
		// 뷰로 불러온 글들 전송하기
		Map<String, List<CSQuestionsVO>> result = new HashMap<>();
		result.put("faqList", faqList);
		return result;
	}
	
	@GetMapping("cs/qna")
	public String qna(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리
		int total = service.selectCountTotal("3", subcateCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
		
		// qna 글들 불러오기
		List<CSQuestionsVO> qnaList = new ArrayList<>();
		
		// cate, subcate 코드에 따라 해당하는 글들을 가져오기
		qnaList = service.selectArticles("3", subcateCode, start);
		
		for(CSQuestionsVO qna : qnaList) {
			// rdate 날짜까지만 표시하기
			qna.setRdate(qna.getRdate().substring(0, 11));
			
			// 아이디 마스킹 처리 - 프론트에서 하는 것보다 여기서 마스킹하는 것이 사용자가 마스킹 전의 데이터에 접근할 가능성을 낮춤
			qna.setMember_uid(qna.getMember_uid().substring(0, qna.getMember_uid().length() - 3).concat("***"));
		}
		
		// 화면에 출력할 글들 저장
		model.addAttribute("qnaList", qnaList);
		// select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
		model.addAttribute("subcateCode", subcateCode);
		// 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
		model.addAttribute("pg", pg);
		
		return "cs/qna";
	}
	
	@GetMapping("cs/qna/view")
	public String qnaView(String no, Model model) {
		// 사용자가 선택한 글 불러오기
		List<CSQuestionsVO> list = sqlSession.selectList("kr.co.swiftER.dao.CSDAO.selectArticle", no);
		CSQuestionsVO article = list.get(0);
		List<FileVO> files = article.getFvoList();
		
		// rdate 날짜까지만 표시하기
		article.setRdate(article.getRdate().substring(0, 11));
		// 아이디 마스킹 처리
		article.setMember_uid(article.getMember_uid().substring(0, article.getMember_uid().length() - 3).concat("***"));
		
		// 불러온 글 저장
		model.addAttribute("article", article);
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		return "cs/qna_view";
	}
	
	@GetMapping("download")
	public ResponseEntity<Resource> download(String parent, int num) throws IOException{
		// 파일 조회
		FileVO fvo = service.selectFileForDownload(parent, num);
		
		// 파일 다운로드
		return service.fileDownload(fvo);
	}
	
	@GetMapping("cs/qna/write")
	public String qnaWrite() {
		return "cs/qna_write";
	}
	
	@PostMapping("cs/qna/write")
	public String qnaWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, MultipartHttpServletRequest req, Principal principal){
		// 작성자(현재 로그인 되어있는 사용자)의 정보 가져오려면 principal 객체를 현재 메서드의 파라미터로 줘서 principal 객체에 .getName()하면 됨
		String username = principal.getName();
		
		// CSQuestionsVO 객체에 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setMember_uid(username);
		article.setRegip(req.getRemoteAddr());
		
		// 사용자가 업로드한 파일들 가져오고 article 객체의 file 속성값 정하기
		if(!article.getFname().isEmpty()) { // 첨부 파일이 한 개 이상인 경우
			List<MultipartFile> files = req.getFiles("fname");
			article.setFile(files.size());
			
			// 사용자가 작성한 QnA DB에 insert
			service.insertArticle(article);
			
			for(MultipartFile file : files) {
				// DB에 파일 업로드
				service.uploadFile(file, article);
			}
		}else { // 첨부 파일이 없는 경우
			
			// 사용자가 작성한 QnA DB에 insert
			service.insertArticle(article);
			
			article.setFile(0);
		}
		
		return "redirect:/cs/qna";
	}
}
