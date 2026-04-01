##################################
# General Android & App Rules
##################################
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.Application

-keepclassmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclassmembers class * {
    public void set*(***);
    public *** get*();
}

##################################
# Firebase (Auth, Firestore, Database, Storage)
##################################
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

##################################
# Glide
##################################
-keep class com.bumptech.glide.** { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep class * implements com.bumptech.glide.module.GlideModule { *; }
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** { *; }
-dontwarn com.bumptech.glide.**

##################################
# Lottie
##################################
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

##################################
# Android-SpinKit
##################################
-keep class com.github.ybq.** { *; }
-dontwarn com.github.ybq.**

##################################
# CircleImageView
##################################
-keep class de.hdodenhof.circleimageview.** { *; }

##################################
# RoundedImageView
##################################
-keep class com.makeramen.roundedimageview.** { *; }

##################################
# Shimmer
##################################
-keep class com.facebook.shimmer.** { *; }

##################################
# Google Play Services Auth
##################################
-keep class com.google.android.gms.auth.** { *; }
-dontwarn com.google.android.gms.**

##################################
# ViewPager2 and Support Libraries
##################################
-keep class androidx.viewpager2.** { *; }
-dontwarn androidx.viewpager2.**
-dontwarn androidx.**

##################################
# Gson
##################################
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }

##################################
# Prevent removal of app classes (reflection use)
##################################
-keep class com.sourav.aiotclub1.** { *; }

##################################
# Optional: Suppress sun.misc.Unsafe warning (only if needed)
##################################
-dontwarn sun.misc.Unsafe

##################################
# Firebase/GMS annotation retention
##################################
-keepattributes RuntimeVisibleAnnotations
# XR-related dontwarn rules from missing_rules.txt
-dontwarn com.android.extensions.xr.XrExtensionResult
-dontwarn com.android.extensions.xr.XrExtensions
-dontwarn com.android.extensions.xr.function.Consumer
-dontwarn com.android.extensions.xr.node.InputEvent$HitInfo
-dontwarn com.android.extensions.xr.node.InputEvent
-dontwarn com.android.extensions.xr.node.Mat4f
-dontwarn com.android.extensions.xr.node.Node
-dontwarn com.android.extensions.xr.node.NodeTransaction
-dontwarn com.android.extensions.xr.node.NodeTransform
-dontwarn com.android.extensions.xr.node.Vec3
-dontwarn com.android.extensions.xr.splitengine.SplitEngineBridge
-dontwarn com.android.extensions.xr.subspace.Subspace
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
# Keep ExoPlayer classes
-keep class com.google.android.exoplayer2.** { *; }
-keep interface com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**
