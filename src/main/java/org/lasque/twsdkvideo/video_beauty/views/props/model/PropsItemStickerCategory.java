package org.lasque.twsdkvideo.video_beauty.views.props.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkBundle;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.json.JsonHelper;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.CustomStickerGroup;
import org.lasque.twsdkvideo.video_beauty.data.GVisionDynamicStickerBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.twsdkvideo.video_beauty.views.props.model
 *
 * @author sprint
 * @Date 2018/12/28 11:19 AM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/
// 贴纸分类
public class PropsItemStickerCategory extends PropsItemCategory<PropsItemSticker>{

    public PropsItemStickerCategory(List<PropsItemSticker> stickerPropsItems) {
        super(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSticker,stickerPropsItems);
    }
    public static HttpURLConnection getConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
        connection.connect();
        return connection;

    }


    public static void testDownLoad(){
        BufferedInputStream bis =null;
        BufferedOutputStream bos=null;
        String HTTP_URL="https://hls-app.live-dev.tk/uploads/lsq_sticker_0_1542.gsce"; //图片地址
        try {
            int contentLength = getConnection(HTTP_URL).getContentLength();
            System.out.println("文件的大小是:"+contentLength);
            if (contentLength>32) {
                InputStream is= getConnection(HTTP_URL).getInputStream();
                bis = new BufferedInputStream(is);

                FileOutputStream fos = new FileOutputStream("/data/data/com.yhkj.app/files/stickers/lsq_sticker_0_1542.gsce");
                bos= new BufferedOutputStream(fos);
                int b = 0;
                byte[] byArr = new byte[1024];
                while((b= bis.read(byArr))!=-1){
                    bos.write(byArr, 0, b);
                }
                Log.e("下载的文件的大小是","...."+contentLength);
                System.out.println("下载的文件的大小是----------------------------------------------:"+contentLength);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(bis !=null){
                    bis.close();
                }
                if(bos !=null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取本地贴纸
     * @return
     */
    public static void prepareLocalSticker(Context context) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                testDownLoad();
//            }
//        }).start();

        try
        {


            String asset = TuSdkBundle.sdkBundleOther(TuSdk.SDK_CONFIGS);
            String json = TuSdkContext.getAssetsText(asset);
            String master = JsonHelper.json(json).getString("master");



            File stickerDir = new File( context.getFilesDir(),"stickers");

            // return new File(TuSdk.getAppTempPath(), String.format("lsq_%s.mp4", StringHelper.timeStampString()));

            if (!stickerDir.exists()) {
                return ;
            }

            String[] stickerPaths = stickerDir.list();

            for (String stickerFileName : stickerPaths) {

                // 解析该文件贴纸id (开发者可自己做对照表，这里根据文件名解析id)
                String groupId = stickerFileName.substring(stickerFileName.lastIndexOf("_") + 1,stickerFileName.lastIndexOf("."));

                File stickerFile = new File(stickerDir,stickerFileName);



                boolean result = StickerLocalPackage.shared().addStickerGroupFile(stickerFile,Long.parseLong(groupId),master);

                TLog.e("result" + result);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }





//        List<StickerGroup> localList = StickerLocalPackage.shared().getSmartStickerGroups();

    }


    /**
     * 获取所有贴纸分类
     *
     * @return List<PropsItemStickerCategory>
     */
    public static List<PropsItemStickerCategory> allCategories(Context context, GVisionDynamicStickerBean gVisionDynamicStickerBean) {

        prepareLocalSticker(context);
        if(1==1){

            return  loadServerSticker(context,gVisionDynamicStickerBean);
        }
      //  Log.e("sdfsfsdfsfsdfsfsdfsfsf","......"+context.getFilesDir().getAbsolutePath());



        List<PropsItemStickerCategory> categories = new ArrayList<>();

        try {
            InputStream stream = TuSdkContext.context().getResources().openRawResource(R.raw.customstickercategories);

            if (stream == null) return null;

            byte buffer[] = new byte[stream.available()];
            stream.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = JsonHelper.json(json);
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                // 该分类下的所有贴纸道具
                List<PropsItemSticker> propsItems = new ArrayList<PropsItemSticker>();

                JSONArray jsonArrayGroup = item.getJSONArray("stickers");

                for (int j = 0; j < jsonArrayGroup.length(); j++) {

                    JSONObject itemGroup = jsonArrayGroup.getJSONObject(j);
                    CustomStickerGroup group = new CustomStickerGroup();
                    group.groupId = itemGroup.optLong("id");
                    group.previewName = itemGroup.optString("previewImage");
                    group.name = itemGroup.optString("name");

                    PropsItemSticker propsItem = new PropsItemSticker(group);
                    propsItems.add(propsItem);
                }

                // 该贴纸道具分类
                PropsItemStickerCategory category = new PropsItemStickerCategory(propsItems);
                 boolean isCN =   context.getResources().getConfiguration().locale.getCountry().equals("CN");
                category.setName(isCN?item.getString("categoryNameEn"):item.getString("categoryNameEn"));

                categories.add(category);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;

    }



    public static List<PropsItemStickerCategory> loadServerSticker(Context context,GVisionDynamicStickerBean gVisionDynamicStickerBean){
        List<PropsItemStickerCategory> categories = new ArrayList<>();
        for (int i = 0; i < gVisionDynamicStickerBean.getCategories().size(); i++) {
            // 该分类下的所有贴纸道具
            List<PropsItemSticker> propsItems = new ArrayList<PropsItemSticker>();
            for (int j = 0; j < gVisionDynamicStickerBean.getCategories().get(i).getStickers().size(); j++) {
                GVisionDynamicStickerBean.Stickers stickers =    gVisionDynamicStickerBean.getCategories().get(i).getStickers().get(j);
                CustomStickerGroup group = new CustomStickerGroup();
                group.groupId = Long.parseLong(stickers.getId());
                group.previewName = stickers.getPreviewImage();
                group.name =  stickers.getName();
                group.stickerUrl   =  stickers.getStickerUrl();
                group.setStickerId(stickers.getStickerId());
                PropsItemSticker propsItem = new PropsItemSticker(group);
                propsItems.add(propsItem);
            }
            // 该贴纸道具分类
            PropsItemStickerCategory category = new PropsItemStickerCategory(propsItems);
            boolean isCN =   context.getResources().getConfiguration().locale.getCountry().equals("CN");
            category.setName(isCN?gVisionDynamicStickerBean.getCategories().get(i).getCategoryName():gVisionDynamicStickerBean.getCategories().get(i).getCategoryNameEn());
            categories.add(category);
        }
        return  categories;
    }




}

