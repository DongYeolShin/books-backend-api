-- =====================================================
-- IT 서적 데이터 INSERT (총 30권)
-- 카테고리: 기본서(10) / 웹프로그래밍(10) / 모바일(10)
-- 이미지 URL: 알라딘(image.aladin.co.kr) 실제 표지 사진
--
-- ※ books.book_id 는 VARCHAR(100) 입니다.
--   값은 'BOOK001' ~ 'BOOK030' 으로 부여했습니다.
--
-- 참고: 테이블 스키마가 BIGSERIAL 이라면 아래처럼 변경 필요
--   ALTER TABLE books DROP CONSTRAINT books_pkey CASCADE;
--   ALTER TABLE books ALTER COLUMN book_id DROP DEFAULT;
--   ALTER TABLE books ALTER COLUMN book_id TYPE VARCHAR(100);
--   ALTER TABLE books ADD PRIMARY KEY (book_id);
-- =====================================================

-- ============================
-- [1] 기본서 (Programming Fundamentals) - 10권
-- ============================

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK001', '클린 코드', '애자일 소프트웨어 장인 정신', '로버트 C. 마틴', '인사이트', '9788966260959',
        '읽기 좋은 코드를 작성하는 방법과 원칙을 다룬 프로그래밍 명저. 깨끗한 코드를 작성하기 위한 다양한 사례와 실전 기법을 제시한다.',
        'https://image.aladin.co.kr/product/3408/36/cover200/8966260950_2.jpg', 33000, 29700, 50, 584, '2013-12-24', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK002', '실용주의 프로그래머', '20주년 기념판', '데이비드 토머스, 앤드류 헌트', '인사이트', '9788966263363',
        '프로그래밍 분야의 고전으로 불리는 책. 실용주의 철학과 실천법을 통해 더 나은 개발자가 되는 길을 제시한다.',
        'https://image.aladin.co.kr/product/28878/64/cover200/8966263364_1.jpg', 30000, 27000, 30, 444, '2022-02-21', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK003', '클린 아키텍처', '소프트웨어 구조와 설계의 원칙', '로버트 C. 마틴', '인사이트', '9788966262472',
        '소프트웨어 아키텍처의 보편적인 규칙을 설명하며 좋은 설계와 나쁜 설계의 차이를 명확히 보여준다.',
        'https://image.aladin.co.kr/product/20232/24/cover200/8966262473_1.jpg', 32000, 28800, 40, 432, '2019-08-20', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK004', '리팩터링', '2판', '마틴 파울러', '한빛미디어', '9791162242742',
        '리팩터링 분야의 바이블. 코드 구조를 안전하게 개선하는 다양한 기법과 패턴을 제공한다.',
        'https://image.aladin.co.kr/product/23618/61/cover200/k932638523_1.jpg', 40000, 36000, 25, 568, '2020-04-01', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK005', '이펙티브 자바', 'Effective Java 3판', '조슈아 블로크', '인사이트', '9788966262281',
        '자바 프로그래머라면 반드시 읽어야 할 필독서. 90가지 핵심 아이템을 통해 자바를 효과적으로 사용하는 법을 알려준다.',
        'https://image.aladin.co.kr/product/17119/64/cover200/8966262287_1.jpg', 36000, 32400, 35, 580, '2018-11-01', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK006', 'GOF의 디자인 패턴', '재사용성을 지닌 객체지향 소프트웨어의 핵심요소', '에릭 감마, 리처드 헬름, 랄프 존슨, 존 블리시디스', '프로텍미디어', '9791195444953',
        '디자인 패턴의 정석으로 불리는 GoF의 디자인 패턴. 23가지 디자인 패턴을 상세히 설명한다.',
        'https://image.aladin.co.kr/product/5605/15/cover200/6000835492_1.jpg', 32000, 28800, 20, 480, '2015-03-26', 'N', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK007', '모던 자바스크립트 Deep Dive', '자바스크립트의 기본 개념과 동작 원리', '이웅모', '위키북스', '9791158392239',
        '자바스크립트의 기본부터 핵심 개념까지 깊이 있게 다룬 책. 자바스크립트의 동작 원리를 명확히 이해할 수 있다.',
        'https://image.aladin.co.kr/product/25155/25/cover200/k282633473_1.jpg', 45000, 40500, 60, 1056, '2020-09-25', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK008', '혼자 공부하는 파이썬', '1:1 과외하듯 배우는 프로그래밍 자습서', '윤인성', '한빛미디어', '9791169212304',
        '파이썬 입문자를 위한 친절한 가이드. 혼자서도 충분히 학습할 수 있도록 구성되어 있다.',
        'https://image.aladin.co.kr/product/29499/67/cover200/k412837047_1.jpg', 24000, 21600, 70, 504, '2022-12-26', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK009', 'Do it! 자료구조와 함께 배우는 알고리즘 입문', '자바 편', '시바타 보요', '이지스퍼블리싱', '9791163033219',
        '자료구조와 알고리즘의 기본 개념을 자바를 통해 익힐 수 있는 입문서.',
        'https://image.aladin.co.kr/product/29422/98/cover200/k552837322_1.jpg', 25000, 22500, 45, 460, '2022-01-10', 'N', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK010', '객체지향의 사실과 오해', '역할, 책임, 협력 관점에서 본 객체지향', '조영호', '위키북스', '9791158390662',
        '객체지향 프로그래밍의 본질을 협력하는 객체들의 공동체라는 관점에서 풀어낸 책.',
        'https://image.aladin.co.kr/product/6055/2/cover200/8998139766_1.jpg', 18000, 16200, 30, 264, '2015-06-17', 'Y', 'N');


