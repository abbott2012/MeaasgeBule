package com.guoji.mobile.cocobee.utils;

import com.guoji.mobile.cocobee.model.Tag;

import java.util.Comparator;

/**
 * Created by marktrace on 16/11/1.
 */
public class TagComparator implements Comparator<Tag> {
    @Override
    public int compare(Tag tag, Tag t1) {
        return  Math.abs(tag.getRssi()) - Math.abs(t1.getRssi());
    }
}
