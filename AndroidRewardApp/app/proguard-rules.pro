-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepattributes InnerClasses

-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
-keep public class com.google.android.gms.** { public protected *; }
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-dontwarn android.media.Spatializer$OnSpatializerStateChangedListener

-dontwarn android.media.Spatializer
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface

-dontwarn org.jetbrains.annotations.**

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum * { *; }
-keep class com.google.code.gson.* { *; }
-keepattributes *Annotation*, Signature, Exception
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep public class com.google.android.gms.** { public protected *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

#appcompat
-keep class androidx.appcompat.** {
    public protected *;
}

#material
-keep class com.google.android.material.** {
    public protected *;
}

# Google Play Services Auth (Google Sign-In)
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.auth.api.identity.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-dontwarn com.google.android.gms.auth.**
-dontwarn com.google.android.gms.auth.api.signin.**
-dontwarn com.google.android.gms.auth.api.identity.**
-dontwarn com.google.android.gms.common.api.**
-dontwarn com.google.android.gms.tasks.**

# PinEntryEditText
-keep class com.alimuzaffar.lib.pinentryedittext.** { *; }
-dontwarn com.alimuzaffar.lib.pinentryedittext.**

# Gson Converter for Retrofit
-keep class retrofit2.converter.gson.** { *; }
-dontwarn retrofit2.converter.gson.**

# OkHttp Interceptors
-keep class okhttp3.logging.** { *; }
-dontwarn okhttp3.logging.**

# Preserve classes used by reflection in Retrofit and OkHttp
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
   @retrofit2.http.* <methods>;
}

# Keep all Retrofit annotations
-keep @retrofit2.http.* class * { *; }


-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers enum * { *; }
-keep class com.google.code.gson.* { *; }
-keepattributes *Annotation*, Signature, Exception
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# AndroidX Lifecycle
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Keep classes for ViewModel and LiveData
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }
-keep class androidx.lifecycle.LifecycleObserver { *; }
-keep class androidx.lifecycle.LifecycleOwner { *; }
-keep class androidx.lifecycle.Lifecycle { *; }
-keep class androidx.lifecycle.LifecycleRegistry { *; }
-keep class androidx.lifecycle.Lifecycle$Event { *; }

# Keep classes from lifecycle-common-java8 (for Java 8 features)
-keep class androidx.lifecycle.DefaultLifecycleObserver { *; }
-keep class androidx.lifecycle.LifecycleObserver { *; }
-keep class androidx.lifecycle.LifecycleOwner { *; }

# Keep Lifecycle related annotations
-keep class androidx.lifecycle.OnLifecycleEvent { *; }

# If you're using LiveData transformations, keep the following classes
-keep class androidx.lifecycle.Transformations$* { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.analytics.FirebaseAnalytics { *; }
-keep class com.google.firebase.analytics.FirebaseAnalytics$* { *; }
-keep class com.google.android.gms.measurement.** { *; }
-dontwarn com.google.firebase.analytics.**
-dontwarn com.google.android.gms.measurement.**

# Keep Firebase Analytics Event Names
-keep class com.google.firebase.analytics.FirebaseAnalytics$Event { *; }
-keep class com.google.firebase.analytics.FirebaseAnalytics$Param { *; }

# Keep Firebase Analytics related methods
-keepclassmembers class com.google.firebase.analytics.FirebaseAnalytics {
    public void logEvent(java.lang.String, android.os.Bundle);
    public void setUserProperty(java.lang.String, java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Keep filenames and line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Keep JavascriptInterface for WebView bridge
-keepattributes JavascriptInterface

# Sometimes keepattributes is not enough to keep annotations
-keep class android.webkit.JavascriptInterface {
   *;
}

# Keep all classes in Unity Ads package
-keep class com.unity3d.ads.** {
   *;
}

# Keep all classes in Unity Services package
-keep class com.unity3d.services.** {
   *;
}

-keep class com.google.android.gms.ads.initialization.** {
        *;
}

-keep class com.google.android.gms.ads.MobileAds {
        *;
}

-dontwarn com.google.ads.mediation.admob.*
-dontwarn com.google.android.gms.ads.**

-keep public class com.google.android.gms.ads.internal.ClientApi {
  <init>();
}

-dontwarn android.view.Surface
-dontwarn android.media.ApplicationMediaCapabilities
-dontwarn android.media.MediaFeature
-dontwarn android.media.ApplicationMediaCapabilities$Builder
-dontwarn android.media.MediaFeature$HdrType
-dontwarn android.media.AudioAttributes$Builder
-dontwarn android.adservices.measurement.MeasurementManager

-dontwarn android.content.pm.ApkChecksum
-dontwarn android.content.pm.PackageManager$OnChecksumsReadyListener
# Only for the requestChecksums method, but sadly -dontwarn can't take just a single method.
-dontwarn android.content.pm.PackageManager

-keepclassmembers class * extends com.google.android.gms.internal.ads.zzgpm {
  <fields>;
}

-dontwarn com.google.android.gms.ads.internal.util.zzx

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#noinspection ShrinkerUnresolvedReference
#unity
-keep class com.google.android.gms.ads.** {public *;}
-keep class com.google.android.gms.appset.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

#sdk
-dontwarn com.ironsource.**
-dontwarn com.ironsource.adapters.**
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-keep class com.ironsource.adapters.** { *;
}
#omid
-dontwarn com.iab.omid.**
-keep class com.iab.omid.** {*;}
#javascript
-keepattributes JavascriptInterface
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
#For AmazonAps integration
-keep class com.amazon.device.ads.DtbThreadService {
    static *;
}
-keep public interface com.amazon.device.ads** {*; }
#For AppLovin integration
-keepclassmembers class com.applovin.sdk.AppLovinSdk {
    static *;
}
-keep public interface com.applovin.sdk** {*; }
-keep public interface com.applovin.adview** {*; }
-keep public interface com.applovin.mediation** {*; }
-keep public interface com.applovin.communicator** {*; }
#For Bytedance integration
-keep public interface com.bytedance.sdk.openadsdk** {*; }
#For Facebook integration
-keepclassmembers class com.facebook.ads.internal.AdSdkVersion {
    static *;
}
-keepclassmembers class com.facebook.ads.internal.settings.AdSdkVersion {
    static *;
 }
-keepclassmembers class com.facebook.ads.BuildConfig {
    static *;
 }
-keep public interface com.facebook.ads** {*; }

-keep class com.makeopinion.cpxresearchlib.** { *; }
-dontwarn org.conscrypt.*

-optimizationpasses 5
-keepattributes SourceFile,LineNumberTable
-dontwarn android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener
-dontwarn android.media.LoudnessCodecController


-keep class com.applovin.** {  }
-dontwarn com.applovin.*
-keep class com.applovin.mediation.facebook.** {  }
-dontwarn com.applovin.mediation.facebook.*
-keep class com.unity3d.ads.** { }
-dontwarn com.unity3d.ads.*
-keep public class com.google.android.gms.ads.** {   }
-dontwarn com.google.android.gms.ads.*
-keep class com.unity3d.ads.mediation.** {  }
-dontwarn com.unity3d.ads.mediation.*
-keep class com.unity3d.ads.mediation.adquality.** {  }
-dontwarn com.unity3d.ads.mediation.adquality.*
-keep class com.vungle.** {  }
-dontwarn com.vungle.*
-keep class com.inmobi.** {  }
-dontwarn com.inmobi.*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**