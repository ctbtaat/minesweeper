package com.rexlai.minesweeper;

/**
 * Created by rexlai on 2016/5/20.
 */
public interface GameStatusListener {

    // 得分
    void scored();

    // 遊戲結束
    void gameOver();
}
