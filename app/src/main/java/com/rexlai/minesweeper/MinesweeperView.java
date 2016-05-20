package com.rexlai.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by rexlai on 2016/5/20.
 */
public class MinesweeperView extends View {

    // 遊戲方格大小
    private static final int BOUNDARY_COUNT = 9;
    // 地雷數量
    private static final int LANDMINE_COUNT = 10;
    private static final int LANDMINE = 2;
    private static final int CLICKED = 1;
    private static final int NON_CLICKED = 0;

    private final Paint paint = new Paint();
    // 未點擊方格顯示圖片
    private Bitmap square = null;
    // 地雷圖片
    private Bitmap landmine = null;
    // 點擊後方格顯示圖片
    private Bitmap squareClicked = null;
    // x邊長
    private int xSideLength = 0;
    // y邊長
    private int ySideLength = 0;
    // 方格陣列
    private int[][] grid = null;
    // 地雷所在位置
    private Set<Integer> landMineSet = null;
    // 每格方格在畫面上佔的距離(x軸)
    private int xOffset = 0;
    // 每格方格在畫面上佔的距離(y軸)
    private int yOffset = 0;
    // 儲存遊戲狀態
    private GameStatus status = GameStatus.LIVE;
    // 畫面獲取遊戲狀態listener
    private GameStatusListener listener = null;

    /**
     * 遊戲狀態
     */
    public enum GameStatus {
        OVER, LIVE
    }

    public MinesweeperView(Context context) {
        super(context);
    }

    public MinesweeperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinesweeperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化全部
     */
    private void init() {
        initSquareImage();
        initSquareClickedImage();
        initLandmineImage();
        initLandmine();
        initGrid();
    }

    /**
     * 初始化方格
     */
    private void initGrid() {
        grid = new int[BOUNDARY_COUNT][BOUNDARY_COUNT];
        for (int x = 0; x < BOUNDARY_COUNT; x++) {
            for (int y = 0; y < BOUNDARY_COUNT; y++) {
                grid[x][y] = NON_CLICKED;
            }
        }
    }

    /**
     * 初始化地雷，在所有格數中亂數不重複產生地雷
     */
    private void initLandmine() {
        landMineSet = new HashSet<>();
        Random random = new Random();
        int randomNum;
        for (int i = 0; i < LANDMINE_COUNT; i++) {
            randomNum = random.nextInt(BOUNDARY_COUNT * BOUNDARY_COUNT);
            while (!landMineSet.add(randomNum)) {
                randomNum = random.nextInt(BOUNDARY_COUNT * BOUNDARY_COUNT);
            }
        }
    }

    /**
     * 初始化地雷圖片
     */
    private void initLandmineImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.landmine).copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.postScale((float) (xSideLength / BOUNDARY_COUNT) / bitmap.getWidth(), (float) (ySideLength / BOUNDARY_COUNT) / bitmap.getHeight());
        landmine = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    /**
     * 初始化點擊後方格顯示圖片
     */
    private void initSquareClickedImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.square_clicked).copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.postScale((float) (xSideLength / BOUNDARY_COUNT) / bitmap.getWidth(), (float) (ySideLength / BOUNDARY_COUNT) / bitmap.getHeight());
        squareClicked = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    /**
     * 初始化未點擊方格顯示圖片
     */
    private void initSquareImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.square).copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.postScale((float) (xSideLength / BOUNDARY_COUNT) / bitmap.getWidth(), (float) (ySideLength / BOUNDARY_COUNT) / bitmap.getHeight());
        square = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    /**
     * 系統callback，用來取得畫面長寬，並計算x.y軸每格所佔的距離
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        xSideLength = w;
        ySideLength = h;
        xOffset = xSideLength / BOUNDARY_COUNT;
        yOffset = ySideLength / BOUNDARY_COUNT;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 繪製格子
        for (int x = 0; x < BOUNDARY_COUNT; x++) {
            for (int y = 0; y < BOUNDARY_COUNT; y++) {
                canvas.drawBitmap(getGridImage(grid[x][y]), x * xOffset, y * yOffset, paint);
            }
        }
    }

    /**
     * 用來取得某格子當前圖片
     *
     * @param i 方格陣列當前的值
     * @return 該方格所應呈現的圖
     */
    private Bitmap getGridImage(int i) {
        switch (i) {
            case NON_CLICKED:
                return square;
            case CLICKED:
                return squareClicked;
            case LANDMINE:
                return landmine;
            default:
                return square;
        }
    }

    /**
     * 取得點擊的x, y座標，並換算成點擊的格子座標
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int clickX = (int) Math.floor((double) (event.getX() / xOffset));
        int clickY = (int) Math.floor((double) (event.getY() / yOffset));
        gameControl(clickX, clickY);
        return super.onTouchEvent(event);
    }

    /**
     * 遊戲控制，用來決定是否得分或踩到地雷
     *
     * @param clickX 點擊的x位置
     * @param clickY 點擊的y位置
     */
    private void gameControl(int clickX, int clickY) {
        if (status == GameStatus.LIVE) {
            if (clickLandmine(clickX, clickY)) {
                status = GameStatus.OVER;
                layoutAllLandmine();
                if (listener != null) {
                    listener.gameOver();
                }
            } else {
                if (!clicked(clickX, clickY)) {
                    grid[clickX][clickY] = CLICKED;
                    if (listener != null) {
                        listener.scored();
                    }
                }
            }
        }
        invalidate();
    }

    /**
     * 利用點擊位置判斷方格是否點過
     *
     * @param clickX 點擊的x位置
     * @param clickY 點擊的y位置
     * @return 是否點擊過
     */
    private boolean clicked(int clickX, int clickY) {
        return grid[clickX][clickY] == CLICKED;
    }

    /**
     * 利用點擊位置判斷踩到地雷
     *
     * @param clickX 點擊的x位置
     * @param clickY 點擊的y位置
     * @return 是否踩中地雷
     */
    private boolean clickLandmine(int clickX, int clickY) {
        return landMineSet.contains(clickX * BOUNDARY_COUNT + clickY);
    }

    /**
     * 當踩中地雷時，將全部地雷顯示出來
     */
    private void layoutAllLandmine() {
        for (int landmine : landMineSet) {
            grid[landmine / BOUNDARY_COUNT][landmine % BOUNDARY_COUNT] = LANDMINE;
        }
    }

    /**
     * 開啟新遊戲
     */
    public void newGame() {
        status = GameStatus.LIVE;
        initLandmine();
        initGrid();
        invalidate();
    }

    /**
     * 將要取得遊戲狀態的callback帶入
     *
     * @param listener
     */
    public void setListener(GameStatusListener listener) {
        this.listener = listener;
    }
}
