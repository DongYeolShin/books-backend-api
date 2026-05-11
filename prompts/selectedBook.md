

## 1. 목적
- 선택된 책에 대한 정보를 전달하는 api 구현


## 2. 구성
- end-point : base URL 사용(/api/v1/books)
- Method : GET
- 파라메터
    - order-books : BOOK001,BOOK002...

## 3. 기능 구현
- order-books 를 콤마(,) 기준으로 bookId 리스트 만들기
- bookId 리스트에 있는 책 정보를 리턴 

## 4. 리턴 내용
- 책ID, 책제목, 원가격, 할인가격, 이미지 