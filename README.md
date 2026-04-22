<div align="center">

# Space Explorer — Android

**A production-grade Android app for exploring NASA open data — APOD imagery, Mars rover photos, and more.**

[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![CI](https://github.com/Aliipou/Space-Explorer/actions/workflows/ci.yml/badge.svg)](https://github.com/Aliipou/Space-Explorer/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-45%2B_cases-brightgreen?style=flat)](app/src/test/)

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

## Architecture — Clean Architecture (3 layers)

```
app/src/main/java/com/example/spaceexplorer/
│
├── SpaceExplorerApp.kt              # Application — Koin 3-module DI setup
│
├── data/                            # DATA LAYER
│   ├── local/
│   │   ├── SpaceDatabase.kt         # Room singleton, schema v1
│   │   ├── AstronomyPictureEntity.kt # @Entity with TTL + toDomain()/fromDomain()
│   │   └── AstronomyPictureDao.kt   # @Dao: getAll, getByDate, insert, deleteExpired, hasFreshCache
│   ├── remote/
│   │   └── RemoteDataSource.kt      # Wraps Retrofit API, single responsibility
│   └── repository/
│       ├── SpaceRepository.kt       # Interface — contract for domain layer
│       └── SpaceRepositoryImpl.kt   # Offline-first: cache → remote → update cache
│                                    # Exponential backoff retry (3 attempts, jitter)
│
├── domain/                          # DOMAIN LAYER
│   ├── model/
│   │   └── SpaceResult.kt           # sealed class: Loading / Success(fromCache) / Error
│   └── usecase/
│       ├── GetAPODUseCase.kt         # invoke() → Flow<SpaceResult>
│       └── GetRecentAPODUseCase.kt  # invoke() → Flow<SpaceResult>, domain sort
│
├── api/
│   ├── NasaApiService.kt            # Retrofit — suspend functions
│   └── RetrofitClient.kt            # OkHttpClient + logging interceptor
│
├── models/
│   └── AstronomyPicture.kt          # Domain model — Serializable
│
└── ui/                              # PRESENTATION LAYER
    ├── activities/MainActivity.kt
    ├── fragments/
    │   ├── SpaceImageListFragment.kt  # Collects StateFlow with lifecycleScope
    │   └── SpaceImageDetailFragment.kt
    ├── adapters/SpaceImageAdapter.kt  # ListAdapter + DiffUtil
    ├── viewmodels/
    │   └── SpaceViewModel.kt          # StateFlow<SpaceUiState>, no LiveData
    └── utils/
```

### Design Patterns

| Concern | Solution |
|---|---|
| Architecture | Clean Architecture (Data / Domain / Presentation) |
| Reactive state | `StateFlow<SpaceUiState>` — replaces LiveData, coroutine-native |
| Offline-first | Repository: serve cache → fetch remote → update cache |
| Concurrency | Kotlin Coroutines + Flow (`viewModelScope`, `suspend`) |
| Retry strategy | Exponential backoff with jitter in `SpaceRepositoryImpl` |
| Dependency injection | Koin 3 — 3 modules: data, domain, presentation |
| Local persistence | Room 2.6 — `AstronomyPictureEntity` with 24h TTL |
| Image loading | Glide 4 (list) + Coil 2 (Compose detail) — LRU cache |
| Navigation | Navigation Component + SafeArgs |
| Infinite scroll | Paging 3 — `ApodPagingSource` walks dates backward |
| UI (detail) | Jetpack Compose — `ApodDetailScreen` with animations |
| Metrics | `PerformanceTracker` — cache hit rate + load times |
| API key | `BuildConfig` field from `keys.properties` (gitignored) |

---

## Jetpack Compose Detail Screen

`ui/compose/ApodDetailScreen.kt` renders the APOD detail entirely in Compose:

- **Hero image** via `AsyncImage` (Coil) with `crossfade` + gradient overlay
- **Entry animation** — `AnimatedVisibility` with `fadeIn() + slideInVertically()`
- **Accessibility** — `semantics { contentDescription }` on all interactive elements
- **Copyright badge** — semi-transparent `Surface` anchored to image bottom-right
- **Media type chip** — green badge when `mediaType == "image"`

The Fragment host inflates a `ComposeView` and calls `setContent { ApodDetailScreen(...) }`.

---

## Paging 3 — Infinite Scroll

`data/paging/ApodPagingSource` walks the NASA archive **backward by date**:

```kotlin
class ApodPagingSource(private val remote: RemoteDataSource) : PagingSource<String, AstronomyPicture>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, AstronomyPicture> {
        val endDate = params.key ?: today
        val pictures = remote.getAPODByDateRange(startDate, endDate).sortedByDescending { it.date }
        return LoadResult.Page(data = pictures, prevKey = startDate.minusDays(1), nextKey = null)
    }
}
```

Each page loads `pageSize` days. `prevKey` drives the next load; `nextKey = null` because the list is newest-first.

---

## Performance Metrics

`util/PerformanceTracker` is a lightweight singleton that tracks every repository call:

```kotlin
PerformanceTracker.startTrace("apod_random")
// ... fetch ...
PerformanceTracker.endTrace("apod_random", fromCache = true)
// → logcat: [apod_random] 12ms | source=CACHE | cache_hit_rate=87%
```

Call `PerformanceTracker.summary()` to get a formatted report. Replace the `Log` calls with Firebase Performance / Datadog in production.

---

## Offline-First Strategy

The Repository implements a **stale-while-revalidate** pattern:

```
1. Emit SpaceResult.Loading
2. Emit cached data from Room immediately (fromCache = true)   ← user sees data instantly
3. Fetch fresh data from network with exponential backoff retry
4. On success: update Room, emit fresh data (fromCache = false)
5. On failure + cache exists: silently swallow error (user already has data)
6. On failure + no cache: emit SpaceResult.Error
```

This means the app works **100% offline** if data was fetched at least once. Room stores entries with a 24-hour TTL. Expired entries are pruned automatically.

---

## Concurrency: Kotlin Coroutines + StateFlow

The ViewModel exposes a single `StateFlow<SpaceUiState>` — no LiveData anywhere:

```kotlin
data class SpaceUiState(
    val pictures: List<AstronomyPicture> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFromCache: Boolean = false,
    val selectedPicture: AstronomyPicture? = null
)
```

The Fragment collects with `lifecycleScope` + `repeatOnLifecycle(STARTED)` — no memory leaks, lifecycle-safe.

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

Errors propagate via `SpaceResult.Error` through the repository → use case → ViewModel → `StateFlow<SpaceUiState>`. The Fragment collects with `repeatOnLifecycle(STARTED)` and shows a non-blocking Snackbar. Loading and error state are part of the same `SpaceUiState` — always consistent, no split LiveData streams.

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
| `SpaceViewModelStateFlowTest` | 7 | StateFlow emissions, Success/Error/Loading, selectPicture, clearError, cache flag |
| `SpaceResultTest` | 9 | sealed class states, `fromCache`, extension callbacks (`onSuccess`/`onError`), chaining |
| `SpaceRepositoryTest` | 6 | Offline-first flow, cache-first emit, error swallowing with cache, Room insert, clearCache |
| `SpaceViewModelTest` | 5 | StateFlow: load success/failure, isLoading, selectPicture, recent mode |
| `AstronomyPictureModelTest` | 9 | `isImage()`, copyright, truncation, equality, copy |
| `AstronomyPictureTest` | existing | Core model assertions |
| `ApiResponseParsingTest` | existing | Gson JSON parsing |
| `DateUtilsTest` | existing | Date formatting |
| `RetrofitClientTest` | 5 | HTTPS enforced, NASA URL, service creation |

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
| Compose BOM | 2024.02 | Declarative UI (detail screen) |
| Paging 3 | 3.2.1 | Infinite scroll by date range |
| Coil Compose | 2.5.0 | Image loading in Compose screens |

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
