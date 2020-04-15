package com.densvr.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

fun CharSequence.withColor(color: Int): SpannableString {
    return SpannableString(this).apply {
        setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}