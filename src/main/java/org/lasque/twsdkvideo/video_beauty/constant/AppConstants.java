package org.lasque.twsdkvideo.video_beauty.constant;

import static org.lasque.tusdk.core.TuSdkContext.getString;

import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.BackgroundMusicBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;

public class AppConstants {

    // 2:返回 1:点击下一步  0:裁剪
    public static int EDIT_TYPE = 1;

    // 0:拍摄添加音乐  1:编辑视频添加音乐
    public static int ENTER_STATE = -1;

    // 添加的音乐的本地地址
    public static String musicLocalPath;

    // 拍摄时添加背景音乐实体类
    public static BackgroundMusicBean shootBackgroundMusicBean;

    // 选择照片
    public static int SELECT_PHOTO = 0;

    // 选择视频
    public static int SELECT_VIDEO = 1;

    // 从添加贴图进入相册
    public static int STICKER_ENTER = 3;

    // 拍摄是否需要添加草稿箱
    public static boolean isSaveDraft = false;

    // 样式名称
    public static List<String> textStringNames;

    //filter样式名称
    public static List<String> textStringFilterNames;

    //filter样式具体类别下的item名称
    public static List<Integer> textStringFilterTypeItemNamesFirst = Arrays.asList(R.string.lsq_filter_Portrait_Bright_1, R.string.lsq_filter_Portrait_Pale_1, R.string.lsq_filter_Portrait_Clear_1, R.string.lsq_filter_Portrait_Moisten_1,
            R.string.lsq_filter_Portrait_Spring_1, R.string.lsq_filter_Portrait_Bellflower_1, R.string.lsq_filter_Portrait_Cherry_1,  R.string.lsq_filter_Portrait_First_1,R.string.lsq_filter_Portrait_Pure_1,
            R.string.lsq_filter_Portrait_Natural_1, R.string.lsq_filter_Portrait_Retro_1, R.string.lsq_filter_Portrait_Soda_1, R.string.lsq_filter_Portrait_Indifference_1, R.string.lsq_filter_Portrait_Rosy_1, R.string.lsq_filter_Portrait_Icream_1, R.string.lsq_filter_Portrait_Ruddy_1,
            R.string.lsq_filter_Portrait_Dusk_1, R.string.lsq_filter_Portrait_Cream_1, R.string.lsq_filter_Portrait_Texture_1, R.string.lsq_filter_Portrait_Dawn_1, R.string.lsq_filter_Portrait_Milk_1, R.string.lsq_filter_Portrait_Candy_1,
            R.string.lsq_filter_Portrait_Warm_1, R.string.lsq_filter_Portrait_Sweet_1, R.string.lsq_filter_Portrait_Fog_1, R.string.lsq_filter_Portrait_Desserts_1);

    public static List<Integer> textStringFilterTypeItemNamesSecond = Arrays.asList(
            R.string.lsq_filter_Food_Delicious_1, R.string.lsq_filter_Food_Lemon_1, R.string.lsq_filter_Food_Light_1, R.string.lsq_filter_Food_Whisky_1, R.string.lsq_filter_Food_Caramel_1, R.string.lsq_filter_Food_Peach_1, R.string.lsq_filter_Food_Honey_1, R.string.lsq_filter_Food_Mangosteen_1, R.string.lsq_filter_Food_Lime_1);

    public static List<Integer> textStringFilterTypeItemNamesThird = Arrays.asList(
            R.string.lsq_filter_Scenery_SNight_1, R.string.lsq_filter_Scenery_Flower_1, R.string.lsq_filter_Scenery_Rain_1, R.string.lsq_filter_Scenery_Fresh_1, R.string.lsq_filter_Scenery_Izu_1, R.string.lsq_filter_Scenery_Innocence_1, R.string.lsq_filter_Scenery_Tale_1, R.string.lsq_filter_Scenery_Picnic_1, R.string.lsq_filter_Scenery_Blue_1,
            R.string.lsq_filter_Scenery_Birch_1, R.string.lsq_filter_Scenery_Afternoon_1, R.string.lsq_filter_Scenery_City_1, R.string.lsq_filter_Scenery_Film_1, R.string.lsq_filter_Scenery_Silent_1, R.string.lsq_filter_Scenery_Lautumn_1, R.string.lsq_filter_Scenery_Sunrise_1, R.string.lsq_filter_Scenery_Sunset_1, R.string.lsq_filter_Scenery_Grace_1, R.string.lsq_filter_Scenery_Withered_1);

