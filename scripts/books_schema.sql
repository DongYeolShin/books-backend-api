-- =====================================================================
-- 온라인 도서판매 사이트 - 데이터베이스 DDL
-- =====================================================================
-- DBMS       : PostgreSQL 14+
-- Encoding   : UTF-8
-- 작성일     : 2026-04-23
-- 설명       : 학습용 프로토타입용 스키마
--              books, categories, users, orders, cart, reviews 포함
-- =====================================================================


-- =====================================================================
-- 0. 기존 객체 삭제 (재실행 대응)
-- =====================================================================
DROP TABLE IF EXISTS reviews         CASCADE;
DROP TABLE IF EXISTS cart_items      CASCADE;
DROP TABLE IF EXISTS order_items     CASCADE;
DROP TABLE IF EXISTS orders          CASCADE;
DROP TABLE IF EXISTS book_categories CASCADE;
DROP TABLE IF EXISTS categories      CASCADE;
DROP TABLE IF EXISTS books           CASCADE;
DROP TABLE IF EXISTS users           CASCADE;

DROP TYPE  IF EXISTS order_status    CASCADE;
DROP TYPE  IF EXISTS gender_type     CASCADE;


-- =====================================================================
-- 1. ENUM 타입 정의
-- =====================================================================
-- 주문 상태: 대기 → 결제완료 → 배송중 → 배송완료 / 취소
CREATE TYPE order_status AS ENUM (
    'pending',    -- 주문 대기 (결제 전)
    'paid',       -- 결제 완료
    'shipped',    -- 배송 중
    'delivered',  -- 배송 완료
    'cancelled'   -- 취소됨
);



-- =====================================================================
-- 2. books (도서 테이블)
-- =====================================================================
CREATE TABLE books (
    book_id         BIGSERIAL       PRIMARY KEY,
    title           VARCHAR(200)    NOT NULL,
    subtitle        VARCHAR(200),
    author          VARCHAR(100)    NOT NULL,
    publisher       VARCHAR(100),
    isbn            VARCHAR(20)     UNIQUE,
    description     TEXT,
    image_url       VARCHAR(500),
    original_price  INTEGER         NOT NULL CHECK (original_price >= 0),
    sale_price      INTEGER         NOT NULL CHECK (sale_price >= 0),
    stock           INTEGER         NOT NULL DEFAULT 0 CHECK (stock >= 0),
    page_count      INTEGER         CHECK (page_count >= 0),
    publish_date    DATE,
    is_bestseller   VARCHAR(1)         NOT NULL DEFAULT 'N',
    is_new          VARCHAR(1)         NOT NULL DEFAULT 'N',
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  books                 IS '도서 정보';
COMMENT ON COLUMN books.book_id         IS '도서 고유 ID';
COMMENT ON COLUMN books.title           IS '책 제목';
COMMENT ON COLUMN books.subtitle        IS '책 부제목';
COMMENT ON COLUMN books.author          IS '저자';
COMMENT ON COLUMN books.publisher       IS '출판사';
COMMENT ON COLUMN books.isbn            IS 'ISBN (도서 고유 식별자)';
COMMENT ON COLUMN books.description     IS '책 소개/내용';
COMMENT ON COLUMN books.image_url       IS '표지 이미지 경로';
COMMENT ON COLUMN books.original_price  IS '원가';
COMMENT ON COLUMN books.sale_price      IS '판매가';
COMMENT ON COLUMN books.stock           IS '재고 수량';
COMMENT ON COLUMN books.page_count      IS '페이지 수';
COMMENT ON COLUMN books.publish_date    IS '출판일';
COMMENT ON COLUMN books.is_bestseller   IS '베스트셀러 여부';
COMMENT ON COLUMN books.is_new          IS '신간 여부';

-- 인덱스: 메뉴별 조회를 위한 인덱스
CREATE INDEX idx_books_bestseller    ON books(is_bestseller) WHERE is_bestseller = 'Y';
CREATE INDEX idx_books_new           ON books(is_new)        WHERE is_new = 'Y';
CREATE INDEX idx_books_publish_date  ON books(publish_date DESC);
CREATE INDEX idx_books_title         ON books(title);


-- =====================================================================
-- 3. categories (카테고리 테이블)
-- =====================================================================
CREATE TABLE categories (
    category_id     SERIAL          PRIMARY KEY,
    name            VARCHAR(50)     NOT NULL UNIQUE,
    slug            VARCHAR(50)     NOT NULL UNIQUE,
    display_order   INTEGER         NOT NULL DEFAULT 0
);

COMMENT ON TABLE  categories               IS '도서 카테고리';
COMMENT ON COLUMN categories.category_id   IS '카테고리 ID';
COMMENT ON COLUMN categories.name          IS '카테고리명 (예: 웹프로그래밍)';
COMMENT ON COLUMN categories.slug          IS 'URL용 이름 (예: web)';
COMMENT ON COLUMN categories.display_order IS '메뉴 표시 순서';


-- =====================================================================
-- 4. book_categories (도서-카테고리 연결 테이블, N:M)
-- =====================================================================
CREATE TABLE book_categories (
    book_id         BIGINT          NOT NULL,
    category_id     INTEGER         NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id)     REFERENCES books(book_id)           ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)  ON DELETE CASCADE
);

COMMENT ON TABLE book_categories IS '도서-카테고리 매핑 (다대다 관계)';

