package com.ufo.lottery.view;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.ufo.lottery.R;

public class SlotView extends AppCompatImageView implements ISlotStopListener {
    private final static String RESULT_1 = "FIRST_RESULT";
    private final static String RESULT_2 = "SECOND_RESULT";
    private final static String RESULT_3 = "THIRD_RESULT";

    private int width;
    private int height;
    public ArrayList<SlotItemView> nodes;
    private int gridSize;
    private int size;
    private int hSize;
    private SlotItemView item1;
    private SlotItemView item2;
    private SlotItemView item3;
    private ArrayList<Integer> slotList1;
    private ArrayList<Integer> slotList2;
    private ArrayList<Integer> slotList3;
    private boolean run;
    private int stopCount;
    private int maxCount;
    private boolean isMax;
    private int result = -1;
    private boolean relese;

    private static int[] res = new int[]{
            R.mipmap.sushi01, R.mipmap.sushi02, R.mipmap.sushi03, R.mipmap.sushi04,
            R.mipmap.sushi05, R.mipmap.sushi06, R.mipmap.sushi07, R.mipmap.sushi08,
            R.mipmap.sushi09, R.mipmap.sushi10, R.mipmap.sushi11, R.mipmap.sushi12,
            R.mipmap.sushi13, R.mipmap.sushi14, R.mipmap.sushi15
    };


    public SlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        nodes = new ArrayList<>();
        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < res.length; i++) {
            slotList.add(res[i]);
        }
        slotList1 = permute(slotList);
        slotList2 = permute(slotList);
        slotList3 = permute(slotList);
    }

    public ArrayList<Integer> permute(ArrayList<Integer> array) {
        ArrayList<Integer> newArray = new ArrayList<>(array);
        Random random = new Random();
        for (int i = 1; i < newArray.size(); i++) {
            newArray = swap(newArray, i, random.nextInt(i));
        }
        return newArray;
    }

    public ArrayList<Integer> swap(ArrayList<Integer> array, int indexA,
                                   int indexB) {
        Integer temp = array.get(indexA);
        array.set(indexA, array.get(indexB));
        array.set(indexB, temp);
        return array;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        gridSize = (int) (width / 3.2f);
        size = (int) (width * 1.0 / 3.0f);
        hSize = (int) (width * 1.0 / 3.1f);
        if (item1 == null) {
            item1 = new SlotItemView(RESULT_1, gridSize, hSize, slotList1,
                    400);
            item1.setPosition(new Point(width / 2 - size, height / 2));
            item1.setListener(this);
            nodes.add(item1);
        }
        if (item2 == null) {
            item2 = new SlotItemView(RESULT_2, gridSize, hSize, slotList2,
                    450);
            item2.setPosition(new Point(width / 2, height / 2));
            item2.setListener(this);
            nodes.add(item2);
        }
        if (item3 == null) {
            item3 = new SlotItemView(RESULT_3, gridSize, hSize, slotList3,
                    500);
            item3.setPosition(new Point(width / 2 + size, height / 2));
            item3.setListener(this);
            nodes.add(item3);
        }
    }

    public void endRun(int result) {
        if (!run) {
            return;
        }
        run = false;
        if (result > -1) {
            this.result = res[result];
        } else {
            this.result = 0;
        }
        slotOver();

    }

    private void slotOver() {
        if (result == 0) {
            int[] noResult = generateNoResult();
            if (isMax) {
                item1.end(slotList1.get(noResult[0]));
                item2.end(slotList2.get(noResult[1]));
                item3.end(slotList3.get(noResult[2]));
            }
        } else {
            if (isMax) {
                item1.end(result);
                item2.end(result);
                item3.end(result);
            }
        }
    }

    public int[] generateNoResult() {
        int[] results = new int[3];
        Random rd = new Random();
        int size1 = slotList1.size();
        int size2 = slotList2.size();
        int size3 = slotList3.size();
        results[0] = rd.nextInt(size1);
        results[1] = rd.nextInt(size2);
        int f2 = slotList1.get((results[0] + 1) % size1);
        int f1 = slotList1.get(results[0]);
        int f0 = slotList1.get((results[0] + size1 - 1) % size1);
        int s2 = slotList2.get((results[1] + 1) % size2);
        int s1 = slotList2.get(results[1]);
        int s0 = slotList2.get((results[1] + size2 - 1) % size2);
        int t2 = 0;
        int t1 = 0;
        int t0 = 0;
        int count = -1;
        do {
            count++;
            results[2] = count;
            t2 = slotList3.get((results[2] + 1) % size3);
            t1 = slotList3.get(results[2]);
            t0 = slotList3.get((results[2] + size3 - 1) % size3);
        } while ((f1 == s1 && f1 == t1)
                || (f0 == s1 && s1 == t2)
                || (f2 == s1 && s1 == t0)
                || (f0 == s0 && f0 == t0)
                || (f2 == s2 && f2 == t2));

        return results;
    }

    @Override
    public void isMax() {
        maxCount++;
        if (maxCount >= 3) {
            isMax = true;
            if (result != -1) {
                slotOver();
            }
        }
    }

    public void startRun() {
        if (run) {
            return;
        }
        result = 0;
        run = true;
        isMax = false;
        stopCount = 0;
        maxCount = 0;
        item1.startSlot();
        item2.startSlot();
        item3.startSlot();
    }

    @Override
    public void end() {
        stopCount++;
        if (stopCount >= 3) {
            if (result != 0) {
                item1.light();
                item2.light();
                item3.light();
            }
        }
    }

    public void relese() {
        relese = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (relese) {
            return;
        }
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            SlotItemView slotItemView = nodes.get(i);
            slotItemView.onDraw(canvas);
        }
        postInvalidate();
    }
}
