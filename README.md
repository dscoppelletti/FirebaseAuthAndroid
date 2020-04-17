Firebase Authentication Sample for Android
==========================================

## Authenticate with Firebase using Password-Based Accounts on Android

* http://firebase.google.com/docs/auth/android/password-auth

### Dependencies

```
com.google.firebase:firebase-auth:19.3.0
```

### Firebase Console

```
[Authentication]
[Sign-in method]
Provider: Email/Password

[X] Allow users to sign up using their email address and password. Our SDKs also
provide email address verification, password recovery and email address change primitives

[ ] Email link (passwordless sign-in)
[Save]
```

### FirebaseUser

```
uid: ...
displayName:
email: joe.doe@company.com
isEmailVerified: false
providerId: firebase
```

### GetTokenResult

```
token: ...
signInProvider: password
```

## Authenticate Using Google Sign-In on Android

* http://firebase.google.com/docs/auth/android/google-signin

### Dependencies

```
com.google.firebase:firebase-auth:19.3.0
com.google.android.gms:play-services-auth:17.0.0
```

### Google Cloud Console

```
[API & Services|Credentials]

OAuth 2.0 Client IDs
Web client (auto created by Google Service)
    Client ID: ... (the value to pass to the requestIdToken method of the
                   GoogleSignInOptions.Builder object)
    Client secret: ...
```

### Firebase Console

```
[Authentication]
[Sign-in method]

Provider: Google

[X] Enable
Project public-facing name: Firebase Auth Sample
Project support email: info@company.com
Whitelist client IDs from external projects (optional):
    [...]
Web SDK configuration
    Web client ID: ... (the value from Google Cloud Console)
    Web client secret: ... (the value from Google Cloud Console)
[Save]
```

### FirebaseUser

```
uid: ...
displayName: Joe Doe
email: joe.doe@gmail.com
isEmailVerified: true
providerId: firebase
```

### GetTokenResult

```
token: ...
signInProvider: google.com
```

## Authenticate Using Facebook Login on Android

* http://firebase.google.com/docs/auth/android/facebook-login
* http://developers.facebook.com/docs/facebook-login/android/v2.2
* http://developers.facebook.com/docs/app-events/getting-started-app-events-android

### Dependencies

```   
com.google.firebase:firebase-auth:19.3.0
com.facebook.android:facebook-android-sdk:5.5.1
```

### Facebook for Developers

```
App ID: ...
App Secret: ...
```

### Firebase Console

```
[Authentication]
[Sign-in method]

Provider: Facebook

[X] Enable
App ID: ... (the value from Facebook)
App secret: ... (the value from Facebook)

To complete setup, add this OAuth redirect URI to your Facebook app
configuration: ...
[Save]
```

### Facebook for Developers

```
[Product Settings|Facebook Login]

OAuth redirect URIs: ... (the value from Firebase Console)
```

### FirebaseUser

```
uid: ...
displayName: Jane Doe
email: jane.doe@gmail.com
isEmailVerified: false (likely because she is a test user)
providerId: firebase
```

### GetTokenResult

```
token: ...
signInProvider: facebook.com
```
