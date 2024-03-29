package com.even.sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.KeyboardUtils;


import com.even.mricheditor.ActionType;
import com.even.mricheditor.RichEditorAction;
import com.even.mricheditor.RichEditorCallback;
import com.even.mricheditor.ui.ActionImageView;
import com.even.sample.fragment.EditHyperlinkFragment;
import com.even.sample.fragment.EditTableFragment;
import com.even.sample.fragment.EditorMenuFragment;
import com.even.sample.interfaces.OnActionPerformListener;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressLint("SetJavaScriptEnabled")
public class RichEditorActivity extends AppCompatActivity {
    FrameLayout flAction;
    LinearLayout llActionBarContainer;
    private ImageView iv_action_insert_link;
    private ImageView iv_get_html,
            iv_action_undo,
            iv_action_redo,
            iv_action,
            iv_action_txt_color,
            iv_action_txt_bg_color,
            iv_action_line_height,
            iv_action_insert_image;

    private boolean isKeyboardShowing;
    private String htmlContent = "<p>Hello World</p>";

    private RichEditorAction mRichEditorAction;
    private RichEditorCallback mRichEditorCallback;

    private EditorMenuFragment mEditorMenuFragment;

    private List<ActionType> mActionTypeList =
            Arrays.asList(ActionType.BOLD, ActionType.ITALIC, ActionType.UNDERLINE,
                    ActionType.STRIKETHROUGH, ActionType.SUBSCRIPT, ActionType.SUPERSCRIPT,
                    ActionType.NORMAL, ActionType.H1, ActionType.H2, ActionType.H3, ActionType.H4,
                    ActionType.H5, ActionType.H6, ActionType.INDENT, ActionType.OUTDENT,
                    ActionType.JUSTIFY_LEFT, ActionType.JUSTIFY_CENTER, ActionType.JUSTIFY_RIGHT,
                    ActionType.JUSTIFY_FULL, ActionType.ORDERED, ActionType.UNORDERED, ActionType.LINE,
                    ActionType.BLOCK_CODE, ActionType.BLOCK_QUOTE, ActionType.CODE_VIEW);

    private List<Integer> mActionTypeIconList =
            Arrays.asList(R.drawable.ic_format_bold,
                    R.drawable.ic_format_italic,
                    R.drawable.ic_format_underlined,
                    R.drawable.ic_format_strikethrough,
                    R.drawable.ic_format_subscript,
                    R.drawable.ic_format_superscript,
                    R.drawable.ic_format_para,
                    R.drawable.ic_format_h1,
                    R.drawable.ic_format_h2,
                    R.drawable.ic_format_h3,
                    R.drawable.ic_format_h4,
                    R.drawable.ic_format_h5,
                    R.drawable.ic_format_h6,
                    R.drawable.ic_format_indent_decrease,
                    R.drawable.ic_format_indent_increase,
                    R.drawable.ic_format_align_left,
                    R.drawable.ic_format_align_center,
                    R.drawable.ic_format_align_right,
                    R.drawable.ic_format_align_justify,
                    R.drawable.ic_format_list_numbered,
                    R.drawable.ic_format_list_bulleted,
                    R.drawable.ic_line,
                    R.drawable.ic_code_block,
                    R.drawable.ic_format_quote,
                    R.drawable.ic_code_review);

