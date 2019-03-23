# VAT Calculator
With the given API of [jsonvat](https://jsonvat.com/)  , I got an API of VAT converting in various tax tips. In this app, I have developed an app which calculates the currency based on the given country list. You can select any country from the dropdown list. I tried to develop this app with a combination of all the Android Architecture Components and latest technologies. 

### App Layout Flow – 
![vat calculator](https://github.com/sunnat629/VATCalculator/blob/master/vatCalc.jpg "vat calculator")




### Here are the technologies and libs -

- **Android KTX**: JetPack component and this Full app is fully developed by Kotlin Language.
- **androidx**: One of the components of JetPack. It replaces the Support Android libs and added more features and flexibilities.
- **Material Deign**: This app is in 100% material design based app with androidx and android.material based UI elements.
- **ViewModel**: I used the MVVM architecture in this app
- **Kotlin Coroutines**: Used to fetch REST API data asynchronously and manage the background thread in easier way.
- **Two ways Databinding**: It bind observable data to UI elements and Get from UI and Set to UI
- **One-way Databinding**: It bind observable data to UI elements and Set to UI
- **Listener Databinding**: I use it for onClick() purpose
- **LiveData**: To hold primitives/collection types and observe the changes
- **Retrofit**: to fetch the REST API into Java/Kotlin interface
- **Okhttp**: HTTP/HTTP2 client for Android
- **Anko**: Kotlin lightweight lib to use toast in an easier way.
- **Glide**: To handle gif file

- **Fonts in XML**: I used it to change the font of the displayed textView (working only API level 26 or higher)
- **Translatable**: I haved used all the string from the resource file so that this app can be translated with any language without any harass.
- **Different Screen Size**: supported 


#### Important tool versions to run and build this project
- Android Studio: stable v3.3
- Kotlin version: 1.3.21
- Gradle version: 3.3.2
- Minimum SDK version: 21
- Compile SDK version: 28
- Target SDK version: 28