-- ============================
-- [2] 웹프로그래밍 (Web Programming) - 10권
-- ============================

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK011', '모던 리액트 Deep Dive', '리액트의 핵심 개념과 동작 원리부터 Next.js까지', '김용찬', '위키북스', '9791158393755',
        '리액트의 내부 동작 원리부터 Next.js, 테스트, 배포까지 실무에 필요한 모든 것을 다룬다.',
        'https://image.aladin.co.kr/product/32749/91/cover200/k542936989_1.jpg', 45000, 40500, 55, 940, '2024-02-26', 'Y', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK012', '리액트를 다루는 기술', '개정판', '김민준', '길벗', '9791165215477',
        '리액트의 핵심 개념을 빠짐없이 다루는 입문서. 실습 위주로 리액트를 배울 수 있다.',
        'https://image.aladin.co.kr/product/20481/95/cover200/k662635453_1.jpg', 38000, 34200, 40, 800, '2020-08-31', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK013', 'Node.js 교과서', '기본기에 충실한 Node.js 입문서 개정 3판', '조현영', '길벗', '9791140703944',
        'Node.js의 기본부터 Express, Sequelize, Socket.IO까지 백엔드 개발에 필요한 핵심 기술을 다룬다.',
        'https://image.aladin.co.kr/product/30678/38/cover200/k552830138_1.jpg', 36000, 32400, 50, 716, '2022-12-30', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK014', '스프링 부트 3 핵심 가이드', '스프링 부트 3을 활용한 애플리케이션 개발 실무', '장정우', '위키북스', '9791158395469',
        '스프링 부트 3 기반으로 RESTful API를 개발하는 방법을 처음부터 차근차근 안내한다.',
        'https://image.aladin.co.kr/product/36096/9/cover200/k052038969_1.jpg', 32000, 28800, 35, 528, '2024-09-30', 'Y', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK015', '자바 ORM 표준 JPA 프로그래밍', NULL, '김영한', '에이콘출판', '9788960777330',
        'JPA의 기본 개념부터 실무에서 사용하는 고급 기능까지 한 권에 담은 JPA 표준서.',
        'https://image.aladin.co.kr/product/6268/14/cover200/8960777331_1.jpg', 43000, 38700, 40, 700, '2015-07-31', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK016', 'HTTP 완벽 가이드', '웹은 어떻게 동작하는가', '데이빗 고울리, 브라이언 토티 외', '인사이트', '9788966261208',
        'HTTP 프로토콜의 모든 것을 다룬 명저. 웹 개발자라면 반드시 알아야 할 HTTP의 깊이 있는 내용을 담고 있다.',
        'https://image.aladin.co.kr/product/4973/15/cover200/8966261205_1.jpg', 50000, 45000, 20, 856, '2014-12-24', 'N', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK017', 'Vue.js 코딩 공작소', '예제로 배우는 Vue.js 3', '이재훈', '비제이퍼블릭', '9791165921095',
        'Vue.js 3의 핵심 개념과 Composition API를 실습 예제로 익힐 수 있는 입문서.',
        'https://image.aladin.co.kr/product/20782/66/cover200/k452636666_1.jpg', 28000, 25200, 25, 408, '2021-12-30', 'N', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK018', '실전에서 바로 쓰는 Next.js', NULL, '미셸 리델, 칼 리스', '한빛미디어', '9791169211857',
        '실무에서 바로 활용할 수 있는 Next.js 기반의 React 애플리케이션 개발 가이드.',
        'https://image.aladin.co.kr/product/30844/87/cover200/k382831471_1.jpg', 30000, 27000, 30, 388, '2023-01-31', 'N', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK019', '스프링 인 액션', 'Spring in Action 6판', '크레이그 월즈', '제이펍', '9791192469829',
        '스프링 프레임워크의 핵심을 다양한 실전 예제와 함께 학습할 수 있는 스프링 입문서의 결정판.',
        'https://image.aladin.co.kr/product/23975/50/cover200/k602639016_1.jpg', 42000, 37800, 25, 680, '2023-08-31', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK020', '스프링 부트 3 백엔드 개발자 되기', '자바 편 2판', '신선영', '골든래빗', '9791191905137',
        '스프링 부트 3 기반의 백엔드 개발자가 되기 위한 실전 가이드.',
        'https://image.aladin.co.kr/product/33692/57/cover200/k792939139_1.jpg', 32000, 28800, 45, 504, '2024-04-30', 'Y', 'Y');