    public static List<Integer> textStringFilterTypeItemNamesFourth = Arrays.asList(
            R.string.lsq_filter_Sharp_Dark_1, R.string.lsq_filter_Sharp_Contrast_1, R.string.lsq_filter_Sharp_Dream_1, R.string.lsq_filter_Sharp_Nostalgia_1, R.string.lsq_filter_Sharp_Imprint_1, R.string.lsq_filter_Sharp_Messy_1, R.string.lsq_filter_Sharp_Mottled_1, R.string.lsq_filter_Sharp_Quiet_1, R.string.lsq_filter_Sharp_Otimes_1, R.string.lsq_filter_Sharp_Past_1,
            R.string.lsq_filter_Sharp_Pink_1, R.string.lsq_filter_Sharp_Ash_1, R.string.lsq_filter_Sharp_Neon_1, R.string.lsq_filter_Sharp_Red_1, R.string.lsq_filter_Sharp_History_1, R.string.lsq_filter_Sharp_Black_1, R.string.lsq_filter_Sharp_Orange_1);

    // font未选中样式
    public static List<Integer> unSelectFontIcons = Arrays.asList(R.drawable.font_option_1_unselect, R.drawable.font_option_2_unselect, R.drawable.font_option_3_unselect, R.drawable.font_option_4_unselect, R.drawable.font_option_5_unselect, R.drawable.font_option_6_unselect,  R.drawable.font_option_7_unselect,R.drawable.font_option_8_unselect,R.drawable.font_option_9_unselect);
   // font选中样式
    public static List<Integer> selectFontIcons = Arrays.asList(R.drawable.font_option_1_select, R.drawable.font_option_2_select, R.drawable.font_option_3_select, R.drawable.font_option_4_select, R.drawable.font_option_5_select, R.drawable.font_option_6_select,  R.drawable.font_option_7_select,R.drawable.font_option_8_select,R.drawable.font_option_9_select);
    // font选中样式
    public static List<String> fontName = Arrays.asList(
            "",//默认roboto字体
            "IBMPlexMono-Medium.ttf",
            "RopaSans-Regular.ttf",
            "AlfaSlabOne-Regular.ttf",
            "Coiny-Regular.ttf",
            "Romanesco-Regular.ttf",
            "Sacramento-Regular.ttf",
            "Mansalva-Regular.ttf",
            "VampiroOne-Regular.ttf");



    // style未选中样式
    public static List<Integer> unSelectStyleIcons = Arrays.asList(R.drawable.style_option_1_unselect, R.drawable.style_option_2_unselect, R.drawable.style_option_3_unselect);
    // style选中样式
    public static List<Integer> selectStyleIcons = Arrays.asList(R.drawable.style_option_1_select, R.drawable.style_option_2_select, R.drawable.style_option_3_select);

    // 颜色颜色
    public static List<Integer> colors = Arrays.asList(R.color.color_ffffff, R.color.color_000000, R.color.color_3897F0, R.color.color_70C050, R.color.color_FDCB5C, R.color.color_FD8D32, R.color.color_ED4956, R.color.color_D10869, R.color.color_A306BA, R.color.color_ED0013, R.color.color_ED858E, R.color.color_FFD2D3, R.color.color_FFDBB4, R.color.color_FFC382, R.color.color_D28E46, R.color.color_996438, R.color.color_432323, R.color.color_1C4A29, R.color.color_262626, R.color.color_363636, R.color.color_555555, R.color.color_737373, R.color.color_999999, R.color.color_C7C7C7, R.color.color_DBDBDB, R.color.color_EFEFEF);

    // Alignment未选中样式
    public static  List<Integer> unSelectAlignmentIcons = Arrays.asList(R.drawable.aligment_option_1_unselect,R.drawable.aligment_option_2_unselect,R.drawable.aligment_option_3_unselect);
   // Alignment选中状态
    public static  List<Integer> selectAlignmentIcons = Arrays.asList(R.drawable.aligment_option_1_select,R.drawable.aligment_option_2_select,R.drawable.aligment_option_3_select);

    // Direction未选中样式
    public static  List<Integer> unSelectDirectionIcons = Arrays.asList(R.drawable.direction_option_1_unselect,R.drawable.direction_option_2_unselect);
    // Direction选中样式
    public static  List<Integer> selectDirectionIcons = Arrays.asList(R.drawable.direction_option_1_select,R.drawable.direction_option_2_select);
    // 下载音乐Call集合
    public  static List<Call> callList = new ArrayList<>();
}