CREATE INDEX idx_book_categories_category ON book_categories(category_id);


-- =====================================================================
-- 5. users (회원 테이블)
-- =====================================================================
CREATE TABLE users (
    user_id          BIGSERIAL       PRIMARY KEY,
    email            VARCHAR(100)    NOT NULL UNIQUE,
    passwd         VARCHAR(255)    NOT NULL,
    name             VARCHAR(50)     NOT NULL,
    phone            VARCHAR(20),
    gender           VARCHAR(20),
    birth_date       DATE            CHECK (birth_date <= CURRENT_DATE),
    postal_code      VARCHAR(10),
    address          VARCHAR(255),
    address_detail   VARCHAR(255),
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
 
COMMENT ON TABLE  users                 IS '회원 정보';
COMMENT ON COLUMN users.user_id         IS '회원 ID';
COMMENT ON COLUMN users.email           IS '이메일 (로그인 ID)';
COMMENT ON COLUMN users.passwd          IS '암호화된 비밀번호 (bcrypt 등)';
COMMENT ON COLUMN users.name            IS '회원 이름';
COMMENT ON COLUMN users.phone           IS '전화번호';
COMMENT ON COLUMN users.gender          IS '성별 (남자/여자)';
COMMENT ON COLUMN users.birth_date      IS '생년월일';
COMMENT ON COLUMN users.postal_code     IS '우편번호';
COMMENT ON COLUMN users.address         IS '기본 주소 (도로명/지번)';
COMMENT ON COLUMN users.address_detail  IS '상세 주소 (동/호수 등)';
 


-- =====================================================================
-- 6. orders (주문 테이블)
-- =====================================================================
CREATE TABLE orders (
    order_id          BIGSERIAL      PRIMARY KEY,
    user_id           BIGINT         NOT NULL,
    total_amount      INTEGER        NOT NULL CHECK (total_amount >= 0),
    status            order_status   NOT NULL DEFAULT 'pending',
    shipping_address  VARCHAR(255)   NOT NULL,
    ordered_at        TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

COMMENT ON TABLE  orders                    IS '주문 마스터';
COMMENT ON COLUMN orders.order_id           IS '주문 ID';
COMMENT ON COLUMN orders.user_id            IS '주문자 ID';
COMMENT ON COLUMN orders.total_amount       IS '총 결제 금액';
COMMENT ON COLUMN orders.status             IS '주문 상태';
COMMENT ON COLUMN orders.shipping_address   IS '배송지 주소';
COMMENT ON COLUMN orders.ordered_at         IS '주문 일시';

CREATE INDEX idx_orders_user       ON orders(user_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_orders_ordered_at ON orders(ordered_at DESC);


-- =====================================================================
-- 7. order_items (주문 상세 테이블)
-- =====================================================================
CREATE TABLE order_items (
    order_item_id       BIGSERIAL   PRIMARY KEY,
    order_id            BIGINT      NOT NULL,
    book_id             BIGINT      NOT NULL,
    quantity            INTEGER     NOT NULL CHECK (quantity > 0),
    price_at_purchase   INTEGER     NOT NULL CHECK (price_at_purchase >= 0),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id)  REFERENCES books(book_id)   ON DELETE RESTRICT
);

COMMENT ON TABLE  order_items                      IS '주문 상세 (주문에 담긴 책 목록)';
COMMENT ON COLUMN order_items.order_item_id        IS '주문 상세 ID';
COMMENT ON COLUMN order_items.order_id             IS '주문 ID';
COMMENT ON COLUMN order_items.book_id              IS '도서 ID';
COMMENT ON COLUMN order_items.quantity             IS '주문 수량';
COMMENT ON COLUMN order_items.price_at_purchase    IS '구매 시점의 가격 (책 가격이 바뀌어도 주문 내역은 유지)';

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_book  ON order_items(book_id);


-- =====================================================================
-- 8. cart_items (장바구니 테이블)
-- =====================================================================
CREATE TABLE cart_items (
    cart_item_id    BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    book_id         BIGINT          NOT NULL,
    quantity        INTEGER         NOT NULL DEFAULT 1 CHECK (quantity > 0),
    added_at        TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE (user_id, book_id)  -- 한 회원이 같은 책을 중복으로 담지 못하게
);

COMMENT ON TABLE  cart_items              IS '장바구니';
COMMENT ON COLUMN cart_items.cart_item_id IS '장바구니 항목 ID';
COMMENT ON COLUMN cart_items.user_id      IS '회원 ID';
COMMENT ON COLUMN cart_items.book_id      IS '도서 ID';
COMMENT ON COLUMN cart_items.quantity     IS '수량';

CREATE INDEX idx_cart_items_user ON cart_items(user_id);


-- =====================================================================
-- 9. reviews (리뷰 테이블)
-- =====================================================================
CREATE TABLE reviews (
    review_id       BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    book_id         BIGINT          NOT NULL,
    rating          SMALLINT        NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content         TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE (user_id, book_id)  -- 회원당 책 1권에 리뷰 1개
);

COMMENT ON TABLE  reviews            IS '도서 리뷰';
COMMENT ON COLUMN reviews.review_id  IS '리뷰 ID';
COMMENT ON COLUMN reviews.rating     IS '별점 (1~5)';
COMMENT ON COLUMN reviews.content    IS '리뷰 내용';

CREATE INDEX idx_reviews_book ON reviews(book_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);


