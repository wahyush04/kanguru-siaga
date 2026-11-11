# =====================================================
# ProGuard / R8 Rules for Kanguru Siaga
# =====================================================

# 1. Crashlytics readable stack traces & source attributes
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# 2. Preserve @Keep annotations and BuildConfig
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
-keep class com.kangurusiaga.app.BuildConfig { *; }

# 3. Room Databases, DAOs, Entities, and Migrations
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.migration.Migration
-keep class com.kangurusiaga.app.data.local.entity.** { *; }
-keep class com.kangurusiaga.app.core.database.** { *; }

# 4. Firebase Firestore DTOs (reflection/map serialization safety)
-keepclassmembers class com.kangurusiaga.app.data.remote.firestore.** {
    <fields>;
    <methods>;
}

# 5. Java/Kotlin Coroutines & JSON standard library compatibility
-dontwarn kotlinx.coroutines.debug.**
-dontwarn org.slf4j.**
