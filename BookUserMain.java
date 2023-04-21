import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class BookUserMain {
	private BufferedReader br;
	private BookDAO dao;
	private int me_num; // 회원 번호
	private boolean flag; // 로그인 여부(로그인: true, 미로그인: false)
	
	public BookUserMain() {
		try {
			dao = new BookDAO();
			br = new BufferedReader(new InputStreamReader(System.in));
			callMenu();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) try {br.close();} catch(IOException e) {}
		}
	}
	
	public void callMenu() throws IOException {
		while(true) {
			System.out.print("1. 로그인 || 2. 회원가입 || 3. 종료 : "); // 다중행
			try {
				int no = Integer.parseInt(br.readLine());
				if (no == 1) { // 로그인
					System.out.print("아이디: ");
					String me_id = br.readLine();
					System.out.print("비밀번호: ");
					String me_passwd = br.readLine();
					
					me_num = dao.loginCheck(me_id, me_passwd);
					if (me_num > 0) {
						System.out.println(me_id + "님, 로그인 되었습니다.");
						flag = true;
						break;
					}
					System.out.println("아이디 또는 비밀번호 불일치");
				} else if (no == 2) { // 회원가입
					System.out.print("아이디: ");
					String me_id = br.readLine();
					if (dao.checkId(me_id) == 1) {
						System.out.println("존재하는 아이디입니다.");
						continue;
					}
					System.out.print("비밀번호: ");
					String me_passwd = br.readLine();
					System.out.print("이름: ");
					String me_name = br.readLine();
					System.out.print("전화번호: ");
					String me_phone = br.readLine();
					
					dao.insertMember(me_id, me_passwd, me_name, me_phone);
				} else if (no == 3) { // 종료
					System.out.println("프로그램 종료");
					break;
				} else {
					System.out.println("잘못 입력했습니다.");
				}
			} catch(NumberFormatException e) {
				System.out.println("숫자만 입력 가능");
			}
		} // end of while
		
		// 로그인했을 때 메뉴
		while(flag) {
			System.out.print("1. 도서 대출 || 2. MY대출목록 || 3. 대출도서 반납 || 4. 종료: ");
			try {
				int no = Integer.parseInt(br.readLine());
				if (no == 1) { // 도서 대출
					System.out.print("검색할 도서번호: ");
					int bk_num = Integer.parseInt(br.readLine());
					if (dao.checkBorrow(bk_num) == 1) {
						System.out.println("이미 대출 중입니다.");
						continue;
					}
					System.out.print("회원 번호: ");
					int me_num = Integer.parseInt(br.readLine());
					
					dao.borrowBook(bk_num, me_num);
					System.out.println("대출이 완료되었습니다.");
					// 도서 목록
				} else if (no == 2) { // MY대출목록
					System.out.print("회원 아이디: ");
					String me_id = br.readLine();
					
					dao.myBorrowList(me_id);
				} else if (no == 3) { // 대출도서 반납
					System.out.print("대출 번호: ");
					int re_num = Integer.parseInt(br.readLine());
					if (dao.returnCheck(re_num) == 0) {
						System.out.println("반납할 내역이 없습니다.");
						continue;
					}
					
					dao.updateReturn(re_num);
				} else if (no == 4) { // 종료
					System.out.println("프로그램 종료");
					break;
				} else {
					System.out.println("잘못 입력했습니다.");
				}
			} catch(NumberFormatException e) {
				System.out.println("숫자만 입력 가능");
			}
		}
	} // end of callMenu
	
	public static void main(String[] args) {
		new BookUserMain();
	}
}

// <미해결>
// 도서 대출할 시 존재하지 않는 도서 번호나 회원 번호를 입력하면 에러 발생
// MY대출목록에서 본인이 아닌 아이디를 조회해도 다른 사람 대출내역을 볼 수 있음
// 일단 로그인만 하면 다른 사용자가 대출한 책을 반납할 수 있음
// 회원번호를 조회해야 정보를 볼 수 있다는 점이 불편함. 회원가입 과정에서 회원번호를 알려주거나 아이디로 대출이나 대출이력을 조회하는 게 더 효율적임