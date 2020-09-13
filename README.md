# NewsApp Android App
## ⚡📱 [Video Preview](https://www.youtube.com/watch?v=Pxl6860Nsec)
<div align="center"><img src="https://i.imgur.com/RWfSVbY.png"/></div>

The app shows latest news using Guardian API, and latest weather information using OpenWeatherMap API - based on user's live location.

Users can bookmark any article that they like and it is saved locally using SharedPreferences.

Used Google Trends API and MP Android Chart to graph keyword trends searched by users.

Implemented Bing Autosuggest API in the search bar to help users look for news related to a specific keyword.

PS. This was homework #9 of CSCI 571 - Spring 2020 - Web Technologies - under [Professor Marco Papa](https://viterbi.usc.edu/directory/faculty/Papa/Marco).

## Setup
1. Download / clone this repo.
2. Import this project in Android studio (Java)
3. Edit MainActivity.java and insert Bing Autosuggest, Openweathermap API keys.

## Technologies Used
- Java
- XML
- Shared Preferences
- JSON
- GSON

## APIs Used
- [Guardian News](https://open-platform.theguardian.com/documentation/)
- [New York Times](https://developer.nytimes.com/get-started)
- [Bing Autosuggest](https://azure.microsoft.com/en-us/services/cognitive-services/autosuggest/)
- [Openweathermap](https://home.openweathermap.org/)
- [Google Trends](https://www.npmjs.com/package/google-trends-api#interestovertime)
