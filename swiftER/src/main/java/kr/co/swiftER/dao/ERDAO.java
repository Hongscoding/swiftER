package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;

@Mapper
@Repository
public interface ERDAO {
	
	public List<ERCateVO> selectErCate();
	public List<ERSubcateVO> selectErSubCate(String city);
	public List<ERReviewVO> selectErReview(@Param("code") String code,@Param("start") int start);
	public int insertErReview(ERReviewVO vo);
	public List<ERCateVO> selectErRegion(String city);
	public List<ERSubcateVO> selectErSubRegion(@Param("town") String town,@Param("city") String city);
	public int selectCountTotal(@Param("code")String code);
	
}
