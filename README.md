![Image](https://images.xiaozhuanlan.com/photo/2021/b106fd65d34a4a724244e7c5b42a2372.jpg)

[《Learning Android Again》](https://xiaozhuanlan.com/kunminx) paid readers can add WeChat to join the group: myatejx

> [Free preview](https://juejin.cn/post/7106042518457810952), **[Column directory](https://www.yuque.com/kunminx/fpmbc5/ghlwb5)**, **[Update details](https://www.yuque.com/kunminx/fpmbc5/in59vu)**, [Discount policy](https://www.yuque.com/kunminx/fpmbc5/of601a)

---

# Copyright Declaration

We published an exclusive interview on Juejin about this project being "sold as a course" [《What’s it like to have your open-source project turned into a course and sold for over 10 million?》](https://juejin.im/post/5ecb4950518825431a669897)

This project is designed to help developers **understand Google’s open-source Jetpack MVVM components and their purpose and responsibility boundaries** through high-frequency application scenarios.

At the same time, this project serves as the "supporting project" for the [《Learning Android Again》](https://xiaozhuanlan.com/topic/6017825943) column's Jetpack MVVM series articles. **The content of the articles and the project code design reflect my unique understanding of Jetpack MVVM, and I hold copyright for this.**

No organization or individual may use this project's code design and my exclusive understanding of Jetpack MVVM for "**packaging and selling, lead generation, publishing, and selling courses**" or other commercial purposes without prior communication with the author.

---

# Architecture Overview

![Architecture Diagram](https://images.xiaozhuanlan.com/photo/2023/b10d6c52e0cdb4197725059399fad12f.jpg)

---

# Preface

Last week, I published an article [《In-depth Jetpack MVVM》](https://juejin.im/post/5dafc49b6fb9a04e17209922) on various "tech communities." Initially, I thought it would go unnoticed, especially in 2019 when Android was being criticized. But to my surprise, the article gained attention from architects and technical managers at "well-known domestic companies" and Android developers from "world-class companies."

Feedback from readers shows that most Android developers have recently stepped out of their comfort zones and started exploring and applying Jetpack MVVM in real-world projects.

Unfortunately, there is an abundance of **disjointed, repetitive, code-heavy articles** about Jetpack MVVM online. These articles neither provide a "complete perspective" to help readers clarify the context nor help new Jetpack learners—often creating confusion and discouraging them.

The good news is, in this issue, we bring you the **best practices for Jetpack MVVM**, carefully crafted!

---

|                          Addictive Interaction Design                          |                            Coherent User Experience                            |                      Reliable Source Unified Distribution                      |
| :----------------------------------------------------------------------------: | :----------------------------------------------------------------------------: | :----------------------------------------------------------------------------: |
| ![](https://upload-images.jianshu.io/upload_images/57036-0a5cdc68f003211a.gif) | ![](https://upload-images.jianshu.io/upload_images/57036-2b21db531e51ff03.gif) | ![](https://upload-images.jianshu.io/upload_images/57036-9a541148ce5bed2e.gif) |

| Seamless Switching Between Landscape and Portrait Layouts |
| :-------------------------------------------------------: |
|  ![](https://i.loli.net/2021/08/25/X9rado7AfnCEgv3.gif)   |

---

# Project Introduction

I have 3 years of experience in "mobile business architecture" practice and design, leading or participating in the "reconstruction" of many medium to large-sized projects. I have a deep understanding of Jetpack MVVM's efforts in **establishing standardized development patterns to reduce unpredictable errors**.

In this case study, I will show you how Jetpack MVVM **simplifies** and transforms what would otherwise be a very error-prone and time-consuming development task into a simple process with just a few lines of code.

> 👆👆👆 Key Point!

In this project:

> We arrange two sets of **completely different layouts** for **landscape and portrait** modes, and with the help of knowledge like [lifecycle](https://xiaozhuanlan.com/topic/0213584967), [rebuilding mechanism](https://xiaozhuanlan.com/topic/7692814530), [state management](https://xiaozhuanlan.com/topic/7692814530), [DataBinding](https://xiaozhuanlan.com/topic/9816742350), [ViewModel](https://xiaozhuanlan.com/topic/6257931840), [LiveData](https://xiaozhuanlan.com/topic/0168753249), [Navigation](https://xiaozhuanlan.com/topic/5860149732), we easily achieve **seamless switching between portrait and landscape layouts without unexpected errors** with just a few lines of code.

> We have multiple Fragment pages that display **playback status indicators** (including play/pause button states, current index indicators for the playlist, etc.) and will show you "how" and "why" to use [LiveData](https://xiaozhuanlan.com/topic/0168753249) **together with** the reliable source [ViewModel](https://xiaozhuanlan.com/topic/6257931840) or singleton to achieve **unified event distribution across the entire app**.

> We arranged cross-page communication between Fragment and Activity to show you how to implement **lifecycle-safe and message-syncing** communication between pages using **Demeter's Law** (also known as the Law of Least Knowledge) and UnPeekLiveData with an app-level SharedViewModel.

> We provide content such as view controllers, [ViewModel](https://xiaozhuanlan.com/topic/6257931840), Dispatcher, and DataRepository in directories like `ui.page`, `domain.request`, `data.repository`, etc. This demonstrates how the **one-way dependency** architecture design helps avoid issues like "memory leaks" through layered data requests and responses.

> The project code is written in ISO-certified industrial-grade Java. We also provide rich comments in the classes above to help you understand why the "skeleton code" is designed this way and how this design **avoids unexpected errors** in the context of software engineering.

---

Besides **mastering the best practices of MVVM in "simplifying complexity" code**, you will also gain the following from this project:

1. Clean code style and standard resource naming conventions.
2. In-depth understanding and proper use of the "view controller" concept.
3. Full use of AndroidX and Material Design 2.
4. Best practices for ConstraintLayout.
5. **Excellent user experience and interaction design**.
6. No use of Dagger, no complex tricks, and no writing overly difficult code.
7. The one more thing is:

You can now download and experience it from the "App Store"!

[![google-play1.png](https://upload-images.jianshu.io/upload_images/57036-f9dbd7810d38ae95.png)](https://www.coolapk.com/apk/247826) [![coolapk1.png](https://upload-images.jianshu.io/upload_images/57036-6cf24d0c9efe8362.png)](https://www.coolapk.com/apk/247826)

---

# Thanks to

[AndroidX](https://developer.android.google.cn/jetpack/androidx)

[Jetpack](https://developer.android.google.cn/jetpack/)

[material-components-android](https://github.com/material-components/material-components-android)

[Qingting](https://play.google.com/store/apps/details?id=com.tencent.qqmusiclocalplayer)

[AndroidSlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel)

Image materials used in this project are from [UnSplash](https://unsplash.com/), which provides **free licensed images**.

Audio materials used in this project are from [BenSound](https://www.bensound.com/), which provides **free licensed music**.

---

# Who is Using

According to anonymous surveys of friends' "open-source library usage," by May 28, 2022, we learned that major companies, including "Tencent Music, NetEase, BMW, TCL," have referenced or are using this architecture model we open-sourced, or are using frameworks like [UnPeek-LiveData](https://github.com/KunMinX/UnPeek-LiveData) that we maintain.

We have updated the statistics in the relevant open-source library ReadMe, and those who missed the survey don't need to worry. We will continue to keep it open and periodically update the companies and products listed in the form, inviting more people to participate in using and providing feedback on the "architecture components" to foster continuous evolution and upgrades.

https://wj.qq.com/s2/8362688/124a/

| Group / Company / Brand / Team            | Product               |
| ----------------------------------------- | --------------------- |
| Tencent Music                             | QQ Music              |
| NetEase                                   | NetEase Cloud Music   |
| TCL                                       | Built-in File Manager |
| Guizhou Broadcasting Network              | LeBoBo                |
| Shanghai Yibo Health Management Co., Ltd. | Anobo                 |
|                                           | XiaoLaJiao            |
| ezen                                      | Egshig Music          |
| BMW                                       | Speech                |
| Shanghai Hujiao Information Co., Ltd.     | ZhixinHuixue Teacher  |
| MeishuBao                                 | TanChangBao           |
|                                           | Net Security          |
| ByteDance Livestream                      | Livestream SDK        |
| OnePlus Mobile                            | OPNote                |

---

# My Pages

Email：[kunminx@gmail.com](mailto:kunminx@gmail.com)

Juejin：[KunMinX at Juejin](https://juejin.im/user/58ab0de9ac502e006975d757/posts)

[《Learning Android Again》 Column](https://xiaozhuanlan.com/kunminx)

Paid readers add WeChat to join the group: myatejx

[![Learning Android Again Small Column](https://images.xiaozhuanlan.com/photo/2021/d493a54a32e38e7fbcfa68d424ebfd1e.png)](https://xiaozhuanlan.com/kunminx)

---

# License

```
Copyright 2019-present KunMinX

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
