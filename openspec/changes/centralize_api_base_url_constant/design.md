# Technical Design: Centralize API Base URL in `constant.kt`

## 1. Architectural Overview
This change centralizes the API Base URL definition according to Clean Architecture principles, ensuring single-source-of-truth configuration across the network layer.

```
+-------------------------------------------------------------+
|               com.example.constant.kt                      |
|  const val API_BASE_URL = "https://api.weshetab.ir"         |
+------------------------------+------------------------------+
                               |
                               v
+------------------------------+------------------------------+
|                   com.example.network                       |
|  - ApiClient (Retrofit baseUrl)                             |
|  - TokenAuthenticator (Refresh token URL resolution)        |
|  - Other network services/interceptors                      |
+-------------------------------------------------------------+
```

---

## 2. File Specifications

### 2.1 File Creation: `app/src/main/java/com/example/constant.kt`
```kotlin
package com.example

/**
 * Global application constants.
 */
const val API_BASE_URL = "https://api.weshetab.ir"
```

### 2.2 Network Layer Updates: `ApiClient.kt`
- Import `com.example.API_BASE_URL`.
- Set baseUrl via `normalizeBaseUrl(API_BASE_URL)`.

### 2.3 Authenticator & Interceptors: `TokenAuthenticator.kt` & `ResponseInterceptor.kt`
- Ensure base URL resolution delegates to `API_BASE_URL` or `ApiClient.getBaseUrl()`.

---

## 3. Data Flow & Security
- `API_BASE_URL` specifies the HTTPS protocol scheme (`https://api.weshetab.ir`).
- Normalization ensures trailing slashes are appended properly where required by Retrofit (`baseUrl` requires trailing `/`).
- Safe against accidental runtime misconfigurations.
