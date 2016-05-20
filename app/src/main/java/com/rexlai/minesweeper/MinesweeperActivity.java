package com.rexlai.minesweeper;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MinesweeperActivity extends AppCompatActivity implements View.OnClickListener, GameStatusListener {

    private MinesweeperView minesweeperView = null;

    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initMinesweeperView();
        initButton();
        initActionBar();
        refreshScore(score);
    }

    /**
     * 初始化MinesweeperView，並帶入遊戲狀態監聽
     */
    private void initMinesweeperView() {
        minesweeperView = (MinesweeperView) findViewById(R.id.mine_sweeper);
        minesweeperView.setListener(this);
    }

    /**
     * 初始化按鈕
     */
    private void initButton() {
        findViewById(R.id.button_new).setOnClickListener(this);
    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * 遊戲結束，顯示分數dialog
     */
    private void showScoreDialog() {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.game_over)
                .setMessage(String.format(getString(R.string.scored), score))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * 刷新分數
     * @param score 分數
     */
    private void refreshScore(int score) {
        ((TextView) findViewById(R.id.score)).setText(String.format(getString(R.string.scored), score));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_new:
                refreshScore(score = 0);
                minesweeperView.newGame();
                break;
        }
    }

    /**
     * 得分後刷新畫面
     */
    @Override
    public void scored() {
        refreshScore(++score);
    }

    /**
     * 遊戲結束顯示分數dialog
     */
    @Override
    public void gameOver() {
        showScoreDialog();
    }
}
