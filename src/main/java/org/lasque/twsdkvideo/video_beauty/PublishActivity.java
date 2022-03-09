package org.lasque.twsdkvideo.video_beauty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorHomeComponent;
import org.lasque.twsdkvideo.video_beauty.utils.SpUtils;

public class PublishActivity extends ScreenAdapterActivity {
    TuSdkImageView coverImage;
    EditText describeEditText;
    TextView tvPublishCount;
    ImageView imageBack;
    RelativeLayout btnDraft;
    RelativeLayout btnPost;
    TextView tvCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
        initListener();
    }

    private void initView() {
        coverImage  = findViewById(R.id.iv_publish);
        describeEditText = findViewById(R.id.et_publish);
        tvPublishCount = findViewById(R.id.tv_publish_count);
        imageBack = findViewById(R.id.iv_publish_back);
        tvCover = findViewById(R.id.tv_cover);
        btnDraft = findViewById(R.id.btn_draft);
        btnPost = findViewById(R.id.btn_post);
        coverImage.setCornerRadius(TuSdkContext.dip2px(5));
        Glide.with(this).load("http://img2.baidu.com/it/u=98371021,1121096365&fm=253&app=53&size=w500&n=0&g=0n&f=jpeg?sec=1646802813&t=b1774097a3c5e0ae5b3399c16d6d3455").into(coverImage);
    }

    private void initListener() {
        imageBack.setOnClickListener(mOnClickListener);
        btnDraft.setOnClickListener(mOnClickListener);
        btnPost.setOnClickListener(mOnClickListener);
        tvCover.setOnClickListener(mOnClickListener);
        coverImage.setOnClickListener(mOnClickListener);
        describeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              int length = s.toString().length();
              if(length <=200){
                  tvPublishCount.setText(length+"/200");
              }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 点击事件回调
     **/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.iv_publish_back){
                finish();

            }else  if(id == R.id.btn_draft){

            }else  if(id == R.id.btn_post){

            }else  if(id == R.id.iv_publish){

            }else if(id == R.id.tv_cover){

            }

        }


    };

}