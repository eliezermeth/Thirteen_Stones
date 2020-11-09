package com.mindtedtech.thirteen_stones.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mindtedtech.thirteen_stones.R;
import com.mindtedtech.thirteen_stones.lib.DialogUtils;
import com.mindtedtech.thirteen_stones.models.ThirteenStones;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import static com.mindtedtech.thirteen_stones.lib.DialogUtils.showInfoDialog;
import static com.mindtedtech.thirteen_stones.models.ThirteenStones.getGameFromJSON;
import static com.mindtedtech.thirteen_stones.models.ThirteenStones.getJSONFromGame;

public class MainActivity extends AppCompatActivity {
    private ThirteenStones mGame;
    private TextView mTvStatusBarCurrentPlayer, mTvStatusBarStonesRemaining;
    private ImageView mImageViewStones;
    private Snackbar mSnackBar;

    private int[] mImages;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("GAME", getJSONFromGame(mGame));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame = getGameFromJSON(savedInstanceState.getString("GAME"));
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViews();
        setupImagesIntArray();
        setupFAB();
        startFirstGame();
    }

    private void setupViews() {
        mTvStatusBarCurrentPlayer = findViewById(R.id.tv_status_current_player);
        mTvStatusBarStonesRemaining = findViewById(R.id.tv_status_stones_remaining);
        mSnackBar =
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.welcome),
                        Snackbar.LENGTH_LONG);
        mImageViewStones = findViewById(R.id.image_view_stones);
    }

    private void setupImagesIntArray() {
        mImages = new int[]{R.drawable.stones_00, R.drawable.stones_01, R.drawable.stones_02,
                R.drawable.stones_03, R.drawable.stones_04, R.drawable.stones_05,
                R.drawable.stones_06, R.drawable.stones_07, R.drawable.stones_08,
                R.drawable.stones_09, R.drawable.stones_10, R.drawable.stones_11,
                R.drawable.stones_12, R.drawable.stones_13};
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialog(MainActivity.this,
                        getString(R.string.info_title), mGame.getRules());
            }
        });
    }

    @SuppressWarnings("unused")
    public void pick123(View view) {
        try {
            mGame.takeTurn(Integer.parseInt(((Button) view).getText().toString()));
            updateUI();
            showGameOverMessageIfGameNowOver();
        } catch (IllegalStateException | IllegalArgumentException e) {
            if (e.getMessage() != null) {
                mSnackBar.setText(e.getMessage());
                mSnackBar.show();
            }
        }
    }

    private void dismissSnackBarIfShown() {
        if (mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }

    private void showGameOverMessageIfGameNowOver() {
        if (mGame.isGameOver()) {
            dismissSnackBarIfShown();
            showInfoDialog(this, getString(R.string.game_over),
                    getString(R.string.winner_is_player_number) +
                            mGame.getWinningPlayerNumberIfGameOver() +
                            ". " + getString(
                            R.string.games_played) + " " + mGame.getNumberOfGamesPlayed());
        }
    }

    private void updateUI() {
        dismissSnackBarIfShown();
        mTvStatusBarCurrentPlayer.setText(
                String.format(Locale.getDefault(), "%s: %d",
                        getString(R.string.current_player),
                        mGame.getCurrentPlayerNumber()));
        mTvStatusBarStonesRemaining.setText
                (String.format(Locale.getDefault(), "%s: %d",
                        getString(R.string.stones_remaining_in_pile),
                        mGame.getStonesRemaining()));
        try {
            mImageViewStones.setImageDrawable(
                    ContextCompat.getDrawable(this, mImages[mGame.getStonesRemaining()]));
        } catch (ArrayIndexOutOfBoundsException e) {
            mSnackBar.setText(R.string.error_msg_could_not_update_image);
            mSnackBar.show();
        }
    }

    private void startFirstGame() {
        mGame = new ThirteenStones();
        updateUI();
    }

    private void startNextNewGame() {
        mGame.startGame();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_game: {
                startNextNewGame();
                return true;
            }
            case R.id.action_statistics: {
                showStatistics();
                return true;
            }
            case R.id.action_reset_stats: {
                mGame.resetStatistics();
                return true;
            }
            case R.id.action_settings: {
                showSettings();
                return true;
            }
            case R.id.action_about: {
                showAbout();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void showStatistics() {
        dismissSnackBarIfShown();
        Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
        intent.putExtra("GAME", mGame.getJSONFromCurrentGame());
        startActivity(intent);
    }

    private void showAbout() {
        dismissSnackBarIfShown();
        showInfoDialog(MainActivity.this, "About 13 Stones",
                "A quick two-player game; have fun!\n" +
                        "\nAndroid game by SA.\nmintedtech@gmail.com");
    }

    private void showSettings() {
        dismissSnackBarIfShown();
        //Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        //startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            applySettingsChanges();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void applySettingsChanges() {
        // Not Yet Implemented
    }
}
