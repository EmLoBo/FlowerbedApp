# Flowerbed
![tests](https://github.com/EmLoBo/FlowerbedApp/actions/workflows/flowerbed-tests.yml/badge.svg)

An Android app for planning your garden — search plants, build flowerbed projects, and watch the weather that affects them.

## Features

- **Plant search & database** — browse and look up species via the [Trefle](https://trefle.io) API.
- **My Projects** — group plants into named flowerbeds you're planning.
- **Weather** — current conditions from [Open-Meteo](https://open-meteo.com), plus official
  [IMGW](https://danepubliczne.imgw.pl) meteo warnings matched to your voivodeship.
- **Custom background** — pick any image from your gallery to personalise the home screen.

## Stack

- Kotlin · Jetpack Compose · Material 3
- Hilt (DI) · Navigation Compose
- Coroutines · Flow
- Retrofit + Moshi · OkHttp
- Room · DataStore · Coil
- Google Play Services Location

## Architecture

Clean layering with one module:

```
core/domain   — models, repository interfaces, use cases
core/data     — repository implementations, Room, Retrofit, DataStore
feature/*     — one screen per package: ViewModel + Composable
ui/           — navigation, theme, shared components
```

## Setup

1. Clone the repo.
2. Add a Trefle API token to `local.properties`:
   ```
   TREFLE_TOKEN=your_token_here
   ```
3. Open in Android Studio (Hedgehog or newer), let Gradle sync, run on an emulator or device with Android 8.0+ (`minSdk = 26`).

## License

TBD.
