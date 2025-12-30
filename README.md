# User API

중고거래 사이트의 사용자 관련 API 명세서입니다.

---

## 1. 회원가입

**POST** `/users`

### Request Body

```json
{
  "email": "user@test.com",
  "password": "password123",
  "nickname": "은석"
}
```

### Response (201 Created)

```json
{
  "id": 1
}
```

---

## 2. 로그인

**POST** `/users/login`

### Request Body

```json
{
  "email": "user@test.com",
  "password": "password123"
}
```

### Response (200 OK)

```json
{
  "accessToken": "JWT_ACCESS_TOKEN"
}
```

---

## 3. 내 정보 조회

**GET** `/users/me`

### Header

```
Authorization: Bearer {accessToken}
```

### Response (200 OK)

```json
{
  "id": 1,
  "email": "user@test.com",
  "nickname": "은석"
}
```

---

## 4. 회원 정보 수정

**PUT** `/users/me`

### Header

```
Authorization: Bearer {accessToken}
```

### Request Body

```json
{
  "nickname": "새닉네임"
}
```

### Response (200 OK)

```json
{
  "id": 1,
  "nickname": "새닉네임"
}
```

---

## 5. 회원 탈퇴

**DELETE** `/users/me`

### Header

```
Authorization: Bearer {accessToken}
```

### Response (204 No Content)

---

## 인증 방식

* 로그인 성공 시 JWT Access Token 발급
* 이후 모든 인증이 필요한 요청은 Header에 토큰 포함

```
Authorization: Bearer {accessToken}
```
