package com.even.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.even.sample.R;


public class EditHyperlinkFragment extends Fragment {
    EditText etAddress;
    EditText etDisplayText;
    private Button btn_ok;
    private ImageView iv_back;

    private OnHyperlinkListener mOnHyperlinkListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_hyperlink, container, false);
        etAddress = rootView.findViewById(R.id.et_address);
        etDisplayText = rootView.findViewById(R.id.et_display_text);
        btn_ok = rootView.findViewById(R.id.btn_ok);
        iv_back = rootView.findViewById(R.id.iv_back);
        btn_ok.setOnClickListener(v -> {
            onClickOK();
        });
        iv_back.setOnClickListener(v -> {
            onClickBack();
        });
        return rootView;
    }

    void onClickBack() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    void onClickOK() {
        if (mOnHyperlinkListener != null) {
            mOnHyperlinkListener.onHyperlinkOK(etAddress.getText().toString(),
                    etDisplayText.getText().toString());
            onClickBack();
        }
    }

    public void setOnHyperlinkListener(OnHyperlinkListener mOnHyperlinkListener) {
        this.mOnHyperlinkListener = mOnHyperlinkListener;
    }

    public interface OnHyperlinkListener {
        void onHyperlinkOK(String address, String text);
    }
}
