package com.ufo.lottery.view;

import android.graphics.Bitmap;

import com.ufo.lottery.utils.BmFactory;

public class SlotItem {
    public int key;
    public int index;
    public Bitmap img;

    public SlotItem(int key, int index) {
        this.key = key;
        this.index = index;
        this.img = BmFactory.getBitmapResource(key);

    }


}
