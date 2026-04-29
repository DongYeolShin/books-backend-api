# books 메인 페이지 api

## 1. Book API 
- 관련 클래스 만들 때 Books- 접두사 사용 
- 기본 end-point : /api/v1/books

## 2. 기능 정의

### 2.1  메인 페이지 리스트 
- method : GET
- end-point :  /api/v1/books/topn
- 매개변수 : X
- 데이터는  베스트셀러, 새로운책, 기본서  5 개씩 각각 출력 
- 데이터 정렬 : 출간 일이 최신 인것, 같다면 이름순 
- 결과 데이터 :
  ```
   {
      code : 200,
      data  : {
        "bestTopN" : [
          {
             "bookId": " ",
             "title" : " ",
             "author" : " ",
             "price" : 0
           }
        ],
        "newTopN" : [{
             "bookId": " ",
             "title" : " ",
             "author" : " ",
             "price" : 0
           }],
        "basicTopN" : [{
             "bookId": " ",
             "title" : " ",
             "author" : " ",
             "price" : 0
          }]
      }
   }
    ```

## 3.  보안 설정
- /api/v1/books/topn 로그인안해도 접근 가능 