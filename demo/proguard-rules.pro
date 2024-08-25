# 启动优化相关的一些配置
# 指定更精细级别的优化
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
# 表示对代码优化的次数，一般为 5
-optimizationpasses 5
# 允许改变作用域
-allowaccessmodification
# 关闭预验证
-dontpreverify

# 表示混淆时不使用大小写混合类名
-dontusemixedcaseclassnames

# 表示不跳过 library 中的非 public 类
-dontskipnonpubliclibraryclasses

# 表示打印混淆的详细信息
-verbose

#表示对注解中的参数进行保留
-keepattributes *Annotation*


# 表示不混淆任何包含 native 方法的类名以及 native 方法名，这个和刚才验证的结果是一致的
-keepclasseswithmembernames class * {
    native <methods>;
}

# 表示不混淆 View 中的 setXXX() 和 getXXX() 方法，因为属性动画需要有相应的 setter 和 getter 方法实现
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# 表示不混淆 Activity 中参数是 View 的方法，因为有这么一种用法，在 XML 中配置 android:onClick="btnClick" 属性，混淆就找
# 不到了
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 表示不混淆枚举的 values() 和 valueOf() 方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 表示不混淆 Parcelable 实现类中的 CREATOR 字段，毫无疑问，CREATOR 字段是绝对不能改变的，包括大小写都不能变，不然整个
# Parcelable 工作机制都会失效
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 表示不混淆 R 文件中的所有静态字段，我们都知道 R 文件是通过字段来记录每个资源 id ，字段名如果被混淆，id 就找不到了
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 表示对 android.support 包下的代码不警告，因为 support 包中的所有代码都在兼容性上做了足够的判断，因此不用担心代码会出问题
# 所以直接忽略警告就可以了
-dontwarn android.support.**


