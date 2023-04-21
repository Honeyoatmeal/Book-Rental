package kr.s08.book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import kr.util.DBUtil;

public class BookDAO {
	// 도서 등록(select)-관리자
	public void insertBook(String bk_name, String bk_category) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "INSERT INTO book (bk_num, bk_name, bk_category, bk_regdate) VALUES (book_seq.nextval, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bk_name);
			pstmt.setString(2, bk_category);
			int count = pstmt.executeUpdate();
			System.out.println(count + "개의 도서가 등록되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	// 도서 목록 보기(select)-관리자
	public void selectBookInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT * FROM book ORDER BY bk_num DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("-----------------------------------");
			System.out.println("도서번호\t도서제목\t도서분야\t도서등록일");
			while(rs.next()) {
				System.out.print(rs.getInt("bk_num") + "\t");
				System.out.print(rs.getString("bk_name") + "\t");
				System.out.print(rs.getString("bk_category") + "\t");
				System.out.println(rs.getDate("bk_regdate"));
			}
			System.out.println("-----------------------------------");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	// 아이디 중복 체크-회원
	public int checkId(String me_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs= null;
		String sql = null;
		int count = 0; 
		
		try {
			// JDBC 수행 1, 2단계
			conn = DBUtil.getConnection();
			// SQL문 작성
			sql = "SELECT me_id FROM member WHERE me_id = ?";
			// JDBC 수행 3단계
			pstmt = conn.prepareStatement(sql);
			// ?에 데이터 바인딩
			pstmt.setString(1, me_id);
			// JDBC 수행 4단계: SQL문 실행
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return count; // 중복: 1, 중복 아님: 0
	}
	// 회원 가입(insert)-회원
	public void insertMember(String me_id, String me_passwd, String me_name, String me_phone) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "INSERT INTO member (me_num, me_id, me_passwd, me_name, me_phone, me_regdate) VALUES (member_seq.nextval, ?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, me_id);
			pstmt.setString(2, me_passwd);
			pstmt.setString(3, me_name);
			pstmt.setString(4, me_phone);
			int count = pstmt.executeUpdate();
			System.out.println(count + "명이 가입되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	// 로그인 체크-회원
	public int loginCheck(String me_id, String me_passwd) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int me_num = 0; // 회원 번호
		String sql = null;
		
		try {
			// JDBC 수행 1, 2단계
			conn = DBUtil.getConnection();
			// SQL문 작성
			sql = "SELECT me_num FROM member WHERE me_id = ? AND me_passwd = ?";
			// JDBC 수행 3단계
			pstmt = conn.prepareStatement(sql);
			// ?에 데이터 바인딩
			pstmt.setString(1, me_id);
			pstmt.setString(2, me_passwd);
			// JDBC 수행 4단계: SQL문 실행
			rs = pstmt.executeQuery();
			if (rs.next()) {
				me_num = rs.getInt("me_num"); // 큰따옴표가 없으면 값에 0이 들어감
			} // 잘못 입력하면 0을 반환 -> 로그인 실패
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return me_num;
	}
	// 회원 목록 보기(select where num)-관리자
	public void selectMember() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT * FROM member ORDER BY me_num DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("-------------------------------------");
			System.out.println("회원번호\t아이디\t비밀번호\t이름\t전화번호\t등록일");
			while(rs.next()) {
				System.out.print(rs.getInt("me_num") + "\t");
				System.out.print(rs.getString("me_id") + "\t");
				System.out.print(rs.getString("me_passwd") + "\t");
				System.out.print(rs.getString("me_name") + "\t");
				System.out.print(rs.getString("me_phone") + "\t");
				System.out.println(rs.getDate("me_regdate"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	// 도서 대출 여부 확인(아이디 중복체크와 같은 패턴)
	// 도서번호(bk_num)로 검색해 re_status의 값이 0이면 대출 가능, 1이면 대출 불가
	public int checkBorrow(int bk_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int count = 0;
		
		try {
			conn = DBUtil.getConnection();
			// sql = "SELECT re_status FROM reservation WHERE bk_num = ?";
			sql = "SELECT re_status FROM book b LEFT OUTER JOIN (SELECT * FROM reservation WHERE re_status = 1) r ON b.bk_num = r.bk_num WHERE r.bk_num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bk_num);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return count;
	}
	// 도서 대출 등록
	public void borrowBook(int bk_num, int me_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "INSERT INTO reservation (re_num, re_status, bk_num, me_num, re_regdate, re_modifydate) VALUES (reservation_seq.nextval, 1, ?, ?, SYSDATE, SYSDATE + 14)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bk_num);
			pstmt.setInt(2, me_num);
			int count = pstmt.executeUpdate();
			System.out.println(count + "건이 대출되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	// 대출 목록 보기-관리자
	// 대출 및 반납 모든 데이터 표시
	public void rentReturnList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT * FROM reservation ORDER BY re_num DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("---------------------------------");
			System.out.println("예약번호\t예약상태\t도서번호\t회원번호\t대여일자\t\t반납일자");
			while(rs.next()) {
				System.out.print(rs.getInt("re_num") + "\t");
				System.out.print(rs.getInt("re_status") + "\t");
				System.out.print(rs.getInt("bk_num") + "\t");
				System.out.print(rs.getInt("me_num") + "\t");
				System.out.print(rs.getDate("re_regdate") + "\t");
				System.out.println(rs.getDate("re_modifydate"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	// MY 대출 목록 보기(현재 대출한 목록(re_status = 1)만 표시)-회원
	public void myBorrowList(String me_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			//sql = "SELECT re_num, bk_num, me_num, re_regdate, re_modifydate FROM reservation WHERE re_status = 1 AND me_num = ?";
			sql = "SELECT r.re_num, r.bk_num, m.me_num, r.re_regdate, r.re_modifydate FROM reservation r JOIN member m ON r.me_num = m.me_num WHERE r.re_status = 1 AND m.me_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, me_id);
			rs = pstmt.executeQuery();
			System.out.println("---------------------------------");
			System.out.println("예약번호\t도서번호\t회원번호\t대여일자\t\t반납일자");
			while (rs.next()) {
				System.out.print(rs.getInt("re_num") + "\t");
				System.out.print(rs.getInt("bk_num") + "\t");
				System.out.print(rs.getInt("me_num") + "\t");
				System.out.print(rs.getDate("re_regdate") + "\t");
				System.out.println(rs.getDate("re_modifydate"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	// 반납 가능 여부-회원
	// 대출번호(re_num)와 회원번호(me_num)를 함께 조회해서 re_status = 1이면 반납 가능
	public int returnCheck(int re_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int count = 0;
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT re_status FROM reservation WHERE re_num = ? AND re_status = 1";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, re_num);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				count = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return count;
	}
	// 반납 처리(update)-회원
	public void updateReturn(int re_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "UPDATE reservation SET re_status = 0 WHERE re_num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, re_num);
			int count = pstmt.executeUpdate();
			System.out.println(count + "건이 반납되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
}import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import kr.util.DBUtil;

public class BookDAO {
	// 도서 등록(select)-관리자
	public void insertBook(String bk_name, String bk_category) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "INSERT INTO book (bk_num, bk_name, bk_category, bk_regdate) VALUES (book_seq.nextval, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bk_name);
			pstmt.setString(2, bk_category);
			int count = pstmt.executeUpdate();
			System.out.println(count + "개의 도서가 등록되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	// 도서 목록 보기(select)-관리자
	public void selectBookInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT * FROM book ORDER BY bk_num DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("-----------------------------------");
			System.out.println("도서번호\t도서제목\t도서분야\t도서등록일");
			while(rs.next()) {
				System.out.print(rs.getInt("bk_num") + "\t");
				System.out.print(rs.getString("bk_name") + "\t");
				System.out.print(rs.getString("bk_category") + "\t");
				System.out.println(rs.getDate("bk_regdate"));
			}
			System.out.println("-----------------------------------");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	// 아이디 중복 체크-회원
	public int checkId(String me_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs= null;
		String sql = null;
		int count = 0; 
		
		try {
			// JDBC 수행 1, 2단계
			conn = DBUtil.getConnection();
			// SQL문 작성
			sql = "SELECT me_id FROM member WHERE me_id = ?";
			// JDBC 수행 3단계
			pstmt = conn.prepareStatement(sql);
			// ?에 데이터 바인딩
			pstmt.setString(1, me_id);
			// JDBC 수행 4단계: SQL문 실행
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return count; // 중복: 1, 중복 아님: 0
	}
	// 회원 가입(insert)-회원
	public void insertMember(String me_id, String me_passwd, String me_name, String me_phone) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "INSERT INTO member (me_num, me_id, me_passwd, me_name, me_phone, me_regdate) VALUES (member_seq.nextval, ?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, me_id);
			pstmt.setString(2, me_passwd);
			pstmt.setString(3, me_name);
			pstmt.setString(4, me_phone);
			int count = pstmt.executeUpdate();
			System.out.println(count + "명이 가입되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	// 로그인 체크-회원
	public int loginCheck(String me_id, String me_passwd) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int me_num = 0; // 회원 번호
		String sql = null;
		
		try {
			// JDBC 수행 1, 2단계
			conn = DBUtil.getConnection();
			// SQL문 작성
			sql = "SELECT me_num FROM member WHERE me_id = ? AND me_passwd = ?";
			// JDBC 수행 3단계
			pstmt = conn.prepareStatement(sql);
			// ?에 데이터 바인딩
			pstmt.setString(1, me_id);
			pstmt.setString(2, me_passwd);
			// JDBC 수행 4단계: SQL문 실행
			rs = pstmt.executeQuery();
			if (rs.next()) {
				me_num = rs.getInt("me_num"); // 큰따옴표가 없으면 값에 0이 들어감
			} // 잘못 입력하면 0을 반환 -> 로그인 실패
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return me_num;
	}
	// 회원 목록 보기(select where num)-관리자
	public void selectMember() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT * FROM member ORDER BY me_num DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("-------------------------------------");
			System.out.println("회원번호\t아이디\t비밀번호\t이름\t전화번호\t등록일");
			while(rs.next()) {
				System.out.print(rs.getInt("me_num") + "\t");
				System.out.print(rs.getString("me_id") + "\t");
				System.out.print(rs.getString("me_passwd") + "\t");
				System.out.print(rs.getString("me_name") + "\t");
				System.out.print(rs.getString("me_phone") + "\t");
				System.out.println(rs.getDate("me_regdate"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	// 도서 대출 여부 확인(아이디 중복체크와 같은 패턴)
	// 도서번호(bk_num)로 검색해 re_status의 값이 0이면 대출 가능, 1이면 대출 불가
	public int checkBorrow(int bk_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int count = 0;
		
		try {
			conn = DBUtil.getConnection();
			// sql = "SELECT re_status FROM reservation WHERE bk_num = ?";
			sql = "SELECT re_status FROM book b LEFT OUTER JOIN (SELECT * FROM reservation WHERE re_status = 1) r ON b.bk_num = r.bk_num WHERE r.bk_num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bk_num);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return count;
	}
	// 도서 대출 등록
	public void borrowBook(int bk_num, int me_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "INSERT INTO reservation (re_num, re_status, bk_num, me_num, re_regdate, re_modifydate) VALUES (reservation_seq.nextval, 1, ?, ?, SYSDATE, SYSDATE + 14)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bk_num);
			pstmt.setInt(2, me_num);
			int count = pstmt.executeUpdate();
			System.out.println(count + "건이 대출되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	// 대출 목록 보기-관리자
	// 대출 및 반납 모든 데이터 표시
	public void rentReturnList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT * FROM reservation ORDER BY re_num DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("---------------------------------");
			System.out.println("예약번호\t예약상태\t도서번호\t회원번호\t대여일자\t\t반납일자");
			while(rs.next()) {
				System.out.print(rs.getInt("re_num") + "\t");
				System.out.print(rs.getInt("re_status") + "\t");
				System.out.print(rs.getInt("bk_num") + "\t");
				System.out.print(rs.getInt("me_num") + "\t");
				System.out.print(rs.getDate("re_regdate") + "\t");
				System.out.println(rs.getDate("re_modifydate"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	// MY 대출 목록 보기(현재 대출한 목록(re_status = 1)만 표시)-회원
	public void myBorrowList(String me_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			//sql = "SELECT re_num, bk_num, me_num, re_regdate, re_modifydate FROM reservation WHERE re_status = 1 AND me_num = ?";
			sql = "SELECT r.re_num, r.bk_num, m.me_num, r.re_regdate, r.re_modifydate FROM reservation r JOIN member m ON r.me_num = m.me_num WHERE r.re_status = 1 AND m.me_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, me_id);
			rs = pstmt.executeQuery();
			System.out.println("---------------------------------");
			System.out.println("예약번호\t도서번호\t회원번호\t대여일자\t\t반납일자");
			while (rs.next()) {
				System.out.print(rs.getInt("re_num") + "\t");
				System.out.print(rs.getInt("bk_num") + "\t");
				System.out.print(rs.getInt("me_num") + "\t");
				System.out.print(rs.getDate("re_regdate") + "\t");
				System.out.println(rs.getDate("re_modifydate"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	// 반납 가능 여부-회원
	// 대출번호(re_num)와 회원번호(me_num)를 함께 조회해서 re_status = 1이면 반납 가능
	public int returnCheck(int re_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int count = 0;
		try {
			conn = DBUtil.getConnection();
			sql = "SELECT re_status FROM reservation WHERE re_num = ? AND re_status = 1";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, re_num);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				count = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return count;
	}
	// 반납 처리(update)-회원
	public void updateReturn(int re_num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = DBUtil.getConnection();
			sql = "UPDATE reservation SET re_status = 0 WHERE re_num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, re_num);
			int count = pstmt.executeUpdate();
			System.out.println(count + "건이 반납되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
}