# User API

중고거래 사이트의 사용자 관련 API 명세서입니다.

---

## 개발 배경

본 프로젝트는 REST API 설계를 목적으로 시작하였으나, 로그인 인증 흐름 및 JWT 동작 방식을 직접 눈으로 확인하고 학습하기 위해 Thymeleaf 기반의 HTML 폼 방식으로 함께 구현하였습니다.

이로 인해 아래와 같은 차이가 발생합니다.

| 항목 | REST API 명세 | 현재 구현 (HTML 폼) |
|---|---|---|
| 인증 방식 | `Authorization: Bearer` 헤더 | `jwtToken` 쿠키 (HttpOnly) |
| 회원가입 | `POST /users` | `POST /{loginType}/join` |
| 로그인 | `POST /users/login` → JSON 응답 | `POST /{loginType}/login` → 쿠키 저장 후 리다이렉트 |
| 내 정보 조회 | `GET /users/me` | `GET /{loginType}/info` |
| 정보 수정 | `PUT /users/me` | `POST /{loginType}/info` |
| 회원 탈퇴 | `DELETE /users/me` | `POST /{loginType}/withdraw` |

> HTML 폼은 GET/POST만 지원하므로 PUT, DELETE 메서드를 사용할 수 없어 POST로 대체하였습니다.

---

## REST API 명세 (설계 목표)

### 1. 회원가입

**POST** `/users`

#### Request Body

```json
{
  "email": "user@test.com",
  "password": "password123",
  "nickname": "은석",
  "phone": "01012345678",
  "username": "user_id_test"
}
```

#### Response (201 Created)

```json
{
  "id": 1
}
```

---

### 2. 로그인

**POST** `/users/login`

#### Request Body

```json
{
  "email": "user@test.com",
  "password": "password123"
}
```

#### Response (200 OK)

```json
{
  "accessToken": "JWT_ACCESS_TOKEN"
}
```

---

### 3. 내 정보 조회

**GET** `/users/me`

#### Header

```
Authorization: Bearer {accessToken}
```

#### Response (200 OK)

```json
{
  "id": 1,
  "email": "user@test.com",
  "nickname": "은석"
}
```

---

### 4. 회원 정보 수정

**PUT** `/users/me`

#### Header

```
Authorization: Bearer {accessToken}
```

#### Request Body

```json
{
  "nickname": "새닉네임"
}
```

#### Response (200 OK)

```json
{
  "id": 1,
  "nickname": "새닉네임"
}
```

---

### 5. 회원 탈퇴

**DELETE** `/users/me`

#### Header

```
Authorization: Bearer {accessToken}
```

#### Response (204 No Content)

---

## 인증 방식 (REST API)

* 로그인 성공 시 JWT Access Token 발급
* 이후 모든 인증이 필요한 요청은 Header에 토큰 포함

```
Authorization: Bearer {accessToken}
```

---

## 현재 구현 상태 (HTML 폼 방식)

### URL 구조

```
/{loginType}/join      → 회원가입
/{loginType}/login     → 로그인
/{loginType}/home      → 홈 화면
/{loginType}/info      → 회원정보 조회 및 수정
/{loginType}/withdraw  → 회원 탈퇴
```

예시: `/users/home`, `/users/login`

### 인증 흐름

1. 로그인 성공 시 JWT 토큰 발급 → `jwtToken` 쿠키에 저장 (HttpOnly)
2. 이후 요청마다 쿠키에서 토큰을 읽어 자동 인증
3. 토큰 유효시간: 1시간

### 구현 완료 기능

- [x] 회원가입
- [x] 로그인 / 로그아웃 (쿠키 삭제)
- [x] 홈 화면 (닉네임 표시)
- [x] 회원정보 조회 (아이디 / 이메일 / 닉네임 / 전화번호)
- [x] 회원정보 수정 (닉네임 / 전화번호 / 비밀번호)
- [x] 회원 탈퇴 (DB 삭제 + 쿠키 만료)


### 💡 Commit 타입

| 타입 | 설명 |
| :--- | :--- |
| **feat** | 새로운 기능 추가 |
| **fix** | 버그 수정 |
| **docs** | 문서 수정 |
| **style** | 코드 스타일 수정 (기능 변경 없음) |
| **refactor** | 코드 구조 개선 |
| **test** | 테스트 코드 추가 |
| **chore** | 기타 작업 (빌드 설정 등) |
