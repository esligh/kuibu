package com.kuibu.custom.widget;

import java.util.regex.Matcher;

import android.text.ParcelableSpan;

public interface SpanCreator {
    ParcelableSpan create(Matcher m);
}
