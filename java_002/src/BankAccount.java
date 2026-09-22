/**
 * 은행 계좌 클래스 (설계도)
 * - 실제 은행 계좌에서 "가지고 있는 정보"는 필드로,
 *   "할 수 있는 일"은 메서드로 옮겼다.
 */
public class BankAccount {

    // ===== 1. 필드 (객체가 가지는 데이터) =====
    // private : 클래스 밖에서 잔액을 마음대로 바꾸지 못하게 숨긴다 (캡슐화)
    private String accountNumber;   // 계좌번호
    private String owner;           // 예금주
    private long balance;           // 잔액 (원)

    // static : 객체마다 따로가 아니라 "클래스 전체가 공유"하는 값
    private static int accountCount = 0;   // 지금까지 만들어진 계좌 수

    // ===== 2. 생성자 (객체를 만들 때 딱 한 번 실행) =====
    // 잔액 없이 계좌만 만드는 경우
    public BankAccount(String accountNumber, String owner) {
        this(accountNumber, owner, 0);   // 아래 생성자를 호출 (생성자 오버로딩)
    }

    // 처음부터 돈을 넣고 계좌를 만드는 경우
    public BankAccount(String accountNumber, String owner, long balance) {
        this.accountNumber = accountNumber;   // this.필드 = 매개변수
        this.owner = owner;
        this.balance = balance;
        accountCount++;
    }

    // ===== 3. 메서드 (객체가 할 수 있는 일) =====

    /** 입금 */
    public void deposit(long amount) {
        if (amount <= 0) {
            System.out.println("[입금 실패] 0원 이하는 입금할 수 없습니다.");
            return;
        }
        balance += amount;
        System.out.printf("[입금] %s님 %,d원 입금 → 잔액 %,d원%n", owner, amount, balance);
    }

    /** 출금 — 성공하면 true, 실패하면 false */
    public boolean withdraw(long amount) {
        if (amount <= 0) {
            System.out.println("[출금 실패] 0원 이하는 출금할 수 없습니다.");
            return false;
        }
        if (amount > balance) {
            System.out.printf("[출금 실패] %s님 잔액 부족 (요청 %,d원 / 잔액 %,d원)%n",
                              owner, amount, balance);
            return false;
        }
        balance -= amount;
        System.out.printf("[출금] %s님 %,d원 출금 → 잔액 %,d원%n", owner, amount, balance);
        return true;
    }

    /** 이체 — 다른 계좌 객체를 매개변수로 받는다 */
    public void transfer(BankAccount target, long amount) {
        System.out.printf("[이체] %s → %s, %,d원%n", owner, target.owner, amount);
        if (withdraw(amount)) {     // 내 계좌에서 출금이 성공했을 때만
            target.deposit(amount); // 상대 계좌에 입금
        }
    }

    /** 계좌 정보 출력 */
    public void printInfo() {
        System.out.printf("계좌번호: %s | 예금주: %s | 잔액: %,d원%n",
                          accountNumber, owner, balance);
    }

    // ===== 4. getter =====
    // 잔액은 읽을 수만 있고, 바꾸는 건 deposit / withdraw 로만 가능하다.
    // 그래서 setBalance() 는 일부러 만들지 않았다.
    public long getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    public static int getAccountCount() {
        return accountCount;
    }
}