    private static final int REQUEST_CODE_CHOOSE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rich_editor);
        llActionBarContainer = findViewById(R.id.ll_action_bar_container);
        flAction = findViewById(R.id.fl_action);
        iv_action_insert_link = findViewById(R.id.iv_action_insert_link);
        iv_get_html = findViewById(R.id.iv_get_html);
        iv_action_undo = findViewById(R.id.iv_action_undo);
        iv_action_redo = findViewById(R.id.iv_action_redo);
        iv_action = findViewById(R.id.iv_action);
        iv_action_txt_color = findViewById(R.id.iv_action_txt_color);
        iv_action_txt_bg_color = findViewById(R.id.iv_action_txt_bg_color);
        iv_action_line_height = findViewById(R.id.iv_action_line_height);
        iv_action_insert_image = findViewById(R.id.iv_action_insert_image);

        initImageLoader();
        initView();

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getResources().getDisplayMetrics());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9,
                getResources().getDisplayMetrics());
        for (int i = 0, size = mActionTypeList.size(); i < size; i++) {
            ActionImageView actionImageView = new ActionImageView(RichEditorActivity.this);
            actionImageView.setLayoutParams(new LinearLayout.LayoutParams(width, width));
            actionImageView.setPadding(padding, padding, padding, padding);
            actionImageView.setActionType(mActionTypeList.get(i));
            actionImageView.setTag(mActionTypeList.get(i));
            actionImageView.setActivatedColor(R.color.blue);
            actionImageView.setDeactivatedColor(R.color.tintColor);
            actionImageView.setRichEditorAction(mRichEditorAction);
            actionImageView.setBackgroundResource(R.drawable.btn_colored_material);
            actionImageView.setImageResource(mActionTypeIconList.get(i));
            actionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionImageView.command();
                }
            });
            llActionBarContainer.addView(actionImageView);
        }

        mEditorMenuFragment = new EditorMenuFragment();
        mEditorMenuFragment.setActionClickListener(new MOnActionPerformListener(mRichEditorAction));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fl_action, mEditorMenuFragment, EditorMenuFragment.class.getName()).commit();
        KeyboardUtils.registerSoftInputChangedListener(this, height -> {
            isKeyboardShowing = height > 0;
            if (height > 0) {
                flAction.setVisibility(View.INVISIBLE);
                ViewGroup.LayoutParams params = flAction.getLayoutParams();
                params.height = height;
                flAction.setLayoutParams(params);
            } else if (flAction.getVisibility() != View.VISIBLE) {
                flAction.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.iv_action_table).setOnClickListener(v -> {
            onClickInsertTable();
        });
        iv_action_insert_link.setOnClickListener(v -> {
            onClickInsertLink();
        });
        // TODO get HTML as text
        iv_get_html.setOnClickListener(v -> {
            onClickGetHtml();
        });
        iv_action_redo.setOnClickListener(v -> {
            onClickRedo();
        });
        iv_action_undo.setOnClickListener(v -> {
            onClickUndo();
        });
        iv_action.setOnClickListener(v -> {
            onClickAction();
        });
        iv_action_txt_color.setOnClickListener(v -> {
            onClickTextColor();
        });
        iv_action_txt_bg_color.setOnClickListener(v -> {
            onClickHighlight();
        });
        iv_action_line_height.setOnClickListener(v -> {
            onClickLineHeight();
        });
        iv_action_insert_image.setOnClickListener(v -> {
            onClickInsertImage();
        });
    }

    /**
     * ImageLoader for insert Image
     */
    private void initImageLoader() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(false);
        imagePicker.setSaveRectangle(true);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(256);
        imagePicker.setOutPutY(256);
    }

    @SuppressLint("AddJavascriptInterface")
    private void initView() {
        WebView webView = findViewById(R.id.wv_container);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webView.setWebChromeClient(new CustomWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        mRichEditorCallback = new MRichEditorCallback();
        webView.addJavascriptInterface(mRichEditorCallback, "MRichEditor");
        webView.loadUrl("file:///android_asset/richEditor.html");
        mRichEditorAction = new RichEditorAction(webView);
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                if (!TextUtils.isEmpty(htmlContent)) {
                    mRichEditorAction.insertHtml(htmlContent);
                }
                KeyboardUtils.showSoftInput(RichEditorActivity.this);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    void onClickAction() {
        if (flAction.getVisibility() == View.VISIBLE) {
            flAction.setVisibility(View.GONE);
        } else {
            if (isKeyboardShowing) {
                KeyboardUtils.hideSoftInput(RichEditorActivity.this);
            }
            flAction.setVisibility(View.VISIBLE);
        }
    }

    private RichEditorCallback.OnGetHtmlListener onGetHtmlListener = html -> {
        if (TextUtils.isEmpty(html)) {
            Toast.makeText(RichEditorActivity.this, "Empty Html String", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Toast.makeText(RichEditorActivity.this, html, Toast.LENGTH_SHORT).show();
    };

    void onClickGetHtml() {
        mRichEditorAction.refreshHtml(mRichEditorCallback, onGetHtmlListener);
    }

    void onClickUndo() {
        mRichEditorAction.undo();
    }

    void onClickRedo() {
        mRichEditorAction.redo();
    }

    void onClickTextColor() {
        mRichEditorAction.foreColor("blue");
    }

    void onClickHighlight() {
        mRichEditorAction.backColor("red");
    }

    void onClickLineHeight() {
        mRichEditorAction.lineHeight(20);
    }

    void onClickInsertImage() {
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS
                && data != null
                && requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<ImageItem> images =
                    (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null && !images.isEmpty()) {

                //1.Insert the Base64 String (Base64.NO_WRAP)
                ImageItem imageItem = images.get(0);
                mRichEditorAction.insertImageData(imageItem.name,
                        encodeFileToBase64Binary(imageItem.path));

                //2.Insert the ImageUrl
                //mRichEditorAction.insertImageUrl(
                //    "https://avatars0.githubusercontent.com/u/5581118?v=4&u=b7ea903e397678b3675e2a15b0b6d0944f6f129e&s=400");
            }
        }
    }

    private static String encodeFileToBase64Binary(String filePath) {
        byte[] bytes = FileIOUtils.readFile2BytesByStream(filePath);
        byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
        return new String(encoded);
    }

    void onClickInsertLink() {
        KeyboardUtils.hideSoftInput(RichEditorActivity.this);
        EditHyperlinkFragment fragment = new EditHyperlinkFragment();
        fragment.setOnHyperlinkListener(
                (address, text) -> mRichEditorAction.createLink(text, address));
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
                .commit();
    }

    void onClickInsertTable() {
        KeyboardUtils.hideSoftInput(RichEditorActivity.this);
        EditTableFragment fragment = new EditTableFragment();
        fragment.setOnTableListener((rows, cols) -> mRichEditorAction.insertTable(rows, cols));
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (flAction.getVisibility() == View.INVISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MRichEditorCallback extends RichEditorCallback {

        @Override
        public void notifyFontStyleChange(ActionType type, final String value) {
            ActionImageView actionImageView =
                    (ActionImageView) llActionBarContainer.findViewWithTag(type);
            if (actionImageView != null) {
                actionImageView.notifyFontStyleChange(type, value);
            }

            if (mEditorMenuFragment != null) {
                mEditorMenuFragment.updateActionStates(type, value);
            }
        }
    }

    public class MOnActionPerformListener implements OnActionPerformListener {
        private RichEditorAction mRichEditorAction;

        public MOnActionPerformListener(RichEditorAction mRichEditorAction) {
            this.mRichEditorAction = mRichEditorAction;
        }

        @Override
        public void onActionPerform(ActionType type, Object... values) {
            if (mRichEditorAction == null) {
                return;
            }

            String value = "";
            if (values != null && values.length > 0) {
                value = (String) values[0];
            }

            switch (type) {
                case SIZE:
                    mRichEditorAction.fontSize(Double.parseDouble(value));
                    break;
                case LINE_HEIGHT:
                    mRichEditorAction.lineHeight(Double.parseDouble(value));
                    break;
                case FORE_COLOR:
                    mRichEditorAction.foreColor(value);
                    break;
                case BACK_COLOR:
                    mRichEditorAction.backColor(value);
                    break;
                case FAMILY:
                    mRichEditorAction.fontName(value);
                    break;
                case IMAGE:
                    onClickInsertImage();
                    break;
                case LINK:
                    onClickInsertLink();
                    break;
                case TABLE:
                    onClickInsertTable();
                    break;
                case BOLD:
                case ITALIC:
                case UNDERLINE:
                case SUBSCRIPT:
                case SUPERSCRIPT:
                case STRIKETHROUGH:
                case JUSTIFY_LEFT:
                case JUSTIFY_CENTER:
                case JUSTIFY_RIGHT:
                case JUSTIFY_FULL:
                case CODE_VIEW:
                case ORDERED:
                case UNORDERED:
                case INDENT:
                case OUTDENT:
                case BLOCK_QUOTE:
                case BLOCK_CODE:
                case NORMAL:
                case H1:
                case H2:
                case H3:
                case H4:
                case H5:
                case H6:
                case LINE:
                    ActionImageView actionImageView = llActionBarContainer.findViewWithTag(type);
                    if (actionImageView != null) {
                        actionImageView.performClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
