# Sea for ListenBrainz

Pretty much just an excuse to use a bunch of cool stuff together in one not very cool project.

Uses:
- [Workflows](https://github.com/square/workflow-kotlin)
- [Compose](https://developer.android.com/jetpack/compose)
- [Workflows + Compose = <3](https://github.com/square/workflow-kotlin-compose)
- [Hephaestus](https://github.com/square/hephaestus)
- [Kotlin 1.4-M3](https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-is-out-standard-library-changes/)

## Q&A

#### _Was it fun to write?_

No, not really. Compose is still so new and in-flux, I couldn't get _any_ sort of compilation generation working (except for Hephaestus) which meant no automatic `Parcelable` which means writing jankness with Moshi to use that for Workflow snapshotting.

#### _Was it cool to write?_

Super. Workflows are great. Compose is great. Hephaestus is....unnecessary for a project of this size but [Ralf](https://github.com/vRallev) doesn't get enough love and Hephaestus is seriously so darn amazing.

#### _Would you do it again?_

No. Wait for Compose to at least get in an alpha/beta state. Wait for 1.4 to get to RC status. Wait for [Zach](https://github.com/zach-klippenstein) to spend another weekend hacking on `workflow-kotlin-compose`.

#### _Why did/didn't you do X?_

Because I wanted to, didn't want to, or didn't know about it. You should definitely do it, though.

#### _I checked out this project and it doesn't build. Something about the `workflow-kotlin-compose` library?_

Yeah...there was something weird going on there, so I ended up checking out that project locally and using some Gradle magic to use my local version instead of the hosted version. It's probably fixed by now, though. Just remove the substitution and include build code from `settings.gradle` and it'll probably work. If not, then this is just a scientific repo that was never meant to be reproduced, only observed.