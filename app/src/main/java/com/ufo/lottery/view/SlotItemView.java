package com.ufo.lottery.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

import com.ufo.lottery.R;
import com.ufo.lottery.utils.BmFactory;

import java.util.ArrayList;

public class SlotItemView {
    private static final int SPEED_TIME = 5000;
    public String tag;// 对象唯一标识
    public Point position;// 当前位置
    public long startTime;// 动画起始时间
    protected ArrayList<SlotItem> items;
    protected int screenWidth;
    protected int screenHeight;
    private int index;
    private boolean isSlot;
    private boolean isNeedEnd;
    private boolean isEnd;
    private int speedCount;
    private long stopTime;
    private float distance;
    private float stopDistance;
    private float lastDistance;
    private float needStopDistance;
    private static final float[] SCALES = new float[]{0.7f, 0.8f, 0.9f, 1.0f,
            1.1f, 1.0f, 0.9f, 0.8f};
    private static final int[] STEP = new int[]{60, 50, 41, 33, 26, 20, 15,
            11, 8, 6};
    private ISlotStopListener listener;
    private boolean light;
    private Rect rect = new Rect();

    public SlotItemView(String tag, int width, int height,
                        ArrayList<Integer> items, int speedCount) {
        index = -1;
        this.tag = tag;
        screenWidth = width;
        this.speedCount = speedCount;
        screenHeight = height;
        this.items = new ArrayList<>();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            this.items.add(new SlotItem(items.get(i), i));
        }
    }

    public void setListener(ISlotStopListener listener) {
        this.listener = listener;
    }

    public void onDraw(Canvas canvas) {
        if (isSlot) {
            updateDistance();
        }
        int size = items.size();
        for (int i = 0; i < size; i++) {
            drawItem(canvas, i);
        }
    }

    private void updateDistance() {
        long currentTimeMillis = System.currentTimeMillis();
        if (isNeedEnd) {
            long time = currentTimeMillis - stopTime;
            float stopDistace = 0;
            if (needStopDistance - stopDistance >= 5000) {
                stopDistace = stopDistance + STEP[0];
            } else if (needStopDistance - stopDistance >= 4000) {
                stopDistace = stopDistance + STEP[1];
            } else if (needStopDistance - stopDistance >= 3000) {
                stopDistace = stopDistance + STEP[2];
            } else if (needStopDistance - stopDistance >= 2500) {
                stopDistace = stopDistance + STEP[3];
            } else if (needStopDistance - stopDistance >= 2000) {
                stopDistace = stopDistance + STEP[4];
            } else if (needStopDistance - stopDistance >= 1500) {
                stopDistace = stopDistance + STEP[5];
            } else if (needStopDistance - stopDistance >= 1000) {
                stopDistace = stopDistance + STEP[6];
            } else if (needStopDistance - stopDistance >= 500) {
                stopDistace = stopDistance + STEP[7];
            } else if (needStopDistance - stopDistance >= 200) {
                stopDistace = stopDistance + STEP[8];
            } else {
                stopDistace = stopDistance + STEP[9];
            }
            float lastY = (position.y + (1 - index) * screenHeight + getOffset(
                    index, lastDistance + distance + stopDistance));
            float currentY = (position.y + (1 - index) * screenHeight + getOffset(
                    index, lastDistance + distance + stopDistace));
            if (time >= 1000 && lastY <= position.y && currentY >= position.y) {
                isSlot = false;
                isNeedEnd = false;
                isEnd = true;
                stopDistance = (stopDistace - (currentY - position.y));
                lastDistance = lastDistance + distance + stopDistance;
                distance = 0;
                stopDistance = 0;
                stopTime = 0;
                listener.end();
                return;
            }
            if (stopDistace > stopDistance) {
                stopDistance = stopDistace;
            }
        } else {
            long time = currentTimeMillis - startTime;
            distance = (float) (0.5f * speedCount
                    * Math.pow(Math.min(SPEED_TIME, time), 2) / 1000000.0f);
            if (time >= SPEED_TIME) {
                if (time - 2 * SPEED_TIME >= 0) {
                    listener.isMax();
                }
                distance += (time - SPEED_TIME) * (SPEED_TIME * speedCount)
                        / 1000000.0f;
            }
        }
    }

    public void startSlot() {
        isSlot = true;
        isNeedEnd = false;
        isEnd = false;
        light = false;
        index = -1;
        startTime = System.currentTimeMillis();
    }

    public void end(int result) {
        int size = items.size();
        for (int i = 0; i < size; i++) {
            SlotItem slotItem = items.get(i);
            if (slotItem.key == result) {
                index = i;
                break;
            }
        }
    }

    public void light() {
        light = true;
    }

    private void drawItem(Canvas canvas, int i) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(232, 244, 239));
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(20);
        SlotItem slotItem = items.get(i);
        Bitmap img = slotItem.img;
        float x = position.x;
        float y = (position.y + (1 - i) * screenHeight + getOffset(i,
                lastDistance + distance + stopDistance));
        float bubbleScale = 1.0f;
        if (i == index && !isNeedEnd) {
            isNeedEnd = true;
            stopTime = System.currentTimeMillis();
            needStopDistance = 30 * screenHeight + Math.abs(y - position.y);
        }
        if (i == index && isEnd && light) {
            long now = System.currentTimeMillis();
            long coast = now - startTime;
            int index = (int) Math.abs(coast / 100);
            float s = SCALES[index % SCALES.length];
            bubbleScale = s;
        }
        if (img != null) {
            float imgW = bubbleScale * img.getWidth();
            float imgH = bubbleScale
                    * img.getHeight();
            rect.set((int) (x - imgW / 2), (int) (y - imgH / 2),
                    (int) (x + imgW / 2), (int) (y + imgH / 2));
            canvas.drawBitmap(img, null, rect, paint);
        }
    }

    private float getOffset(int i, float distance) {
        return (i - 14) * screenHeight + ((14 - i) * screenHeight + distance)
                % (15 * screenHeight);
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
