-- ==========================================
-- 1. 기존 객체 삭제 (초기화용)
-- ==========================================
DROP TABLE BILLING CASCADE CONSTRAINTS;
DROP TABLE BOWLER CASCADE CONSTRAINTS;
DROP SEQUENCE SEQ_BOWLER_ID;
DROP SEQUENCE SEQ_BILLING_ID;

-- ==========================================
-- 2. 자동 증가 시퀀스 생성
-- ==========================================
CREATE SEQUENCE SEQ_BOWLER_ID START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_BILLING_ID START WITH 1 INCREMENT BY 1 NOCACHE;

-- ==========================================
-- 3. BOWLER (볼러 회원) 테이블 생성
-- ==========================================
CREATE TABLE BOWLER (
    BOWLER_ID   NUMBER,
    NAME        VARCHAR2(50) NOT NULL,
    LANE_NUMBER NUMBER(2) NOT NULL,
    GAME_COUNT  NUMBER DEFAULT 0 NOT NULL,
    GRADE       VARCHAR2(10) NOT NULL,
    STATUS      CHAR(1) DEFAULT 'Y' NOT NULL, -- 이용중('Y'), 퇴실완료('N')
    
    CONSTRAINT PK_BOWLER PRIMARY KEY (BOWLER_ID),
    CONSTRAINT CK_BOWLER_LANE  CHECK (LANE_NUMBER BETWEEN 1 AND 20),
    CONSTRAINT CK_BOWLER_GAMES CHECK (GAME_COUNT >= 0),
    CONSTRAINT CK_BOWLER_GRADE CHECK (GRADE IN ('일반', 'VIP')),
    CONSTRAINT CK_BOWLER_STATUS CHECK (STATUS IN ('Y', 'N'))
);

-- ==========================================
-- 4. BILLING (결제 내역) 테이블 생성
-- ==========================================
CREATE TABLE BILLING (
    BILLING_ID   NUMBER,
    BOWLER_ID    NUMBER NOT NULL,
    TOTAL_FEE    NUMBER NOT NULL,
    PAYMENT_DATE DATE DEFAULT SYSDATE NOT NULL,
    
    CONSTRAINT PK_BILLING PRIMARY KEY (BILLING_ID),
    CONSTRAINT FK_BILLING_BOWLER FOREIGN KEY (BOWLER_ID) REFERENCES BOWLER (BOWLER_ID) ON DELETE CASCADE
);

-- ==========================================
-- 5. ★ 대용량 데이터 대응을 위한 인덱스(INDEX) 생성 ★
-- ==========================================
-- ① 회원 테이블: STATUS가 'Y'인 활성화 회원만 초고속으로 조회하는 인덱스
CREATE INDEX IDX_BOWLER_STATUS ON BOWLER(STATUS, BOWLER_ID DESC);

-- ② 정산 테이블: 데이터가 수억 건 쌓여도 최근 결제 이력 순으로 초고속 렌더링하는 인덱스
CREATE INDEX IDX_BILLING_DATE ON BILLING(PAYMENT_DATE DESC, BILLING_ID DESC);


-- ==========================================
-- 6. 가독성을 위한 코멘트(COMMENT) 등록
-- ==========================================
COMMENT ON TABLE  BOWLER             IS '볼링장 이용 회원 정보 테이블';
COMMENT ON COLUMN BOWLER.BOWLER_ID   IS '볼러 고유 식별 번호 (PK)';
COMMENT ON COLUMN BOWLER.STATUS      IS '회원 이용 상태 (Y: 이용중 / N: 퇴실완료)';
COMMENT ON TABLE  BILLING            IS '퇴실 시 정산되는 결제 이력 테이블';


/*
  기존 외래키 제약조건을  삭제 한게 아니라 ON DELETE CASCADE(연쇄 삭제) 옵션만 제거
  
  ON DELETE CASCADE를 뺐는데 자바에서 삭제가 안 될까요? (진짜 핵심 원인)
  
  자바 코드(MemberPayServlet)의 처리 순서가 오라클의 기본 무결성 원칙과 충돌하기 때문
  
  현재 자바 코드는 한 트랜잭션 안에서 아래 순서로 작동
  insertBilling 실행 ?? 자식 데이터 생성
  deleteMember 실행 ?? 부모 데이터 삭제 시도
  
  오라클의 기본 외래키 원칙은 "자식(BILLING)이 부모(BOWLER_ID)를 참조하고 있는 상태에서는 부모를 절대 먼저 지울 수 없다"
  
  ON DELETE CASCADE가 있었을 때: 부모를 지우면 자식까지 오라클이 자동으로 같이 지워버려서 무결성을 유지
  
  ON DELETE CASCADE를 뺐을 때: 자식이 부모를 꽉 붙잡고 있으니, 오라클이 부모를 지우지 못하게 막아버려서 자바의 deleteMember 쿼리가 무시되고 회원이 목록에 그대로 남아있었던 것
  
  데이터 무결성을 지키면서 완벽하게 해결하려면 오라클 외래키 옵션을 ON DELETE SET NULL로 바꾸거나, 회원 테이블에 STATUS(상태) 컬럼을 추가하는 것
  
    해결책 A: ON DELETE SET NULL 사용 
  
  회원(BOWLER)이 지워지면 결제 장부(BILLING)의 회원 번호 자리만 NULL(빈칸)로 바꾸고, 결제 금액과 장부 데이터는 그대로 보존하는 옵션
  
  무결성도 유지되고 장부도 유지됨
  
    해결책 B: STATUS(상태) 컬럼 도입
  
  회원 테이블에 STATUS 컬럼을 만들고, 이용 중일 때는 'Y', 퇴실(정산완료) 시에는 'N'으로 업데이트(UPDATE)
  
  MemberListServlet에서는 STATUS = 'Y'인 회원만 조회해서 화면에 뿌립니다. (정산 누르면 목록에서 자연스럽게 사라짐)
  
  장부 무결성: 부모 데이터가 DB에 완전히 살아있으므로 외래키 무결성이 1%도 훼손되지 않고 완벽하게 보존됨
  
  결제 및 정산 데이터(BILLING)는 매출 증빙과 이력 관리를 위해 영구 보존되어야 하므로 누적이 불가피
  
  오라클 인덱스(INDEX) 최적화 및 월별/연도별 테이블 파티셔닝(Partitioning) 전략을 적용
*/
