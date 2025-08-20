# Add project specific ProGuard rules here.
# You can control the obfuscation, shrinking, and optimization of your code.
# For more details, see https://www.guardsquare.com/manual/configuration

# Keep the main Fabric mod class
-keep class dev.lvstrng.argon.Main { *; }

# Keep all Mixins
-keep class dev.lvstrng.argon.mixin.** { *; }

# Keep event system
-keep class dev.lvstrng.argon.event.** { *; }

# Keep modules and their settings
-keep class dev.lvstrng.argon.module.** { *; }

# Keep managers
-keep class dev.lvstrng.argon.managers.** { *; }

# Keep utilities
-keep class dev.lvstrng.argon.utils.** { *; }

# Keep fonts
-keep class dev.lvstrng.argon.font.** { *; }

# Keep Fabric Loader classes
-keep class net.fabricmc.loader.api.** { *; }
-keep class net.fabricmc.api.** { *; }
-keep class net.fabricmc.fabric.api.** { *; }

# Keep Mixin classes
-keep class org.spongepowered.asm.mixin.** { *; }
-keep class org.objectweb.asm.** { *; }

# Keep constructors and native methods
-keep public class * { public <init>(...); }
-keep public class * { native <methods>; }

# Keep annotations (important for Mixin)
-keepattributes Signature,InnerClasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable,Exceptions,ConstantValue,Synthetic,Bridge,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault,MethodParameters

# Suppress warnings for common libraries
-dontwarn java.**
-dontwarn sun.misc.**
-dontwarn org.lwjgl.**
-dontwarn net.minecraft.**
-dontwarn com.google.gson.**
-dontwarn org.spongepowered.asm.mixin.**
-dontwarn org.objectweb.asm.**

# Suppress warnings for your own obfuscated code
-dontwarn dev.lvstrng.argon.**
