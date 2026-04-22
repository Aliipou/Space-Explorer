<div align="center">

# Space Explorer — Android

**A production-grade Android app for exploring NASA open data — APOD imagery, Mars rover photos, and more.**

[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![CI](https://github.com/Aliipou/Space-Explorer/actions/workflows/ci.yml/badge.svg)](https://github.com/Aliipou/Space-Explorer/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-30%2B_cases-brightgreen?style=flat)](app/src/test/)

iOS companion: [SpaceWeather-iOS (SwiftUI)](https://github.com/Aliipou/SpaceWeather-iOS)

</div>

---

## Features

### Astronomy Picture of the Day (APOD)
- **Random mode** — 20 random entries from NASA's full archive
- **Recent mode** — last 30 days in reverse chronological order
- RecyclerView list with Glide image loading and smooth animations
- Detail view: full image, title, date, explanation, copyright
- Share photo URL, open in browser
- Video support — YouTube embeds open in browser with a play button

### Navigation
- Fragment-based navigation with NavComponent + SafeArgs (type-safe)
- Slide-in/out animations between list and detail
- Up navigation, back-stack handled by Navigation component
- Menu actions: Refresh (random), Recent

### UI / Material Design
- Space-themed dark color palette
- Material Design 3 components (CardView, Toolbar, SnackBar, SwipeRefreshLayout)
- Landscape layout for detail view
- Empty state when no data

---

## Architecture

```
app/src/main/java/com/example/spaceexplorer/
│
├── SpaceExplorerApp.kt              # Application class — Koin DI init
│
├── api/
│   ├── NasaApiService.kt            # Retrofit interface — suspend functions
│   └── RetrofitClient.kt            # OkHttpClient + logging + Retrofit builder
│
├── models/
│   └── AstronomyPicture.kt          # Data class, Serializable, computed helpers
│
├── ui/
│   ├── activities/
│   │   └── MainActivity.kt          # NavHost, Toolbar, NavController
│   ├── fragments/
│   │   ├── SpaceImageListFragment.kt   # RecyclerView, SwipeRefresh, MenuProvider
│   │   └── SpaceImageDetailFragment.kt # Detail, share, open in browser
│   ├── adapters/
│   │   └── SpaceImageAdapter.kt     # ListAdapter with DiffUtil
│   └── viewmodels/
│       └── SpaceViewModel.kt        # ViewModel + Coroutines + LiveData
│
└── utils/
    ├── Constants.kt
    └── DateUtils.kt
```

### Design Patterns

| Concern | Solution |
|---|---|
| Architecture | MVVM — ViewModel + LiveData + Repository-ready |
| Concurrency | Kotlin Coroutines (`viewModelScope`, `suspend`) |
| Dependency injection | Koin — `activityViewModel()`, scoped modules |
| Navigation | Navigation Component + SafeArgs (type-safe args) |
| Image loading | Glide — memory + disk LRU cache |
| Networking | Retrofit 2 + OkHttp 4 + Gson |
| API key management | `BuildConfig` field from `keys.properties` (never committed) |
| Reactive UI | `LiveData.observe()` — lifecycle-aware, no memory leaks |

---

## Concurrency: Kotlin Coroutines

Every network call is a `suspend` function in the Retrofit interface:

```kotlin
@GET("planetary/apod")
suspend fun getAstronomyPictures(
    @Query("api_key") apiKey: String,
    @Query("count") count: Int = 20
): List<AstronomyPicture>
```

ViewModels launch in `viewModelScope` — auto-cancelled on ViewModel clear:

```kotlin
fun loadRandomAstronomyPictures(count: Int = 20) {
    _isLoading.value = true
    viewModelScope.launch {
        try {
            val pictures = nasaApiService.getAstronomyPictures(count = count)
            _astronomyPictures.value = pictures
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}
```

Benefits: no callback hell, structured concurrency (no coroutine leaks), automatic lifecycle handling on rotation/back press.

---

## Error Handling

Network failures propagate through `MutableLiveData<String>` to the UI layer. The Fragment observes reactively and shows a non-blocking Snackbar — the user can retry without leaving the screen. Loading state (`_isLoading`) drives the `ProgressBar` and `SwipeRefreshLayout` independently, so UI state is always consistent.

---

## Caching Strategy

| Layer | Technology | Size |
|---|---|---|
| HTTP responses | OkHttp `Cache` | 50 MB disk |
| Decoded images (memory) | Glide LRU memory cache | Auto-scaled to heap |
| Decoded images (disk) | Glide disk cache | 250 MB |

Images are loaded lazily as list cells scroll into view. Glide respects HTTP `Cache-Control` headers from NASA's CDN, so previously-loaded images are served instantly from disk on re-open.

---

## Security

- **API key** in `keys.properties` — excluded by `.gitignore`, never committed
- Key injected as `BuildConfig.NASA_API_KEY` at compile time
- `keys.properties.template` provided for contributors
- All requests over **HTTPS** — `NasaApiService.BASE_URL = "https://api.nasa.gov/"`
- `HttpLoggingInterceptor.Level.BODY` in Debug only — R8/ProGuard strips it from Release builds

---

## CI/CD

GitHub Actions on every push to `main`/`develop` and every PR:

```
.github/workflows/ci.yml
  ├── unit-tests  →  ./gradlew test (JVM, no emulator)
  ├── lint        →  ./gradlew lintDebug
  └── build       →  ./gradlew assembleDebug  (after tests pass)
```

Debug APK uploaded as artifact per build. Lint HTML report uploaded on failure.

---

## Requirements

| Requirement | Version |
|---|---|
| Android Studio | Hedgehog (2023.1.1)+ |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |
| Kotlin | 1.9 |
| Java | 17 |

---

## Quick Start

```bash
git clone https://github.com/Aliipou/Space-Explorer.git
cd Space-Explorer
cp keys.properties.template keys.properties
# Edit keys.properties → set NASA_API_KEY=your_key
./gradlew assembleDebug
```

Get a free NASA API key at [api.nasa.gov](https://api.nasa.gov/). `DEMO_KEY` works with rate limits (30 req/hour).

---

## Running Tests

```bash
# Unit tests — fast, no emulator
./gradlew test

# Instrumentation tests — requires running emulator
./gradlew connectedAndroidTest

# HTML report
open app/build/reports/tests/testDebugUnitTest/index.html
```

---

## Test Coverage

**6 test files · 30+ cases**

### Unit Tests (`app/src/test/`)

| File | Cases | What's tested |
|---|---|---|
| `SpaceViewModelTest` | 5 | Load success/failure, isLoading lifecycle, selectPicture, recent mode |
| `AstronomyPictureModelTest` | 9 | `isImage()`, copyright formatting, explanation truncation, equality |
| `AstronomyPictureTest` | existing | Core model assertions |
| `ApiResponseParsingTest` | existing | Gson JSON parsing, field mapping |
| `DateUtilsTest` | existing | Date formatting, edge cases |
| `RetrofitClientTest` | 5 | Service creation, HTTPS enforced, NASA URL, interface implementation |

### Instrumentation Tests (`app/src/androidTest/`)

| File | What's tested |
|---|---|
| `MainActivityTest` | App launches, toolbar visible |
| `NavigationTest` | List → Detail → Back, up navigation, view presence |

---

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| Kotlin Coroutines | 1.7.3 | Async/await, structured concurrency |
| Retrofit 2 | 2.9.0 | Type-safe HTTP |
| OkHttp 4 | 4.11.0 | HTTP layer, logging, disk cache |
| Glide | 4.16.0 | Image loading + LRU cache |
| Koin | 3.4.3 | Dependency injection |
| Navigation Component | 2.7.6 | Fragment nav + SafeArgs |
| LiveData / ViewModel | 2.6.2 | Lifecycle-aware state |
| Mockito-Kotlin | 5.1.0 | Unit test mocks |
| Coroutines Test | 1.7.3 | `runTest`, `StandardTestDispatcher` |

---

## API

```
GET https://api.nasa.gov/planetary/apod?api_key=KEY&count=20
GET https://api.nasa.gov/planetary/apod?api_key=KEY&start_date=2024-01-01&end_date=2024-01-31
```

---

## License

MIT

---

## Related

- [SpaceWeather-iOS (SwiftUI)](https://github.com/Aliipou/SpaceWeather-iOS) — iOS companion app
- [NASA Open APIs](https://api.nasa.gov/)
