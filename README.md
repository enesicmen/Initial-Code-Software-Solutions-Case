## Your comments / remarks

Enes İÇMEN
enesicmen66@gmail.com
https://www.linkedin.com/in/enesicmen/
https://github.com/enesicmen
https://github.com/enesicmen/Initial-Code-Software-Solutions-Case

## Used technologies
- Language: Kotlin
- Architecture: MVVM, Live Data and Navigation Component
- Dependency Injection: Dagger Hilt
- Networking: Firebase
- Image Loading: Glide

## Code Structure
- DI -> Dependency Injection Layer. It contains modules and qualifiers of Hilt.
- UI: UI Layer. ıt contains operations like showing/creating activites, fragments, views etc.

## Data Flow
- Data is wrapped on the app as Resource to be aware of data and its state like Loading, Success, Error.

- Fragment observes a live data Resource data inside its ViewModel. Makes UI operations like showing loading, error or data when livedata changed.

- ViewModel fetches Resource and updates the live data with collected data with LiveData

## Application flow

- The user registers or logs in to the application. Enters the product details and adds the product to the cart. 
  From here, he goes to the payment screen and enters his card information. If the user's basket is over 1000 TL,
  an error message is displayed (To show and simulate the error condition). If the basket amount is below 1000 TL, the payment is successful.
  After purchasing the product, the basket is cleared. You can see your past orders in the Profile tab.

## Android version

- Android Studio Koala v2024.1.1 Patch 1

## Preview Video

https://github.com/user-attachments/assets/549d4e23-31b5-4bd8-8c65-e2ff51e765be


