Android firebase sample
=================

An application focused on the implementation of firebase platform components. It is a refactor of
the [restaurant sample app][100] made by Firebase team.


Introduction
------------

This sample demonstrates the use of firebase components, android jetpack components and the
recommended app architecture. It uses as starter point, the [restaurant sample app][100] made by
Firebase team.

Getting Started
---------------

Clone or download the project, then run it.

Screenshots
-----------

![restaurant app video_1](screenshots/video1.gif "login")
![restaurant app video_2](screenshots/video2.gif "main page")
![restaurant app video_3](screenshots/video3.gif "detail page")

Libraries Used
--------------

* [firebase][101] - Firebase platform components.
    * [Authentication][102] - for authentication.
    * [FirebaseUI][103] - .
    * [Firestore][104] - An NoSql database.
* [Foundation][0] - Components for core system capabilities and support for multidex and automated
  testing.
    * [AppCompat][1] - Degrade gracefully on older versions of Android.
    * [Android KTX][2] - Write more concise, idiomatic Kotlin code.
    * [Test][4] - An Android testing framework for unit and runtime UI tests.
* [Coroutines][3] - for asynchronous programming.
* [Flow][5] - To receive live updates from a database.
* [Architecture][10] - A collection of libraries that help you design robust, testable, and
  maintainable apps. Start with classes for managing your UI component lifecycle and handling data
  persistence.
    * [Lifecycles][12] - Create a UI that automatically responds to lifecycle events.
    * [LiveData][13] - Build data objects that notify views when the underlying database changes.
    * [Navigation][14] - Handle everything needed for in-app navigation.
    * [LiveData][13] - Build data objects that notify views when the underlying database changes.
    * [ViewModel][17] - Store UI-related data that isn't destroyed on app rotations. Easily schedule
      asynchronous tasks for optimal execution.
* [UI][30] - Details on why and how to use UI Components in your apps - together or separate
    * [Material design][32] - for an expressive and adaptable design system.
    * [Fragment][34] - A basic unit of composable UI.
    * [Layout][35] - Lay out widgets using different algorithms.
* Third party and miscellaneous libraries
    * [Glide][90] for image loading
    * [Kotlin Coroutines][91] for managing background threads with simplified code and reducing
      needs for callbacks

[0]: https://developer.android.com/jetpack/components

[1]: https://developer.android.com/topic/libraries/support-library/packages#v7-appcompat

[2]: https://developer.android.com/kotlin/ktx

[4]: https://developer.android.com/training/testing/

[3]: https://developer.android.com/kotlin/coroutines

[5]: https://developer.android.com/kotlin/flow

[10]: https://developer.android.com/jetpack/arch/

[12]: https://developer.android.com/topic/libraries/architecture/lifecycle

[13]: https://developer.android.com/topic/libraries/architecture/livedata

[14]: https://developer.android.com/topic/libraries/architecture/navigation/

[17]: https://developer.android.com/topic/libraries/architecture/viewmodel

[30]: https://developer.android.com/guide/topics/ui

[32]: https://material.io/develop/android

[34]: https://developer.android.com/guide/components/fragments

[35]: https://developer.android.com/guide/topics/ui/declaring-layout

[90]: https://bumptech.github.io/glide/

[91]: https://kotlinlang.org/docs/reference/coroutines-overview.html

[100]: https://github.com/firebase/quickstart-android/tree/master/firestore

[101]: https://firebase.google.com/docs

[102]:https://firebase.google.com/docs/auth

[103]:https://firebase.google.com/docs/auth/android/firebaseui

[104]:https://firebase.google.com/docs/firestore

Upcoming features
-----------------
Updates will include incorporating additional firebase components, refactoring of data layer and 
rearrange UI elements.

License
-------

Copyright 2018 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.
See the NOTICE file distributed with this work for additional information regarding copyright
ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. You may obtain a copy of the
License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.