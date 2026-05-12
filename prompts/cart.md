
## 1. 기능
- 장바구니 등록 API 만들기 

## 2. API 구성
- end-point : /api/v1/carts
- method : POST
- 파라메터 양식 : application/json
- 파라메터 :
  ```
    {
       bookId : BOOK001,
       quantity : 1
    }
  ```
- 결과 :
    - 성공 시 : { "code" : 200 , "message" : "장바구니 등록이 되었습니다"}
    - 실패 시 : { "code" : 500 , "message" : "장바구니 등록이 실패되었습니다."}

  
## 3. 기능 설명
- 로그인한 사용자가 장바구니 등록을 요청하면 DB에 등록 후 결과 리턴
- 비 로그인 상태에서는 접근 불가
- 등록 시 해당 사용자의 장바구니에 동일한 책(bookId)이 이미 존재하는지 확인한다.
  - 존재하면 새로 등록하지 않고, 기존 항목의 수량(quantity)만 요청 수량만큼 증가시킨다.
  - 존재하지 않으면 신규 항목으로 등록한다.

## 4. 구현
- 필요한 controller, service, entity, repository 생성
- 불필요한 코드 수정 금지 
- script/books_schema.sql  파일을 참고


