package com.kuibu.module.markdown;

import java.util.regex.Matcher;

import android.text.ParcelableSpan;

public interface SpanCreator {
    ParcelableSpan create(Matcher m);
}
