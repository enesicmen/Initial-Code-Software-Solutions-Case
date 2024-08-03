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

- The user registers or logs in to the application. He enters the product details and adds the product to the cart. From there, 
  he goes to the payment screen and enters his card information. If the user's basket is below 1000 TL, 
  an error message is displayed (to show and simulate the Error situation). If the basket amount is below 1000 TL, the payment is successful. 
  After purchasing the product, the basket is cleared. You can view your past orders in the Profile tab.


