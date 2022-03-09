package org.lasque.twsdkvideo.video_beauty;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;

import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.lasque.tusdk.impl.TuSpecialScreenHelper;
import org.lasque.twsdkvideo.video_beauty.utils.AppManager;
import org.lasque.twsdkvideo.video_beauty.utils.LanguageUtil;

import java.util.Locale;

/**
 * @author xujie
 * @Date 2018/10/29
 */

public class ScreenAdapterActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getInstance().addActivity(this); //添加到栈中
        if(TuSpecialScreenHelper.isNotchScreen())
        {
            setTheme(android.R.style.Theme_NoTitleBar);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList locales = getResources().getConfiguration().getLocales();
            Locale locale = getResources().getConfiguration().locale;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = getResources().getConfiguration().locale;
        }
        AppManager.getInstance().finishActivity(this); //从栈中移除
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = LanguageUtil.setLocal(newBase);
        super.attachBaseContext(context);

        Locale locale = getResources().getConfiguration().locale;

    }

    /***
     * 由于主项目有引入androidX 相关包 ，需要此方法，否则语言切换不成功
     * @param overrideConfiguration
     */
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        // 兼容androidX在部分手机切换语言失败问题
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
        Locale locale = getResources().getConfiguration().locale;
    }

}
