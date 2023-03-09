# simple-post
## 간단한 게시판 구현

# 목차
- 프로젝트 설명
- ERD
- API

---

## 프로젝트 설명

Java와 Spring Boot를 사용하여 CRUD가 가능한 간단한 게시판 API를 구현해보았다.

## 사용 기술
- Java 17
- Spring Boot 3.0.3
- Spring Data JPA
- H2 Database - 테스트 환경 데이터베이스
- Spring Rest Docs - API 문서화
- RestAssured - 테스트

## ERD
### Post
|속성|설명|제약조건|
|  :-:   |  :-:  | :----: |
|   id   |글 번호 |기본 키 |
|username| 작성자 |Not Null|
|password|비밀번호|Not Null|
|  title |  제목 |Not Null|
|content |  내용 |Not Null|

## API
|URI|Method|요청|응답|설명|
|:-:|:-:|:-:|:-:|:-:|
|/posts/post|POST|작성한 글|작성한 글의 id|글 작성 API
