insert into users(user_id, email, passwd, name, phone, gender, birth_date, postal_code ,
                  address, address_detail, created_at)
values('user01', 'user01@naver.com', '$2a$12$RceAGQkndLN8TczVRkOyxeyKTCEQ8B5sCXANuSaMqa9MEUoRbB7ee',
       '사용자1','010-2222-3333', '남성', '1996-02-10',
       06234 ,'서울시 강남구 테헤란로 123', '스타타워 10층 1001호', now());