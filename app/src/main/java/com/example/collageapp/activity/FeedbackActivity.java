package com.example.collageapp.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.example.collageapp.R;
import com.example.collageapp.util.ShareUtil;


public class FeedbackActivity extends BaseActivity {

    public static void startNewInstance(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected int setContentIdRes() {
        return R.id.layout_content;
    }

    @Override
    protected void create() {


        findViewById(R.id.image_view_back).setOnClickListener(v -> finish());
        View sendButton = findViewById(R.id.text_view_send);
        EditText editText = findViewById(R.id.text_view_feedback);

        sendButton.setOnClickListener(v -> {
            String s = editText.getText().toString();
            if (TextUtils.isEmpty(s)) return;
            ShareUtil.sendFeedback(this, s);
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isEmpty = TextUtils.isEmpty(s);
                if (isEmpty) {
                    sendButton.setEnabled(false);
                    sendButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_send_off, null));
                } else {
                    sendButton.setEnabled(true);
                    sendButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_send_on, null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


}