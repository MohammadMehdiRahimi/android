# سند طراحی معماری (Design): سیستم یکپارچه احراز هویت، ثبت‌نام و مدیریت نشست‌ها

## ۱. ساختار لایه‌ای و جریان داده (Architectural Layers & Data Flow)

معماری سیستم بر پایه جریان داده یک‌طرفه (Unidirectional Data Flow - UDF) و تفکیک لایه‌های مسئولیت طراحی می‌شود:

```
[ لایه رابط کاربری (UI) ]
         │ ▲
(User    │ │ (Immutable
Actions) │ │  UI State)
         ▼ │
[ لایه مدیریت وضعیت و ارائه (Presentation / ViewModels) ]
         │ ▲
(Command)│ │ (Data Stream / Result)
         ▼ │
[ لایه منطق تجاری (Domain UseCases) ]
         │ ▲
(Request)│ │ (Data Models / Domain Entities)
         ▼ │
[ لایه انتزاع داده و مخزن (Repository Layer) ]
         │ ▲
(Data Req)│ │ (Raw Responses / Cookies)
         ▼ │
[ لایه انتقال شبکه و کلاینت HTTP (HTTP Client & Interceptors) ]
         │ ▲
(HTTP    │ │ (JSON Payloads & Set-Cookie)
Request) ▼ │
[ سرور و سرویس‌های پشتیبان (Backend APIs) ]
```

---

## ۲. استراتژی نشست و مدیریت توکن‌ها (Session & Token Strategy)

### ۱.۲. توکن تمدید (`refresh_token`) - مدیریت امن از طریق کوکی
- سرور توکن رفرش را از طریق هدر استاندارد `Set-Cookie` با ویژگی‌های `HttpOnly` و `Secure` ارسال می‌کند.
- کلاینت HTTP این کوکی را در حافظه ماندگار کوکی‌ها (Cookie Storage) ذخیره کرده و در درخواست‌های بعدی به صورت خودکار به عنوان هدر `Cookie` به سرور می‌فرستد.
- هیچ لایه‌ای از نرم‌افزار (UI، ViewModel، یا Domain) به محتوای متنی این توکن دسترسی مستقیم ندارد یا نیازی به خواندن آن از بدنه پاسخ نخواهد داشت.

### ۲.۲. توکن دسترسی (`access_token`) - مدیریت از طریق هدر احراز هویت
- در پاسخ JSON درخواست‌های احراز هویت موفق (`verify-otp`، `register`، `refresh`)، توکن دسترسی به همراه متادیتای کاربر در قالب شیء پاسخ بازگردانده می‌شود.
- لایه داده این توکن را در حافظه امن کلاینت ذخیره می‌کند.
- لایه شبکه به صورت خودکار هدر `Authorization: Bearer <access_token>` را به تمامی درخواست‌های نیازمند احراز هویت اضافه می‌کند.

### ۳.۲. چرخه تمدید خودکار نشست (Token Refresh Cycle)
1. کلاینت درخواستی محافظت‌شده ارسال می‌کند.
2. در صورت انقضای توکن، سرور با کد وضعیت `401 Unauthorized` پاسخ می‌دهد.
3. لایه رهگیری شبکه (Authenticator/Interceptor) درخواست جاری را متوقف کرده و یک درخواست همگام به `POST /auth/refresh` ارسال می‌کند (کوکی ذخیره‌شده به صورت خودکار به همراه درخواست ارسال می‌شود).
4. با دریافت توکن جدید از سرور، توکن محلی به‌روزرسانی شده و درخواست اولیه با هدر جدید تکرار می‌شود.
5. در صورتی که تمدید با شکست مواجه شود (مثلاً ابطال کوکی در سرور)، رخداد ابطال نشست صادر شده، اطلاعات محلی پاک شده و کاربر به صفحه ورود هدایت می‌شود.

---

## ۳. جریان تعاملات API و مشخصات داده‌ها (API Contracts & Payloads)

### فاز اول: ارسال و تایید کد یکبار مصرف (OTP Flow)

