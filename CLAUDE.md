# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Java 21 기반 Spring Boot 4.0.5 백엔드 API (`co.books.api`) 이며 빌드 도구는 Gradle 입니다. Spring Web MVC, Spring Data JPA, PostgreSQL 을 사용합니다. 현재는 초기 스캐폴드 상태로, 애플리케이션 진입점 (`BooksBackendApiApplication`) 과 `contextLoads` 스모크 테스트만 존재합니다.

서버는 Spring 기본값인 8080 이 아닌 **9090** 포트에서 실행되며, 요청/응답 본문과 Tomcat URI 에 대해 UTF-8 인코딩이 강제됩니다. `spring.main.allow-bean-definition-overriding=true` 옵션이 활성화되어 있습니다.

## 자주 사용하는 명령어

Gradle 래퍼를 사용합니다 (Windows 에서는 `gradlew.bat`, 그 외에는 `./gradlew`).

- 애플리케이션 실행: `gradlew.bat bootRun`
- 빌드: `gradlew.bat build`
- 테스트 제외 빌드: `gradlew.bat build -x test`
- 전체 테스트 실행: `gradlew.bat test`
- 단일 테스트 클래스 실행: `gradlew.bat test --tests "co.books.api.BooksBackendApiApplicationTests"`
- 단일 테스트 메서드 실행: `gradlew.bat test --tests "co.books.api.BooksBackendApiApplicationTests.contextLoads"`
- jar 패키징: `gradlew.bat bootJar` (결과물은 `build/libs/` 아래에 생성)

## 작업 시 유의사항

- 모든 주석과 문서 내용은 **한글로 작성**합니다. 소스 코드, 설정 파일 (`application.yml` 등), 마크다운 문서 모두 해당합니다.
- 의존성 구성: `spring-boot-starter-webmvc`, PostgreSQL JDBC (runtime), Lombok (compile-only + annotation processor), Spring Boot DevTools (dev-only), JPA/webmvc 테스트 스타터. JPA 는 현재 **테스트 전용** 의존성이므로, 운영 코드에서 JPA 가 필요하다면 `spring-boot-starter-data-jpa` 를 `implementation` 으로 승격해야 합니다.
- `application.yml` 에 데이터소스 설정이 없습니다. 데이터소스 연결 없이 JPA 엔티티/리포지토리를 추가하면 `contextLoads` 테스트가 실패합니다.
- `application.yml` 파일은 UTF-8 인코딩이며 한글 주석이 포함되어 있습니다. 편집 시 인코딩이 깨지지 않도록 주의하세요.
