package kr.co.swiftER.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.ERDAO;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;

@Service
public class ERService {
	
	@Autowired
	private ERDAO dao;
	
	public List<ERCateVO> selectErCate(){
		return dao.selectErCate();
	}
	
	public List<ERSubcateVO> selectErSubCate(String city){
		return dao.selectErSubCate(city);
	}
	
	public List<ERReviewVO> selectErReview(String code) {
		return dao.selectErReview(code);
	}
	
	public int insertErReview(String code, String title, String content) {
		return dao.insertErReview(code, title, content);
	}

	public List<ERCateVO> selectErRegion(String city){
		return dao.selectErRegion(city);
	}
	
	public List<ERSubcateVO> selectErSubRegion(String town, String city){
		return dao.selectErSubRegion(town, city);
	}
	
}
