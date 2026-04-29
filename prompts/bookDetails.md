# books 책 한권의 상세정보  api


## 1. 기능 정의
- 도서 한권에대한 정보를 제공하는 API
- 
### 2.1  메인 페이지 리스트 
- method : GET
- end-point :  /api/v1/books/{bookId}
- 매개변수 : bookId(PathVariable 로 처리 )
- 데이터는  bookId에 해당하는 책의 정보 
- 필요 데이터 : 아이디, 제목, 부제목, 저자, 출판사, 출간일, 원 가격, 할인가격 , 책 설명 , 리뷰 리스트 
- 결과 데이터 :
  ```
   {
      code : 200,
      data  : {
        "bookId" : "",
         "title" : :"" ,
         "subtitle" : "",
         "author" : "",
         "publisher" "",
         "publisher"
          "publishDate" : "",
          "originalPrice" : "",
          "salePrice" : "",
          "description"  : "",
          "stock"  : "",
          "reviewList" : []
      }
   }
    ```

## 3.  보안 설정
- 로그인 안해도 접근 가능 