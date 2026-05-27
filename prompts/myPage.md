

## 1. 목적
- 로그인한 사용자 정보 API 

## 2. api  세팅
- end-point : /api/v1/users/me
- method : get
- data :
```
  {
    code : 200,
    data : {
       "myInfo" : {
         name : "김민준",
         birth : "1985.04.12",
         "gender" : "남자",
         "phone" : "010-2222-4444",
         "email" : "eveing2@naver.com",
         "address" : "서울시 은평구",
         "addressDetail" : "꿈마을아파트 101호"
       },
       "recentOrders" :[
           {
             "orderId" : "11012-222",
             "orderDate" : "2025-03-22",
             "title" : "IT서적",
             "author" "메리",
             "amount" :"34000",
             "status" : "배송중"
           }
       ]   
    }
  }
```

## 3.  보안 설정
- 로그인 사용자 접근 가능
- 로그인 인증은 기존 JWT 인증 