-- ============================
-- [3] 모바일 (Mobile) - 10권
-- ============================

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK021', '이것이 안드로이드다 with 코틀린', '개정 3판', '고돈호', '한빛미디어', '9791169211093',
        '안드로이드 입문자를 위한 베스트셀러. 코틀린 기반의 안드로이드 앱 개발 전반을 다룬다.',
        'https://image.aladin.co.kr/product/28591/67/cover200/k002835759_1.jpg', 36000, 32400, 50, 856, '2022-12-30', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK022', 'Do it! 깡샘의 안드로이드 앱 프로그래밍 with 코틀린', '개정 4판', '강성윤', '이지스퍼블리싱', '9791163033813',
        '안드로이드 앱 개발을 코틀린으로 시작하는 입문자를 위한 친절한 가이드.',
        'https://image.aladin.co.kr/product/38888/59/cover200/k382137406_1.jpg', 36000, 32400, 40, 712, '2025-01-27', 'Y', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK023', 'Kotlin in Action', '2판', '드미트리 제메로프, 스베트라나 이사코바, 로만 엘리자로프', '에이콘출판', '9791161759999',
        '코틀린의 핵심을 자바 개발자의 시각에서 짚어주는 코틀린 정석서 2판.',
        'https://image.aladin.co.kr/product/35809/98/cover200/k202036651_1.jpg', 40000, 36000, 30, 528, '2024-08-30', 'Y', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK024', '핵심만 골라 배우는 SwiftUI 기반의 iOS 프로그래밍', NULL, '닐 스미스', '제이펍', '9791192469041',
        'SwiftUI를 활용한 iOS 앱 개발의 핵심 내용을 다룬 종합 가이드.',
        'https://image.aladin.co.kr/product/32419/80/cover200/k472935992_1.jpg', 38000, 34200, 25, 692, '2023-12-29', 'N', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK025', '처음 배우는 플러터 앱 개발', '간단한 예제부터 실전 프로젝트까지', '쩐 후 카잉', '한빛미디어', '9791169213004',
        '플러터를 이용해 iOS와 안드로이드용 크로스 플랫폼 앱을 만드는 방법을 다룬 입문서.',
        'https://image.aladin.co.kr/product/35234/47/cover200/k242934848_1.jpg', 32000, 28800, 35, 524, '2024-06-28', 'N', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK026', '리액트 네이티브를 다루는 기술', '하이브리드 모바일 앱 개발', '김범준', '길벗', '9791165217693',
        '리액트 네이티브로 iOS와 안드로이드 앱을 동시에 개발하는 방법을 다룬 실전 가이드.',
        'https://image.aladin.co.kr/product/28198/28/cover200/k752835873_1.jpg', 38000, 34200, 30, 712, '2022-09-30', 'Y', 'N');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK027', '아키텍처를 알아야 앱 개발이 보인다', '안드로이드 앱 아키텍처 입문', '옥수환', '제이펍', '9791192987545',
        '실무에서 사용하는 안드로이드 앱 아키텍처 패턴을 명확하게 정리한 책.',
        'https://image.aladin.co.kr/product/23752/95/cover200/k182639969_1.jpg', 28000, 25200, 25, 392, '2024-02-29', 'N', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK028', '스위프트 프로그래밍', '4판', '야곰', '한빛미디어', '9791169212113',
        'iOS 개발자라면 반드시 알아야 할 스위프트 언어를 자세히 다룬 입문서 4판.',
        'https://image.aladin.co.kr/product/36471/14/cover200/k522039911_1.jpg', 38000, 34200, 35, 568, '2024-10-31', 'Y', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK029', '젯팩 컴포즈로 개발하는 안드로이드 UI', NULL, '토마스 쿤네스', '에이콘출판', '9791161758879',
        '안드로이드 최신 UI 툴킷인 Jetpack Compose를 활용한 모던 안드로이드 UI 개발법.',
        'https://image.aladin.co.kr/product/30709/49/cover200/k102830240_1.jpg', 36000, 32400, 30, 612, '2023-02-28', 'N', 'Y');

INSERT INTO books (book_id, title, subtitle, author, publisher, isbn, description, image_url, original_price, sale_price, stock, page_count, publish_date, is_bestseller, is_new)
VALUES ('BOOK030', 'Do it! 플러터 앱 프로그래밍', '오픈소스 기반의 크로스 플랫폼 앱 개발', '조준수', '이지스퍼블리싱', '9791163033745',
        '플러터의 기본부터 실전 앱 개발까지 한 권으로 끝내는 입문서.',
        'https://image.aladin.co.kr/product/27849/87/cover200/k342734681_1.jpg', 30000, 27000, 40, 416, '2022-08-25', 'N', 'N');

-- =====================================================
-- 끝 (총 30건)
-- =====================================================