#### ۱. درخواست ارسال کد OTP
* **مسیر:** `POST /auth/send-otp`
* **دسترسی:** عمومی (بدون نیاز به توکن)
* **بدنه درخواست (Request Body):**
```json
{
  "mobile": "09123456789"
}
```
* **پاسخ موفق (Response 200 OK):**
```json
{
  "message": "کد تایید با موفقیت ارسال شد",
  "expiresIn": 120
}
```

#### ۲. تایید کد OTP
* **مسیر:** `POST /auth/verify-otp`
* **دسترسی:** عمومی (بدون نیاز به توکن)
* **بدنه درخواست (Request Body):**
```json
{
  "mobile": "09123456789",
  "code": "12345",
  "deviceType": "ANDROID"
}
```
* **پاسخ کاربر جدید (Response 200 OK - New User):**
```json
{
  "isNew": true,
  "registrationToken": "reg_token_abc123xyz",
  "expiresIn": 600
}
```
* **پاسخ کاربر قدیمی (Response 200 OK - Existing User):**
*(هدر پاسخ حاوی `Set-Cookie: refresh_token=...; HttpOnly; Secure`)*
```json
{
  "isNew": false,
  "accessToken": "eyJhbGciOi...",
  "user": {
    "id": "usr_987",
    "mobile": "09123456789",
    "name": "علی رضایی",
    "grade": "GRADE_11",
    "fieldOfStudy": "MATHEMATICS"
  },
  "onboarding": {
    "required": false
  }
}
```

---

### فاز دوم: فرآیند ثبت‌نام کاربر جدید (Registration Flow)

#### ۱. واکشی اطلاعات پایه (Base Info)
* **مسیر:** `GET /base-info/onboarding`
* **دسترسی:** عمومی (بدون نیاز به توکن احراز هویت)
* **پاسخ (Response 200 OK):**
```json
{
  "grades": [
    { "key": "GRADE_5", "value": "پایه پنجم" },
    { "key": "GRADE_6", "value": "پایه ششم" },
    { "key": "GRADE_7", "value": "پایه هفتم" },
    { "key": "GRADE_8", "value": "پایه هشتم" },
    { "key": "GRADE_9", "value": "پایه نهم" },
    { "key": "GRADE_10", "value": "پایه دهم" },
    { "key": "GRADE_11", "value": "پایه یازدهم" },
    { "key": "GRADE_12", "value": "پایه دوازدهم" },
    { "key": "GRADUATED", "value": "فارغ‌التحصیل" }
  ],
  "fieldsOfStudy": [
    { "key": "MATHEMATICS", "value": "ریاضی و فیزیک" },
    { "key": "EXPERIMENTAL_SCIENCES", "value": "علوم تجربی" },
    { "key": "HUMANITIES", "value": "علوم انسانی" },
    { "key": "VOCATIONAL", "value": "فنی و حرفه‌ای" }
  ]
}
```

#### ۲. منطق وابستگی فرم ثبت‌نام
* **نگاشت داده‌ها:** مقادیر `value` در رابط کاربری به کاربر نمایش داده می‌شوند، اما در زمان ثبت، شناسه استاندارد `key` ارسال می‌شود.
* **قانون پایه و رشته:**
  * اگر کلید پایه انتخابی در بازه پایه‌های ابتدایی و متوسطه اول (`GRADE_5` تا `GRADE_9`) باشد، بخش انتخاب رشته در فرم غیرفعال/پنهان شده و مقدار `fieldOfStudy` ارسال نمی‌شود (یا برابر `null` خواهد بود).
  * اگر کلید پایه در مقاطع متوسطه دوم (`GRADE_10`، `GRADE_11`، `GRADE_12`، `GRADUATED`) باشد، انتخاب رشته اجباری است.

