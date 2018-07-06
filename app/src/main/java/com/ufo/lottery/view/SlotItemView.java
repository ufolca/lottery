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
    /**
     * 当前位置
     */
    private Point position;
    /**
     * 动画起始时间
     */
    private long startTime;
    /**
     * 保存的15个元素
     */
    private ArrayList<SlotItem> items;
    private int screenWidth;
    /**
     *
     */
    private int screenHeight;
    /**
     *
     */
    private int index;
    /**
     * 是否在转动
     */
    private boolean isSlot;
    /**
     * 是否需要结束
     */
    private boolean isNeedEnd;
    /**
     * 是否已经结束
     */
    private boolean isEnd;
    /**
     * 加速度
     */
    private int speedCount;
    /**
     * 结束时间戳
     */
    private long stopTime;
    /**
     * 转动总距离
     */
    private float distance;
    /**
     * 停止时的距离
     */
    private float stopDistance;
    /**
     * 上次转动后的距离，为了保证再次点击开始时，摇奖机从上次停止的状态继续开始转动
     */
    private float lastDistance;
    /**
     * 当前距离和停止时的距离的差值
     */
    private float needStopDistance;
    private static final float[] SCALES = new float[]{0.7f, 0.8f, 0.9f, 1.0f,
            1.1f, 1.0f, 0.9f, 0.8f};
    private static final int[] STEP = new int[]{60, 50, 41, 33, 26, 20, 15,
            11, 8, 6};
    private ISlotStopListener listener;
    private boolean light;
    private Rect rect = new Rect();

    public SlotItemView(int width, int height,
                        ArrayList<Integer> items, int speedCount) {
        index = -1;
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
            //更新转动距离
            updateDistance();
        }
        int size = items.size();
        for (int i = 0; i < size; i++) {
            drawItem(canvas, i);
        }
    }

    /**
     * 更新转动距离
     */
    private void updateDistance() {
        long currentTimeMillis = System.currentTimeMillis();
        if (isNeedEnd) {
            //快结束情况下根据目标距离和当前距离差值设置减速缓冲
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
                //如果目标位置和图标当前位置重合则停止转动
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

    /**
     * 开始转动
     */
    public void startSlot() {
        isSlot = true;
        isNeedEnd = false;
        isEnd = false;
        light = false;
        index = -1;
        startTime = System.currentTimeMillis();
    }

    /**
     * 以某个图标为目标停止转动
     *
     * @param result
     */
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

    /**
     * 绘制第i个图标
     *
     * @param canvas
     * @param i
     */
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
            //中奖结束后播放缩放动画设置缩放参数
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

    /**
     * 15个图标通过重新修改坐标保证首尾相连
     *
     * @param i
     * @param distance
     * @return
     */
    private float getOffset(int i, float distance) {
        return (i - 14) * screenHeight + ((14 - i) * screenHeight + distance)
                % (15 * screenHeight);
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
