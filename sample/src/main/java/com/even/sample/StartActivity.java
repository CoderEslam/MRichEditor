package com.even.sample;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * @author even
 */
public class StartActivity extends AppCompatActivity {
    private WebView webView;
    private Button btn_rich_editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        webView = findViewById(R.id.wv_start);
        btn_rich_editor = findViewById(R.id.btn_rich_editor);
        webView.loadUrl("https://github.com");
        btn_rich_editor.setOnClickListener(v -> {
            onClickRichEditor();
        });
    }

    void onClickRichEditor() {
        Intent intent = new Intent(this, RichEditorActivity.class);
        startActivity(intent);
    }
}
