# 더파이러츠 API

## 개발환경
- JDK 11
- Java Spring  
- JPA
- in-Memory H2
- Swagger

## 접속 정보
- port : 8080
- swagger : http://localhost:8080/swagger-ui.html
- h2 : http://localhost:8080/h2-console

## 테이블 생성 SQL
__** 현재 JPA create 설정으로 테이블이 자동 생성 됩니다.__

create table business_time (
store_id bigint not null,
close time,
day varchar(255),
open time
);

create table holiday (
store_id bigint not null,
holiday date
);

create table store (
id bigint generated by default as identity,
address varchar(255),
description varchar(255),
level integer not null,
name varchar(255),
owner varchar(255),
phone varchar(255),
primary key (id)
);

alter table business_time
add constraint FKdpgpgp256opvbyypitxeinul2
foreign key (store_id)
references store;

alter table holiday
add constraint FKctnbttj6fpcmmwua9o7fst6t4
foreign key (store_id)
references store;

## API 사용 가이드
__** swagger에서 간편하게 테스트 해보실 수 있습니다.__

### 1. 점포 추가 
- 두번째 점포 해적수산의 경우 영업 종료 시간을 24:00 -> 00:00으로 변경했습니다.(LocalTime의 hour 범위가 0~23인 관계로 부득이하게 변경했습니다.)  
  아래의 데이터를 통해 테스트 부탁드립니다
- Validation 추가로 일부값 필수 지정, 잘못된 전화번호, 잘못된 영업 시작&종료 시간을 검사했습니다.
- 영업 시작 시간과 영업 종료 시간이 같을 경우 에러(ValidationException) 발생시키고 ControllerAdvice에서 처리하게 했습니다.  

__Method__ : post   
__URL__ : /api/store   
__Data__ :   
-- 첫번째 첨포     
{
       "name": "인어수산",
       "owner": "장인어",
       "description": "인천소래포구 종합어시장 갑각류센터 인어수산",
       "level": 2,
       "address": "인천광역시 남동구 논현동 680-1 소래포구 종합어시장 1 층 1 호", "phone": "010-1111-2222",
       "businessTimes": [
       {
       "day": "Monday",
       "open": "13:00",
       "close": "23:00"
       },
       {
       "day": "Tuesday",
       "open": "13:00",
       "close": "23:00"
       },
       {
       "day": "Wednesday",
       "open": "09:00",
       "close": "18:00"
       },
       {
       "day": "Thursday",
       "open": "09:00",
       "close": "23:00"
       },
       {
       "day": "Friday",
       "open": "09:00",
       "close": "23:00"
       }
       ]
   }
     
-- 두번째 점포   
{
    "name": "해적수산",
    "owner": "박해적",
    "description": "노량진 시장 광어, 참돔 등 싱싱한 고퀄 활어 전문 횟집", "level": 1,
    "address": "서울 동작구 노량진동 13-8 노량진수산시장 활어 001",
    "phone": "010-1234-1234",
    "businessTimes": [
    {
    "day": "Monday",
    "open": "09:00",
    "close": "00:00"
    },
    {
    "day": "Tuesday",
    "open": "09:00",
    "close": "00:00"
    },
    {
    "day": "Wednesday",
    "open": "09:00",
    "close": "00:00"
    },
    {
    "day": "Thursday",
    "open": "09:00",
    "close": "00:00"
    },
    {
    "day": "Friday",
    "open": "09:00",
    "close": "00:00"
    }
    ]
}

### 2. 점포 휴무일 등록
- 존재하지 않는 점포에 대한 휴무일 등록 요청 시 직접 구현한 BussinessException인 EntityNotFoundException이 발생하게 했습니다.

__Method__ : post   
__URL__ : /api/store/holiday   
__Data__ :  
{
    "id": 1,
    "holidays": [
    "2021-04-30", "2021-05-01"
    ]
}

## 3. 점포 목록 조회
__Method__ : get   
__URL__ : /api/store   

## 4. 점포 상세 조회
- 존재하지 않는 점포 id를 통한 상세 조회 요청 시 EntityNotFoundException 발생  

__Method__ : get   
__URL__ : /api/store/{id}/detail  
__Example__ : /api/store/1/detail

## 5. 점포 삭제
- 존재하지 않는 점포 id를 통한 삭제 요청 시 EntityNotFoundException 발생
- 특정 id를 통해 점포 삭제 후 해당 id를 통해 '4. 점포 상세 조회' 수행 시 정상 삭제 되었다면 EntityNotFoundException 발생

__Method__ : post   
__URL__ : /api/store/{id}/delete  
__Example__ : /api/store/1/delete
