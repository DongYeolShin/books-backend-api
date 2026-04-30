

## 1. 메뉴별 책 리스트 만들기 
- 선택된 메뉴 별로 알맞은 도서 리스트를 전달.
- 출력해야 할 도서의 카테고리가 front에서 넘어옴 

## 2. 설정
- end-point : 기본 URL 사용
- Method : GET
- 파라메터 :  category
  - web  >    book_category  매핑  category 테이블 slug 와 비교  
  - new >        books 컬럼에 is_new  Y
  - bestseller > books 컬럼에 is_bestseller Y
  - mobile >  book_category  매핑  category 테이블 slug 와 비교
  - basic >   book_category 매핑  category 테이블 slug 와 비교

## 3. 응답 데이터 
{
    "code" : 200,
     "data" :  {
          id, title, subtitle, 저자, 가격, 출판일
      },
      pageInfo : {
        "nowPageNum" : 1,
         "totalRows" : 50 
      }
}

## 4. 필요 테이블
- books, category, book_category

## 5. 데이터 순서 
- 출판일이 최신인 순서대로 출력
- 같을 경우 제목 순 

## 6. 필터 조건
- 저자 이름, 책 제목 
- 검색 단어가 대상 문자열에 포함되는 경우를 필터링 

## 7. 페이징 처리 
- 기본은 한페이지에 10개씩 보기 설정