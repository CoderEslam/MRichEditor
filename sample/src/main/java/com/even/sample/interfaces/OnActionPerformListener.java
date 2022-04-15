package com.even.sample.interfaces;


import com.even.mricheditor.ActionType;

public interface OnActionPerformListener {
    void onActionPerform(ActionType type, Object... values);
}
