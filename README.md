# MTMI(MJU TMI)
Made in MJU Team project class <br />
* Eamil address: dnr2144@gmail.com
* Demo video: https://youtu.be/kkeV75rNsuc

## Main features introduction
* It provides your MBTI charateristics and recomemend a place where suits you.
* It provides MJU sites that many MJU students use.
* It provides MJU building locations.
* It provides fatest MJU shuttle time and shuttle bus stopover.
* It provides a bulletin board for the subjects you enrolled in this semester. So, students only can access to bulletin boards for subjects enrolled this semester. In each bulletin board you can post your own post and in each post other users can comment their opinion.
* It provides chatting service with other users. 
* It provides the basic mail form when you send an email to the professor.
* Crawling from your LMS account, it provides information on the subjects you have enrolled in this semester (timetables, class hours, professors, attendance rates, assignment submissions). 

## Android convention rule(resource naming)
* _Image files: imageType\_(iconOrImage)\_activityNameORfragmentName\_fileName_
    * e.g. _The icon file used in activity\_get\_started.xml: \_ic\_get\_started\_fileName.xml_
    * e.g. _The image file used in activity\_copyright.xml:  \_img\_copyright\_fileName.png_

## Project file strucuture
* Pacakage
   * account <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|
      |1|EditProfileActivity|Edit user profile|
      |2|LoginActivity|Login|
      |3|MyProfileActivity|Show my profile|
      |4|SignUpActivity|Sign up|
      
   * chatting <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|
      |1|ChattingRoomDetailsActivity|Show the chat window and send or receive a message with the other person|
      |2|ChattingRoomListActivity|Show the chat room list|
      
   * crawling <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|
      |1|CrawlingLmsInfo|Crawling lms data(user profile, homework, professor info, subject info ... etc) from lms|
      |2|LmsAututhenticationDialog|A dialog window that sign in lms site|
      
   * database <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|
      |1|FirebaseManger|Provides CRUD methods to communicate with the Firebase| <br/>
      * entity <br/>
         |-|**Class name**|**Content**|
         |:---:|:---:|:---|
         |1|BoardComment(data class)|The item class of a Post comments|
         |2|BoardPost(data class)|The item class of Post|
         |3|ChattingMessage(data class)|The item class of Chatting message|
         |4|ChattingRoom(data class)|The item class of chatting room|
         |5|ChattingRoomListForm(data class)|The item of each chatting room list when you see chatting room list|
         |6|LastChattingMessage(data class)|Last Chatting message|
         |7|UserData(data class)|The item of User info|
         
   * mbti <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|
      |1|MbtiResultActivity|Show your MBTI type, your charatericts and recommend A place that fits you well (e.g. cafe, restaurant ... etc)|
      |2|MbtiTestQuestionActivity|Provides 20 MBTI test question. If you answer all 20 MBTI questions, Algorithm guess your MBTI propensity.|
      |3|MbtiTestStartFragment|Introduce MBTI Test page and if you have already done the MBTI test, it will guide you to the MBTI result page.|
      
   * myclass <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|  
      |1|MyClassMailToProfessorActivity|Provides the basic mail form when you send an email to the professor. (e.g. attandance, test ... etc)|
      |2|MyClassMainFramgment|MyClass main page|
      |3|MyClassSubjectBulletinBoardDetailsActivity|Show a post and comments specifically|      
      |4|MyClassSubjectBulletinBoardListActivity|Show the post list of a specific bulletin board|
      |5|MyClassSubjectBulletinBoardWritingActivity|The page that can write the post|
      |6|MyClassSubjectListActivity|Show the bulletin board list that can move to each bulletin board|
      |7|MyClassTimeTableActivity|Show the information on the subjects, assignments, professors, and timetables that you have registered semester.|
      
   * util <br/>
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|  
      |1|AES128|Provides methods to encrypt or decrypt a string.|
      |2|APP|Provides extern context that can use in any activity or fragment|
      |3|SharedPrefManger|Manage SharePreferences|      
      |4|ValidationRegex|Provides regex|
      
   * others
      |-|**Class name**|**Content**|
      |:---:|:---:|:---|  
      |1|CampusPhoneNumberActivity|Show school-related organization phone number|
      |2|CopyrightActivity|Show people who make this service|
      |3|GetStartedActivity|Splash page when starts this apllication|      
      |4|HomeActivity|Main page (container)|
      |5|HomeFragment|Main page on HomeActivity|
      |6|MapDetailsActivity|Provides school building location and useful Facilities(cafe, restraunt ... etc) (using Kakao map API)|

## Development environment
* Kotlin
* Firebase Realtime Database
* Firebase FireStore Database
* Android Studio @4.2.1

## Application version
* minSdkVersion: 26
* targetSdkVersion: 30
* GradleVersion: @6.7.1
* JavaVersion: 11
* KotlinVersion: @1.5.10

