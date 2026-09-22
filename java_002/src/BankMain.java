/**
 * BankAccount 설계도로 실제 객체를 만들어 사용해보는 실행 클래스
 */
public class BankMain {

    public static void main(String[] args) {

        // 1. 객체 생성 — new 클래스명(...) 으로 설계도에서 실제 계좌를 찍어낸다
        BankAccount woojin = new BankAccount("110-123-456789", "이우진", 100000);
        BankAccount friend = new BankAccount("220-987-654321", "김철수");   // 잔액 0원

        System.out.println("==== 계좌 개설 ====");
        woojin.printInfo();
        friend.printInfo();
        System.out.println("개설된 계좌 수: " + BankAccount.getAccountCount() + "개");

        // 2. 메서드 호출 — 객체.메서드() 로 각 객체에게 일을 시킨다
        System.out.println("\n==== 입금 / 출금 ====");
        woojin.deposit(50000);
        woojin.withdraw(30000);
        woojin.withdraw(500000);   // 잔액보다 많이 → 실패
        friend.deposit(-1000);     // 음수 입금 → 실패

        // 3. 객체끼리 협력 — 한 객체가 다른 객체를 매개변수로 받아 사용
        System.out.println("\n==== 이체 ====");
        woojin.transfer(friend, 70000);
        friend.transfer(woojin, 1000000);   // 잔액 부족 → 이체 실패

        // 4. 두 객체는 같은 설계도로 만들었지만 데이터는 각자 따로 가진다
        System.out.println("\n==== 최종 잔액 ====");
        woojin.printInfo();
        friend.printInfo();

        // 5. private 필드는 밖에서 직접 바꿀 수 없다
        // woojin.balance = 999999999;   // 컴파일 에러! → getter 로 읽기만 가능
        System.out.println("\n" + woojin.getOwner() + "님 잔액 확인: " + woojin.getBalance() + "원");
    }
}
