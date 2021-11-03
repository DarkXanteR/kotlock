## Overview
Kotlin/JVM coroutine-based distributed locks.

Like `ShedLock` but kotlin-first.

## Distribution

Available from `jitpack.io`:

```kotlin
repositories {
    maven { setUrl("https://jitpack.io") }
}
```

## Usage

### SQL databases

#### Configure SQL Provider
First, create lock table (please note that name has to be primary key)

```sql
-- PostgreSQL
CREATE TABLE kotlocks(name VARCHAR(64) NOT NULL, lock_until TIMESTAMPTZ NOT NULL,
    locked_at TIMESTAMPTZ NOT NULL, locked_by VARCHAR(255) NOT NULL, PRIMARY KEY (name));
```
Add dependency
```kotlin
dependencies {
    implementation("com.github.darkxanter:kotlock-sql-exposed:$kotlockVersion")
}
```

### MongoDB

```kotlin
dependencies {
    implementation("com.github.darkxanter:kotlock-mongo-reactive-streams:$kotlockVersion")
}
```
