package org.lasque.twsdkvideo.video_beauty.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

/**
 *
 result = result.toLowerCase();
 result = result.replaceAll("_", "-");
 if(result.contains("-tw") || result.contains("-hant")) {
 result = "zh_Hant_TW";
 } else if(result.contains("-cn") || result.contains("-hans")) {
 result = "zh_CN";
 } else if(result.contains("pt-br")) {
 result = "pt_BR";
 } else if(result.contains("-") == true) {
 result = result.split("-").first;

 switch(result) {
 case "bn":
 result = "bn_BD";
 break;
 case "en":
 result = "en_US";
 break;
 case "de":
 result = "de_DE";
 break;
 case "es":
 result = "es_ES";
 break;
 case "fr":
 result = "fr_FR";
 break;
 case "ha":
 result = "ha_NG";
 break;
 case "hi":
 result = "hi_IN";
 break;
 case "hu":
 result = "hu_HU";
 break;
 case "id":
 result = "id_ID";
 break;
 case "it":
 result = "it_IT";
 break;
 case "ja":
 result = "ja_JP";
 break;
 case "ko":
 result = "ko_KR";
 break;
 case "ms":
 result = "ms_MY";
 break;
 case "pt":
 result = "pt_PT";
 break;
 case "so":
 result = "so_SO";
 break;
 case "tl":
 result = "tl_PH";
 break;
 case "th":
 result = "th_TH";
 break;
 }
 }

 */
public class LanguageUtil {
    private static Locale mlanguage = Locale.ENGLISH;
    public static void changeLanguage(Context context, String language) {
        String language1 = context.getResources().getConfiguration().locale.toString();
        //应用内配置语言
        Resources resources = context.getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        switch (language.toLowerCase()) {
            case "zh_cn":
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
                mlanguage=Locale.SIMPLIFIED_CHINESE;
                break;
            case "en":
            case "en_us":
                config.setLocale(Locale.ENGLISH);
                mlanguage=Locale.ENGLISH;
                break;
            case "ja_jp": //日文
                config.setLocale(Locale.JAPANESE);
                mlanguage=Locale.JAPANESE;
                break;
            case "zh_hant_tw": //中文繁体
                config.setLocale(Locale.TRADITIONAL_CHINESE);
                mlanguage=Locale.TRADITIONAL_CHINESE;
                break;
            case "ko": //韩文
                config.setLocale(Locale.KOREAN);
                mlanguage=Locale.KOREAN;
                break;
            default:
                config.setLocale(Locale.ENGLISH);
                mlanguage=Locale.ENGLISH;
                break;
        }
        resources.updateConfiguration(config, dm);
    }
    private static Locale getLocaleByLanguage(String language) {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        if (language == Locale.SIMPLIFIED_CHINESE.getLanguage()) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language == Locale.ENGLISH.getLanguage()) {
            locale = Locale.ENGLISH;
        }
        return locale;
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(mlanguage);
        return context.createConfigurationContext(configuration);
    }

    /**
     * 初始化语言 方法
     *
     * @param context
     */
    public static Context setLocal(Context context) {
        return setApplicationLanguage(context);
    }

    /**
     * 设置语言类型
     */
    public static Context setApplicationLanguage(Context context) {

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = mlanguage;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            Locale.setDefault(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
        }
        resources.updateConfiguration(config, dm);
        return context;
    }
}
