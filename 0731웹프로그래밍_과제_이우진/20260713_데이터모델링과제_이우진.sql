-- ==========================================
-- 1. 기존 객체 삭제 (초기화용)                                                  -- 새 테이블 생성전 기존 테이블명이 중복될수  있으니 DROP 명령어로 삭제 실행
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
    NAME  VARCHAR2(50) NOT NULL,
    LANE_NUMBER NUMBER(2)    NOT NULL,  -- NUMBER(2) = 99 까지가능
    GAME_COUNT  NUMBER    DEFAULT 0 NOT NULL, -- 게임수는 NULL 불가
    GRADE       VARCHAR2(10) NOT NULL,  -- 등급은 NULL 불가 

    -- 기본키 제약조건
    CONSTRAINT PK_BOWLER PRIMARY KEY (BOWLER_ID),

    -- 데이터 무결성 검증을 위한 CHECK 제약조건
    CONSTRAINT CK_BOWLER_LANE  CHECK (LANE_NUMBER BETWEEN 1 AND 20), -- 레인은 1-20 번까지 제약.
    CONSTRAINT CK_BOWLER_GAMES CHECK (GAME_COUNT >= 0),         --게임수 제약
    CONSTRAINT CK_BOWLER_GRADE CHECK (GRADE IN ('일반', 'VIP'))   -- 회원등급 제약
);

-- ==========================================
-- 4. BILLING (결제 내역) 테이블 생성
-- ==========================================
CREATE TABLE BILLING (
    BILLING_ID   NUMBER,
    BOWLER_ID    NUMBER       NOT NULL,
    TOTAL_FEE    NUMBER       NOT NULL,
    PAYMENT_DATE DATE         DEFAULT SYSDATE NOT NULL,

    -- 기본키 제약조건
    CONSTRAINT PK_BILLING PRIMARY KEY (BILLING_ID),

    -- 외래키 제약조건 (부모 테이블인 BOWLER의 PK 참조)
    CONSTRAINT FK_BILLING_BOWLER FOREIGN KEY (BOWLER_ID)
        REFERENCES BOWLER (BOWLER_ID) ON DELETE CASCADE
);

-- ==========================================
-- 5. 가독성 및 이력 관리를 위한 코멘트(COMMENT) 등록
-- ==========================================
COMMENT ON TABLE BOWLER IS '볼링장 이용 회원 정보 테이블';
COMMENT ON COLUMN BOWLER.BOWLER_ID IS '볼러 고유 식별 번호 (PK)';
COMMENT ON COLUMN BOWLER.NAME IS '볼러 성명';
COMMENT ON COLUMN BOWLER.LANE_NUMBER IS '배정된 볼링 레인 번호 (1-20)';
COMMENT ON COLUMN BOWLER.GAME_COUNT IS '이용 및 누적된 게임 수';
COMMENT ON COLUMN BOWLER.GRADE IS '볼러 등급 (일반 / VIP)';
COMMENT ON TABLE BILLING IS '퇴실 시 정산되는 결제 이력 테이블';
COMMENT ON COLUMN BILLING.BILLING_ID IS '결제 고유 번호 (PK)';
COMMENT ON COLUMN BILLING.BOWLER_ID IS '결제를 진행한 볼러 번호 (FK)';
COMMENT ON COLUMN BILLING.TOTAL_FEE IS '등급 할인율이 적용된 최종 정산 금액';
COMMENT ON COLUMN BILLING.PAYMENT_DATE IS '정산 및 결제가 완료된 시간';