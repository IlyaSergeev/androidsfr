package com.densvr.ui.helpers

import android.text.TextWatcher
import android.widget.EditText

fun EditText.setTextWithChangeListener(text: CharSequence, textChangeListener: TextWatcher) {
    removeTextChangedListener(textChangeListener)
    setText(text)
    addTextChangedListener(textChangeListener)
}