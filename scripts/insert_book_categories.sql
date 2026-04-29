-- =====================================================
-- book_categories (책 - 카테고리 매핑) INSERT
--
-- 카테고리: 1=기본서, 2=모바일, 3=웹프로그래밍
-- =====================================================

-- [기본서] category_id = 1 -----------------------------
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK001', 1); -- 클린 코드
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK002', 1); -- 실용주의 프로그래머
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK003', 1); -- 클린 아키텍처
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK004', 1); -- 리팩터링
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK005', 1); -- 이펙티브 자바
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK006', 1); -- GOF의 디자인 패턴
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK007', 1); -- 모던 자바스크립트 Deep Dive
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK008', 1); -- 혼자 공부하는 파이썬
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK009', 1); -- Do it! 자료구조와 알고리즘
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK010', 1); -- 객체지향의 사실과 오해

-- [웹프로그래밍] category_id = 3 -----------------------
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK011', 3); -- 모던 리액트 Deep Dive
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK012', 3); -- 리액트를 다루는 기술
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK013', 3); -- Node.js 교과서
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK014', 3); -- 스프링 부트 3 핵심 가이드
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK015', 3); -- 자바 ORM 표준 JPA
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK016', 3); -- HTTP 완벽 가이드
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK017', 3); -- Vue.js 코딩 공작소
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK018', 3); -- 실전에서 바로 쓰는 Next.js
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK019', 3); -- 스프링 인 액션
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK020', 3); -- 스프링 부트 3 백엔드 개발자 되기

-- [모바일] category_id = 2 -----------------------------
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK021', 2); -- 이것이 안드로이드다 with 코틀린
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK022', 2); -- Do it! 깡샘의 안드로이드 앱 프로그래밍
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK023', 2); -- Kotlin in Action 2판
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK024', 2); -- 핵심만 골라 배우는 SwiftUI
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK025', 2); -- 처음 배우는 플러터 앱 개발
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK026', 2); -- 리액트 네이티브를 다루는 기술
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK027', 2); -- 아키텍처를 알아야 앱 개발이 보인다
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK028', 2); -- 스위프트 프로그래밍 4판
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK029', 2); -- 젯팩 컴포즈로 개발하는 안드로이드 UI
INSERT INTO book_categories (book_id, category_id) VALUES ('BOOK030', 2); -- Do it! 플러터 앱 프로그래밍


-- =====================================================
-- 검증 쿼리 (실행 후 확인용)
-- =====================================================

-- 카테고리별 책 개수 확인 (각 10건이어야 함)
-- SELECT c.category_id, c.name, COUNT(bc.book_id) AS book_count
-- FROM categories c
-- LEFT JOIN book_categories bc ON bc.category_id = c.category_id
-- GROUP BY c.category_id, c.name
-- ORDER BY c.display_order;

-- 매핑 안 된 책 확인 (없어야 함)
-- SELECT b.book_id, b.title FROM books b
-- LEFT JOIN book_categories bc ON bc.book_id = b.book_id
-- WHERE bc.book_id IS NULL;

-- 카테고리별 책 목록 보기
SELECT c.name AS category, b.book_id, b.title, b.author
FROM book_categories bc
JOIN books b      ON b.book_id = bc.book_id
JOIN categories c ON c.category_id = bc.category_id
ORDER BY c.display_order, b.book_id;
