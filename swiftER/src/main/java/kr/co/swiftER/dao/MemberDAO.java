package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.MemberDoctorVO;
import kr.co.swiftER.vo.MemberHistoryVO;
import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.MemberVO;

@Mapper
@Repository
public interface MemberDAO {

	public MemberTermsVO selectTerms();
	
	public int countMember(String uid);

	public int insertMember(MemberVO member);
	
	public int updatePass(@Param(value="pass") String pass, @Param(value="uid") String uid);

	public MemberVO selectMember(String uid);

	public List<CommunityArticleVO> selectCaList(String uid);
	
	public List<CommunityArticleVO> selectCaListAll(@Param(value="uid")String uid, @Param(value="start")int start);

	public List<ERReviewVO> selectErReviewList(String uid);

	public List<ERReviewVO> selectErListAll(@Param(value="uid")String uid, @Param(value="start")int start);

	public int countCa(String uid);
	
	// 파일 처리

	public int deleteMember(String uid);

	public int checkGrade(String uid);

	public void deleteDoctor(String uid);

	public int checkMember(String uid);

	public MemberVO findId(@Param(value="name")String name, @Param(value="email") String email);

	public MemberDoctorVO selectDoctor(String uid);

	public int changeNor(MemberVO vo);

	public int changeDoc(MemberDoctorVO dvo);

	public void insertMemberDoctor(MemberDoctorVO dvo);

	public void insertNote(MemberHistoryVO hvo);

	public List<MemberHistoryVO> selectHistories(MemberHistoryVO hvo);

	public int checkHistory(MemberHistoryVO hvo);

	public void updateNote(MemberHistoryVO hvo);

	public int selectCountArticleList(String uid);

	public int selectCountReviewList(String uid);

	public int insertSocialMember(@Param(value="uid")String uid, @Param(value="email")String email, @Param(value="randomPass")String randomPass);

}