## APIs
* Open MAP API of Kakako (https://apis.map.kakao.com/android/)

## Required permissions
* ***android.permission.INTERNET***
* ***android.permission.CALL_PHONE***
* ***android.permission.ACCESS_FINE_LOCATION***
* ***android.permission.ACCESS_COARSE_LOCATION***
* ***android.permission.READ_EXTERNAL_STORAGE***

## Open source 
- circle imageview: https://github.com/hdodenhof/CircleImageView
- custom EditText: https://github.com/florent37/MaterialTextField
- custom spinner: https://github.com/Chivorns/SmartMaterialSpinner
- custom loading button: https://github.com/roynx98/transition-button-android
- DxLoadingButton: https://github.com/StevenDXC/DxLoadingButton
- timetable
  1. https://github.com/tlaabs/TimetableView
  2. https://androidexample365.com/customizable-timetableview-for-android
- custom toggle button: https://github.com/Bryanx/themed-toggle-button-group
  - reference
    1. https://woovictory.github.io/2020/06/13/Android-FlexBoxLayout/
- custom button: https://github.com/JMaroz/RoundButton
- lottie animation: https://github.com/airbnb/lottie-android
- glide: https://github.com/bumptech/glide
- coroutine: https://github.com/Kotlin/kotlinx.coroutines
- okhttp: https://github.com/square/okhttp
- Motion Toast: https://github.com/Spikeysanju/MotionToast
- google gson: https://github.com/google/gson
- Groupie: https://github.com/lisawray/groupie
- ViewPagerIndicator: https://github.com/tommybuonomo/dotsindicator
- Custom Progressbar: https://github.com/skydoves/ProgressView
- Custom Dialog: https://github.com/PatilShreyas/MaterialDialog-Android

## Application layout
### ① _Login & ② SignUp & ③ UserProfile & ④ EditUserProflie & ⑤ Copyright_
<pre> 
<img src="https://user-images.githubusercontent.com/43941383/122704597-ff93d700-d28e-11eb-840a-e0e8ac709636.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122704601-015d9a80-d28f-11eb-985f-bfeec7371ce9.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122704646-10444d00-d28f-11eb-898b-8b7cc92804aa.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122704651-11757a00-d28f-11eb-9976-a965c4302634.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122704655-12a6a700-d28f-11eb-8c86-6866796ebf3b.jpg" width=200 height=400/>   
</pre>

### _① Main Home & ② Choosing MJU Building & ③ Specific Building Location_
<pre>
<img src="https://user-images.githubusercontent.com/43941383/122704608-03bff480-d28f-11eb-9891-c0cc8804183f.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122864462-a6e03f00-d35f-11eb-8933-4a3342d837ff.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122864577-dabb6480-d35f-11eb-8ac6-0af37a3d22fb.jpg" width=200 height=400/>    
</pre>

### _① MBTI Main & ② MBTI Test & ③ MBTI Result & ④ Facility Loacation Recommended_
<pre>
<img src="https://user-images.githubusercontent.com/43941383/122704624-0a4e6c00-d28f-11eb-9455-3a942a39484d.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122863946-b14e0900-d35e-11eb-85b3-de6fa1c8ca77.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122863981-c165e880-d35e-11eb-8679-a1a8848512c8.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122864663-00486e00-d360-11eb-93ba-d92f36c4681b.jpg" width=200 height=400/>
</pre>

### _① MyClass Main & ② Lms Login ③ Bulletin Board List & ④ Post List & ⑤ Show Specific Post and Comments & ⑥ Mail Guidlines to Professor & ⑦ Timetable and Subject Information(assignment, attandance rate, professor)_
<pre>
<img src="https://user-images.githubusercontent.com/43941383/122704629-0c182f80-d28f-11eb-8126-f422e4ee7dc4.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122704644-0f132000-d28f-11eb-9039-f079abee276c.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122865145-d0e63100-d360-11eb-813e-3237c3cbf417.png" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122865304-215d8e80-d361-11eb-87cd-cbc1bd9e5f4c.png" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122704636-0d495c80-d28f-11eb-91a4-da99025082af.jpg" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122706105-20a9f700-d292-11eb-9a65-adf3a04d4401.png" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122867408-6505c780-d364-11eb-82a2-0aab651c0c23.png" width=200 height=400/>
</pre>

### _① Chatting Room List & ② Specific Chatting Room_
<pre>
<img src="https://user-images.githubusercontent.com/43941383/122849113-da15d480-d345-11eb-94cc-56f9c661cb25.png" width=200 height=400/>   <img src="https://user-images.githubusercontent.com/43941383/122849146-eb5ee100-d345-11eb-9300-bd7fb803a82b.png" width=200 height=400/>
</pre>

