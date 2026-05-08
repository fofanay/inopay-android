# Inopay UI — règles ProGuard transmises aux apps qui consomment cette lib.
# Compose génère beaucoup de classes synthétiques ; on conserve celles utilisées en runtime.
-keep class com.inopay.ui.** { *; }
-keepclassmembers class com.inopay.ui.** { *; }