#### ۳. ارسال اطلاعات ثبت‌نام
* **مسیر:** `POST /auth/register`
* **دسترسی:** عمومی با توکن ثبت‌نام
* **بدنه درخواست (Request Body):**
```json
{
  "registrationToken": "reg_token_abc123xyz",
  "mobile": "09123456789",
  "name": "سارا محمدی",
  "grade": "GRADE_10",
  "fieldOfStudy": "EXPERIMENTAL_SCIENCES",
  "deviceType": "ANDROID"
}
```
* **پاسخ ثبت‌نام موفق (Response 200 OK):**
*(هدر پاسخ حاوی `Set-Cookie: refresh_token=...; HttpOnly; Secure`)*
```json
{
  "accessToken": "eyJhbGciOi...",
  "user": {
    "id": "usr_554",
    "mobile": "09123456789",
    "name": "سارا محمدی",
    "grade": "GRADE_10",
    "fieldOfStudy": "EXPERIMENTAL_SCIENCES"
  }
}
```

---

### فاز سوم: تمدید نشست و خروج (Refresh & Logout)

#### ۱. تمدید نشست (Refresh Session)
* **مسیر:** `POST /auth/refresh`
* **دسترسی:** از طریق کوکی `refresh_token`
* **بدنه درخواست:** خالی `{}`
* **پاسخ (Response 200 OK):**
```json
{
  "accessToken": "eyJhbGciOi..."
}
```

#### ۲. خروج کاربر (Logout)
* **مسیر:** `POST /auth/logout`
* **دسترسی:** ارسال کوکی `refresh_token` و هدر `Authorization`
* **پاسخ (Response 200 OK):**
```json
{
  "success": true,
  "message": "نشست کاربر با موفقیت خاتمه یافت"
}
```

---

## ۴. ماشین وضعیت و منطق مسیریابی (Routing State Machine)

```
[ وضعیت اولیه / بارگذاری اپ ]
          │
          ├───────────────────────────────────────────┐
          │ (دارای توکن معتبر)                         │ (فاقد توکن یا نشست منقضی)
          ▼                                           ▼
      [ صفحه اصلی: Home ]                         [ صفحه ورود: Login ]
                                                      │
                                                      │ (ارسال شماره و دریافت کد)
                                                      ▼
                                              [ صفحه تایید: Verify OTP ]
                                                      │
                                                      │ (بررسی پاسخ verify-otp)
                  ┌───────────────────────────────────┴───────────────────────────────────┐
                  ▼ (isNew = true)                                                            ▼ (isNew = false)
      [ ذخیره registrationToken ]                                                 [ ذخیره access_token و Cookie ]
                  │                                                                           │
                  ▼                                                                           ├──────────────────────────┐
      [ واکشی اطلاعات پایه:                                                                    ▼ (onboarding.required)    ▼ (!onboarding.required)
        GET /base-info/onboarding ]                                                 [ صفحه تکمیل پروفایل ]      [ پاکسازی Backstack و
                  │                                                                           │                   ورود به Home ]
                  ▼                                                                           │ (تکمیل پروفایل)
      [ صفحه ثبت‌نام: Register ]                                                                ▼
                  │                                                                 [ ورود به Home ]
                  │ (ارسال موفق فرم POST /auth/register)
                  ▼
      [ دریافت access_token و Cookie ]
                  │
                  ▼
      [ پاکسازی کامل Backstack و ورود به Home ]
```

---

## ۵. راهبرد مدیریت خطاها و وضعیت‌های مرزی (Error Handling & Edge Cases)

1. **انقضای کد OTP:** نمایش پیام خطا و شمارشگر معکوس برای درخواست مجدد ارسال کد.
2. **انقضای توکن ثبت‌نام (`registrationToken` > 600s):** در صورت دریافت خطای منقضی شدن توکن در مرحله ثبت‌نام، نمایش پیام مناسب و هدایت کاربر به صفحه اول لاگین برای احراز هویت مجدد.
3. **عدم دسترسی به شبکه در دریافت اطلاعات پایه:** امکان تلاش مجدد (Retry) در صفحه ثبت‌نام بدون از دست رفتن `registrationToken`.
4. **شکست در تمدید توکن (Refresh Failure):** پاک‌سازی فوری تمام داده‌های هویتی محلی و ارسال رویداد ناوبری اجباری به صفحه ورود.
