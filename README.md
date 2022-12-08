# MySecurity-App



This project is an android based security app developed while studying Cybersecurity by Pawan khadka. This application is known as secure application. Using its Signup, Sigin and forgot password technology one can make their application secure. Here I have used Java as programing language and google firebase as the database of the application.

This is Android application.


## Screenshots

### Splash Activity
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204767517-6dda874e-786b-473a-826f-fef75bc1322e.jpg">

Opening the application user will be greeted with this screen. This screen contains logo of the application which remains for 2-5 sec depending on user’s internet. As I have used firebase database to store all the information of the application. So internet is necessary to run application.

---

### SignIn Activity
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204767713-e8ecdb80-a600-47dd-93f3-82510395b134.jpg">

After few seconds on splash screen user is directed sign in page. Here if user has created their account then they have to provide required information to sign in to application, if not than have to click on “New User | Sign Up!” to create a new account. User have an account but forgot password they can reset it by clicking on “Forgot Password?”
Data of user are saved on firebase and login system is also handled by authentication system of firebase.

---

### Forgot Password Activity
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204767859-dd0be69a-645a-4ddd-96c3-a386a72b8ac3.jpg">

When User clicked on “Forgot Password?” in sign in page they are redirected to this page. If user needs to reset their password they have just to provide registered email and click on reset password button. If email is registered in application than user get an email in same email address sand can reset password from there. If email is incorrect then an error message is shown to user. By clicking on “Go Back” user is redirected to sign in page.

---

### Sign Up Activity
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204767975-fe2844e9-2a7e-432e-851c-705b39f27f8c.jpg">

When User clicked on “New User | Sign Up!” in sign in page they are redirected to this page. Here user needs to provide required information and a unique email to create an account.
Providing data and clicking on Sign Up button system checks information and if everything is valid then user is redirected to login page where usher should provide required information to sign in.

---

### Home Activity
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204768154-6d7d1b84-ac28-4f99-b99b-b6f78d85e1a7.jpg">

After giving correct credentials user is redirect to this page with a toast message. 

---

### Sign Out
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204768264-2c6e1a2a-f3fa-45c4-8e4e-7812fa4ea456.jpg">

By clicking Sign Out from navigation drawer menu user is promoted to an alert dialog saying are you sure if user select ok then user get signed out from the application.

---

### After Sign Out Sucessful
<img width="325" height="700" src="https://user-images.githubusercontent.com/78890102/204768382-663eef2d-8c72-4bf0-a362-08ea9983a83e.jpg">

After user sign out is successful they are directed to login page with a toast message. If user wants to exit application they can click the cross icon at Top right of the application which will allow user to exit application.

---










