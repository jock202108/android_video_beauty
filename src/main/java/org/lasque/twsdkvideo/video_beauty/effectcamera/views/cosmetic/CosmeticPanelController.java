package org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic;

import android.content.Context;

import com.tusdk.pulse.DispatchQueue;
import com.tusdk.pulse.filter.Filter;
import com.tusdk.pulse.filter.FilterPipe;
import com.tusdk.pulse.filter.filters.TusdkCosmeticFilter;

import org.lasque.twsdkvideo.video_beauty.effectcamera.views.record.RecordView;
import org.lasque.twsdkvideo.video_beauty.tubeautysetting.Beauty;
import org.lasque.twsdkvideo.video_beauty.tubeautysetting.PipeMediator;
import org.lasque.tusdkpulse.core.seles.SelesParameters;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.BasePanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.blush.BlushPanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.eyebrow.EyebrowPanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.eyelash.EyelashPanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.eyeliner.EyelinerPanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.eyeshadow.EyeshadowPanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.facial.FacialPanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.lipstick.LipstickPanel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class CosmeticPanelController {
    public static HashMap<String, Float> mDefaultCosmeticPercentParams = new HashMap<String, Float>() {
        {
            put("lipAlpha",0.5f);
            put("blushAlpha",0.5f);
            put("eyebrowAlpha",0.4f);
            put("eyeshadowAlpha",0.5f);
            put("eyelineAlpha",0.5f);
            put("eyelashAlpha",0.5f);
            put("facialAlpha",0.4f);
        }
    };

    private static HashMap<String,Float> mDefaultCosmeticMaxPercentParams = new HashMap<String, Float>(){
        {
            put("lipAlpha",0.8f);
            put("blushAlpha",1.0f);
            put("eyebrowAlpha",0.7f);
            put("eyeshadowAlpha",1.0f);
            put("eyelineAlpha",1.0f);
            put("eyelashAlpha",1.0f);
            put("facialAlpha",1.0f);
        }
    };

    /**
     * ????????????
     */
    public static List<CosmeticTypes.LipstickType> mLipstickTypes = Arrays.asList(CosmeticTypes.LipstickType.values());

    /**
     * ????????????
     */
    public static List<CosmeticTypes.EyelashType> mEyelashTypes = Arrays.asList(CosmeticTypes.EyelashType.values());

    /**
     * ????????????
     */
    public static List<CosmeticTypes.EyebrowType> mEyebrowTypes = Arrays.asList(CosmeticTypes.EyebrowType.values());

    /**
     * ????????????
     */
    public static List<CosmeticTypes.BlushType> mBlushTypes = Arrays.asList(CosmeticTypes.BlushType.values());

    /**
     * ????????????
     */
    public static List<CosmeticTypes.EyeshadowType> mEyeshadowTypes = Arrays.asList(CosmeticTypes.EyeshadowType.values());

    /**
     * ????????????
     */
    public static List<CosmeticTypes.EyelinerType> mEyelinerTypes = Arrays.asList(CosmeticTypes.EyelinerType.values());

    /**
     * ????????????
     */
    public static List<CosmeticTypes.FacialType> mFacialTypes = Arrays.asList(CosmeticTypes.FacialType.values());



    private SelesParameters mEffect = new SelesParameters();

    private Context mContext;

    private Beauty mBeautyManager;

    public CosmeticPanelController(Context context){
        this.mContext = context;


    }

    public void initCosmetic(Beauty beauty){

        mBeautyManager = beauty;

        mEffect.setListener(new SelesParameters.SelesParametersListener() {
            @Override
            public void onUpdateParameters(SelesParameters.FilterModel model, String code, SelesParameters.FilterArg arg) {
                double value = arg.getValue();
                switch (arg.getKey()){
                    case "lipAlpha":
                        mBeautyManager.setLipOpacity((float) value);
                        break;
                    case "blushAlpha":
                        mBeautyManager.setBlushOpacity((float) value);
                        break;
                    case "eyebrowAlpha":
                        mBeautyManager.setBrowOpacity((float) value);
                        break;
                    case "eyeshadowAlpha":
                        mBeautyManager.setEyeshadowOpacity((float) value);
                        break;
                    case "eyelineAlpha":
                        mBeautyManager.setEyelineOpacity((float) value);
                        break;
                    case "eyelashAlpha":
                        mBeautyManager.setEyelashOpacity((float) value);
                        break;
                    case "facialAlpha":
                        mBeautyManager.setFacialOpacity((float) value);
                        break;
                }
            }
        });

        for (String key : mDefaultCosmeticPercentParams.keySet()){
            mEffect.appendFloatArg(key,mDefaultCosmeticPercentParams.get(key));
        }
    }

    public LipstickPanel getLipstickPanel() {
        if (mLipstickPanel == null){
            mLipstickPanel = new LipstickPanel(this);
        }
        return mLipstickPanel;
    }

    public BlushPanel getBlushPanel() {
        if (mBlushPanel == null){
            mBlushPanel = new BlushPanel(this);
        }
        return mBlushPanel;
    }

    public EyebrowPanel getEyebrowPanel() {
        if (mEyebrowPanel == null){
            mEyebrowPanel = new EyebrowPanel(this);
        }
        return mEyebrowPanel;
    }

    public EyeshadowPanel getEyeshadowPanel() {
        if (mEyeshadowPanel == null){
            mEyeshadowPanel = new EyeshadowPanel(this);
        }
        return mEyeshadowPanel;
    }

    public EyelinerPanel getEyelinerPanel() {
        if (mEyelinerPanel == null){
            mEyelinerPanel = new EyelinerPanel(this);
        }
        return mEyelinerPanel;
    }

    public EyelashPanel getEyelashPanel() {
        if (mEyelashPanel == null){
            mEyelashPanel = new EyelashPanel(this);
        }
        return mEyelashPanel;
    }

    public FacialPanel getFacialPanel(){
        if (mFacialPanel == null){
            mFacialPanel = new FacialPanel(this);
        }
        return mFacialPanel;
    }

    public BasePanel getPanel(CosmeticTypes.Types types){
        BasePanel panel = null;
        switch (types){
            case Lipstick:
                panel = getLipstickPanel();
                break;
            case Blush:
                panel = getBlushPanel();
                break;
            case Eyebrow:
                panel = getEyebrowPanel();
                break;
            case Eyeshadow:
                panel = getEyeshadowPanel();
                break;
            case Eyeliner:
                panel = getEyelinerPanel();
                break;
            case Eyelash:
                panel = getEyelashPanel();
                break;
            case Facial:
                panel = getFacialPanel();
                break;
        }
        return panel;
    }

    private LipstickPanel mLipstickPanel;
    private BlushPanel mBlushPanel;
    private EyebrowPanel mEyebrowPanel;
    private EyeshadowPanel mEyeshadowPanel;
    private EyelinerPanel mEyelinerPanel;
    private EyelashPanel mEyelashPanel;
    private FacialPanel mFacialPanel;


    public Context getContext(){
        return mContext;
    }

    public SelesParameters getEffect(){
        return mEffect;
    }

    public Beauty getBeautyManager(){
        return mBeautyManager;
    }

    public void setPanelClickListener(BasePanel.OnPanelClickListener listener){
        getLipstickPanel().setOnPanelClickListener(listener);
        getBlushPanel().setOnPanelClickListener(listener);
        getEyebrowPanel().setOnPanelClickListener(listener);
        getEyeshadowPanel().setOnPanelClickListener(listener);
        getEyelinerPanel().setOnPanelClickListener(listener);
        getEyelashPanel().setOnPanelClickListener(listener);
        getFacialPanel().setOnPanelClickListener(listener);
    }

    public void clearAllCosmetic(){
        for (CosmeticTypes.Types type : CosmeticTypes.Types.values()){
            getPanel(type).clear();
        }
    }

}
