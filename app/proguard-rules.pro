# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.gothwad.tvlauncher.**$$serializer { *; }
-keepclassmembers class com.gothwad.tvlauncher.** {
    *** Companion;
}
-keepclasseswithmembers class com.gothwad.tvlauncher.** {
    kotlinx.serialization.KSerializer serializer(...);
}
