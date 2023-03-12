# simple-post
## 간단한 게시판 구현

# 목차
- 프로젝트 설명
- ERD
- 구현 기능
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

## 구현 기능
- 게시글 페이지로 조회
- 게시글 단건 조회
- 게시글 작성
- 게시글 수정
- 게시글 삭제
- 글 수정, 삭제를 위한 비밀번호 확인