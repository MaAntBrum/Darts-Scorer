package com.example.M501;

import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Random;

/** Activity for a two-player 501-style darts game. Keeps the existing activity_game IDs. */
public class GameActivity extends AppCompatActivity {
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private static final int DARTS_PER_TURN = 3;
    private static final int DEFAULT_START_SCORE = 501;

    // Existing layout views, resolved once instead of in every game method.
    private TextView nameP1, nameP2, scoreP1, scoreP2, averageP1, averageP2;
    private TextView legsViewP1, legsViewP2, visitScoreP1, visitScoreP2;
    private TextView dartsViewP1, dartsViewP2;
    private final TextView[][] dartViews = new TextView[2][DARTS_PER_TURN];
    private ImageView turnIndicatorP1, turnIndicatorP2;
    private Button doubleButton, tripleButton;
    private TextToSpeech textToSpeech;

    private int startScore = DEFAULT_START_SCORE;
    private int scoreP1Value = DEFAULT_START_SCORE;
    private int scoreP2Value = DEFAULT_START_SCORE;
    private int dartsP1;
    private int dartsP2;
    private int legsToWin = 3;
    private int legsP1;
    private int legsP2;
    private int legsPlayed;
    private int startingPlayer = PLAYER_ONE;
    private int currentPlayer = PLAYER_ONE;
    // 1 = single out, 2 = double out, 3 = master out.
    private int checkoutMode = 1;
    private boolean doubleSelected;
    private boolean tripleSelected;
    private boolean legDialogOpen;
    private double totalAverageP1;
    private double totalAverageP2;

    private final int[][] visitScores = new int[2][DARTS_PER_TURN];
    private final boolean[][] visitBustMarkers = new boolean[2][DARTS_PER_TURN];
    private final int[] visitStartScores = new int[]{DEFAULT_START_SCORE, DEFAULT_START_SCORE};
    private final Deque<GameSnapshot> undoHistory = new ArrayDeque<>();

    private static final class GameSnapshot {
        final int score1, score2, darts1, darts2, currentPlayer;
        final int[] visit1, visit2;
        final boolean[] bust1, bust2;
        final String[] displayedDarts1, displayedDarts2;
        final int visitStartScore1, visitStartScore2;
        final int visitScore1, visitScore2;
        final boolean doubleSelected, tripleSelected;

        GameSnapshot(GameActivity a) {
            score1 = a.scoreP1Value; score2 = a.scoreP2Value;
            darts1 = a.dartsP1; darts2 = a.dartsP2; currentPlayer = a.currentPlayer;
            visit1 = a.visitScores[0].clone(); visit2 = a.visitScores[1].clone();
            bust1 = a.visitBustMarkers[0].clone(); bust2 = a.visitBustMarkers[1].clone();
            displayedDarts1 = new String[3]; displayedDarts2 = new String[3];
            for (int i = 0; i < 3; i++) {
                displayedDarts1[i] = a.dartViews[0][i].getText().toString();
                displayedDarts2[i] = a.dartViews[1][i].getText().toString();
            }
            visitStartScore1 = a.visitStartScores[0];
            visitStartScore2 = a.visitStartScores[1];
            visitScore1 = parseInt(a.visitScoreP1.getText().toString(), 0);
            visitScore2 = parseInt(a.visitScoreP2.getText().toString(), 0);
            doubleSelected = a.doubleSelected; tripleSelected = a.tripleSelected;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.grey));

        bindViews();
        readSettings();
        initialiseTextToSpeech();
        bindButtons();
        resetLeg(false);
    }

    private void bindViews() {
        nameP1 = findViewById(R.id.NamePlayer1);
        nameP2 = findViewById(R.id.NamePlayer2);
        scoreP1 = findViewById(R.id.Punkte1);
        scoreP2 = findViewById(R.id.Punkte2);
        averageP1 = findViewById(R.id.avg1);
        averageP2 = findViewById(R.id.avg2);
        legsViewP1 = findViewById(R.id.Legs1);
        legsViewP2 = findViewById(R.id.Legs2);
        visitScoreP1 = findViewById(R.id.aufnahme1);
        visitScoreP2 = findViewById(R.id.textView8);
        dartsViewP1 = findViewById(R.id.textView13);
        dartsViewP2 = findViewById(R.id.textView15);
        turnIndicatorP1 = findViewById(R.id.dran1);
        turnIndicatorP2 = findViewById(R.id.dran2);
        dartViews[0][0] = findViewById(R.id.textView11);
        dartViews[0][1] = findViewById(R.id.textView9);
        dartViews[0][2] = findViewById(R.id.textView12);
        dartViews[1][0] = findViewById(R.id.textView14);
        dartViews[1][1] = findViewById(R.id.textView10);
        dartViews[1][2] = findViewById(R.id.textView16);
        doubleButton = findViewById(R.id.btnDouble);
        tripleButton = findViewById(R.id.btnTriple);
    }

    private void readSettings() {
        nameP1.setText(nonEmpty(getIntent().getStringExtra("Spieler1Name"), "Player1"));
        nameP2.setText(nonEmpty(getIntent().getStringExtra("Spieler2Name"), "Player2"));
        startScore = parseInt(getIntent().getStringExtra("Startpunkte"), DEFAULT_START_SCORE);
        if (startScore <= 0) startScore = DEFAULT_START_SCORE;
        legsToWin = parseInt(getIntent().getStringExtra("Legs"), 3);
        if (legsToWin <= 0) legsToWin = 3;
        String checkout = getIntent().getStringExtra("Checkout");
        if ("Double".equals(checkout)) checkoutMode = 2;
        else if ("Master".equals(checkout)) checkoutMode = 3;
        else checkoutMode = 1;

        String starter = getIntent().getStringExtra("Startspieler");
        if ("Spieler1".equals(starter)) startingPlayer = PLAYER_ONE;
        else if ("Spieler2".equals(starter)) startingPlayer = PLAYER_TWO;
        else startingPlayer = new Random().nextBoolean() ? PLAYER_ONE : PLAYER_TWO;
        currentPlayer = startingPlayer;
    }

    private void initialiseTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(Locale.GERMAN);
                textToSpeech.setSpeechRate(3.0f);
            }
        });
    }

    private void bindButtons() {
        doubleButton.setOnClickListener(v -> selectMultiplier(true, false));
        tripleButton.setOnClickListener(v -> selectMultiplier(false, true));
        Button undoButton = findViewById(R.id.btnRueckgaenig);
        undoButton.setOnClickListener(v -> undoLastThrow());

        int[] ids = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
                R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10,
                R.id.btn11, R.id.btn12, R.id.btn13, R.id.btn14, R.id.btn15,
                R.id.btn16, R.id.btn17, R.id.btn18, R.id.btn19, R.id.btn20,
                R.id.btn25, R.id.btnDaneben};
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 20, 25, 0};
        for (int i = 0; i < ids.length; i++) {
            final int value = values[i];
            findViewById(ids[i]).setOnClickListener(v -> recordThrow(value));
        }
        updateMultiplierButtons();
    }

    private void selectMultiplier(boolean isDouble, boolean isTriple) {
        doubleSelected = isDouble;
        tripleSelected = isTriple;
        updateMultiplierButtons();
    }

    private void updateMultiplierButtons() {
        doubleButton.setBackgroundColor(ContextCompat.getColor(this,
                doubleSelected ? R.color.red : R.color.yellow));
        tripleButton.setBackgroundColor(ContextCompat.getColor(this,
                tripleSelected ? R.color.red : R.color.orange));
    }

    private void recordThrow(int faceValue) {
        if (legDialogOpen || faceValue < 0 || faceValue > 25) return;
        int multiplier = (tripleSelected && faceValue != 25) ? 3 : (doubleSelected ? 2 : 1);
        boolean isDouble = doubleSelected;
        boolean isTriple = tripleSelected && faceValue != 25;
        int points = faceValue * multiplier;
        saveSnapshot();
        clearMultiplierSelection();

        int playerIndex = currentPlayer - 1;
        int dartIndex = countRecordedDarts(playerIndex);
        if (dartIndex >= DARTS_PER_TURN) {
            finishTurn();
            playerIndex = currentPlayer - 1;
            dartIndex = 0;
        }
        int oldScore = getScore(currentPlayer);
        int remaining = oldScore - points;
        boolean bust = remaining < 0 || ((checkoutMode == 2 || checkoutMode == 3) && remaining == 1);
        boolean checkout = remaining == 0 && isValidCheckout(isDouble, isTriple);

        if (remaining == 0 && !checkout) bust = true;
        setDartDisplay(playerIndex, dartIndex, bust ? 0 : points, faceValue, multiplier, bust);
        if (currentPlayer == PLAYER_ONE) dartsP1++; else dartsP2++;
        dartsViewP1.setText(String.valueOf(dartsP1));
        dartsViewP2.setText(String.valueOf(dartsP2));
        visitScores[playerIndex][dartIndex] = bust ? 0 : points;
        visitBustMarkers[playerIndex][dartIndex] = bust;

        if (bust) {
            setScore(currentPlayer, visitStartScores[playerIndex]);
            showBustAndFinishTurn(playerIndex);
            return;
        }

        setScore(currentPlayer, remaining);
        updateVisitScore(playerIndex);
        updateAverages();
        if (checkout) {
            showLegWon(currentPlayer);
            return;
        }
        if (dartIndex == DARTS_PER_TURN - 1) finishTurn();
    }

    private boolean isValidCheckout(boolean isDouble, boolean isTriple) {
        if (checkoutMode == 1) return true;
        if (checkoutMode == 2) return isDouble;
        return isDouble || isTriple;
    }

    private void setDartDisplay(int playerIndex, int dartIndex, int points,
                                int faceValue, int multiplier, boolean bust) {
        String text;
        if (bust) text = "0";
        else if (multiplier == 3 && faceValue != 25) text = "T" + faceValue;
        else if (multiplier == 2) text = "D" + faceValue;
        else text = String.valueOf(faceValue);
        dartViews[playerIndex][dartIndex].setText(text);
        dartViews[playerIndex][dartIndex].setBackgroundColor(ContextCompat.getColor(this,
                bust ? R.color.red : R.color.black));
    }

    private void showBustAndFinishTurn(int playerIndex) {
        for (int i = 0; i < DARTS_PER_TURN; i++) {
            visitScores[playerIndex][i] = 0;
            visitBustMarkers[playerIndex][i] = true;

            if (dartViews[playerIndex][i].getText().length() == 0
                    || dartViews[playerIndex][i].getText().toString().equals("—")
                    || dartViews[playerIndex][i].getText().toString().equals("-")) {
                dartViews[playerIndex][i].setText("0");
            }

            dartViews[playerIndex][i].setBackgroundColor(
                    ContextCompat.getColor(this, R.color.red));
        }

        updateScoreViews();
        updateAverages();
        finishTurn();
    }


    private void finishTurn() {
        int completedPlayer = currentPlayer;
        int index = completedPlayer - 1;

        int visitTotal = visitScores[index][0] + visitScores[index][1] + visitScores[index][2];
        TextView visitView = completedPlayer == PLAYER_ONE
                ? visitScoreP1 : visitScoreP2;
        visitView.setText(String.valueOf(visitTotal));
        if (textToSpeech != null) {
            textToSpeech.speak(
                    String.valueOf(visitTotal),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "visit-score"
            );
        }
        // Zum anderen Spieler wechseln.
        currentPlayer = completedPlayer == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;
        clearVisit(currentPlayer);
        visitStartScores[currentPlayer - 1] = getScore(currentPlayer);

        updateTurnIndicators();
        updateAverages();
    }


    private void clearVisit(int player) {
        int index = player - 1;
        for (int i = 0; i < DARTS_PER_TURN; i++) {
            dartViews[index][i].setText("");
            dartViews[index][i].setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            visitScores[index][i] = 0;
            visitBustMarkers[index][i] = false;
        }
        if (player == PLAYER_ONE) visitScoreP1.setText("0"); else visitScoreP2.setText("0");
    }

    private void showLegWon(int winner) {
        legDialogOpen = true;
        updateAverages();
        double avg1 = averageFor(PLAYER_ONE);
        double avg2 = averageFor(PLAYER_TWO);
        String winnerName = winner == PLAYER_ONE ? nameP1.getText().toString() : nameP2.getText().toString();
        new AlertDialog.Builder(this)
                .setTitle(winnerName + " hat das Leg gewonnen!")
                .setMessage(nameP1.getText() + " Avg: " + formatAverage(avg1) + "\n" +
                        nameP2.getText() + " Avg: " + formatAverage(avg2))
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, which) -> {
                    if (winner == PLAYER_ONE) legsP1++; else legsP2++;
                    legsPlayed++;
                    totalAverageP1 += avg1;
                    totalAverageP2 += avg2;
                    legsViewP1.setText(String.valueOf(legsP1));
                    legsViewP2.setText(String.valueOf(legsP2));
                    legDialogOpen = false;
                    if (legsP1 >= legsToWin) showGameWon(PLAYER_ONE);
                    else if (legsP2 >= legsToWin) showGameWon(PLAYER_TWO);
                    else {
                        // Alternate the starting player for each new leg.
                        currentPlayer = ((startingPlayer + legsP1 + legsP2) % 2 == 0)
                                ? PLAYER_TWO : PLAYER_ONE;
                        resetLeg(true);
                    }
                }).show();
    }

    private void showGameWon(int winner) {
        addGameToDB();
        String winnerName = winner == PLAYER_ONE ? nameP1.getText().toString() : nameP2.getText().toString();
        new AlertDialog.Builder(this)
                .setTitle(winnerName + " hat das Spiel gewonnen!")
                .setMessage(nameP1.getText() + " Legs: " + legsP1 + "\n" +
                        nameP2.getText() + " Legs: " + legsP2)
                .setCancelable(false)
                .setPositiveButton("Beenden", (dialog, which) -> finish())
                .show();
    }

    private void addGameToDB() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        float avg1 = legsPlayed == 0 ? 0f : (float) Math.round((totalAverageP1 / legsPlayed) * 100.0) / 100f;
        float avg2 = legsPlayed == 0 ? 0f : (float) Math.round((totalAverageP2 / legsPlayed) * 100.0) / 100f;
        dbh.addGame(nameP1.getText().toString(), nameP2.getText().toString(), legsP1, legsP2, avg1, avg2);
    }

    private void resetLeg(boolean keepLegs) {
        scoreP1Value = startScore;
        scoreP2Value = startScore;
        visitStartScores[0] = startScore;
        visitStartScores[1] = startScore;
        dartsP1 = 0;
        dartsP2 = 0;
        for (int p = 0; p < 2; p++) {
            for (int d = 0; d < DARTS_PER_TURN; d++) {
                visitScores[p][d] = 0;
                visitBustMarkers[p][d] = false;
                dartViews[p][d].setText("");
                dartViews[p][d].setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            }
        }
        visitScoreP1.setText("0"); visitScoreP2.setText("0");
        dartsViewP1.setText("0"); dartsViewP2.setText("0");
        scoreP1.setText(String.valueOf(startScore)); scoreP2.setText(String.valueOf(startScore));
        averageP1.setText("0.00"); averageP2.setText("0.00");
        undoHistory.clear();
        clearMultiplierSelection();
        updateTurnIndicators();
    }

    private void updateTurnIndicators() {
        turnIndicatorP1.setBackgroundColor(ContextCompat.getColor(this,
                currentPlayer == PLAYER_ONE ? R.color.blue : R.color.white));
        turnIndicatorP2.setBackgroundColor(ContextCompat.getColor(this,
                currentPlayer == PLAYER_TWO ? R.color.blue : R.color.white));
    }

    private void setScore(int player, int score) {
        if (player == PLAYER_ONE) scoreP1Value = score; else scoreP2Value = score;
        updateScoreViews();
    }

    private int getScore(int player) {
        return player == PLAYER_ONE ? scoreP1Value : scoreP2Value;
    }

    private void updateScoreViews() {
        scoreP1.setText(String.valueOf(scoreP1Value));
        scoreP2.setText(String.valueOf(scoreP2Value));
    }

    private void updateVisitScore(int playerIndex) {
        int total = visitScores[playerIndex][0] + visitScores[playerIndex][1] + visitScores[playerIndex][2];
        (playerIndex == 0 ? visitScoreP1 : visitScoreP2).setText(String.valueOf(total));
    }

    private int countRecordedDarts(int playerIndex) {
        int count = 0;
        for (int i = 0; i < DARTS_PER_TURN; i++) {
            if (dartViews[playerIndex][i].getText().length() > 0) count++;
        }
        return count;
    }

    private double averageFor(int player) {
        int darts = player == PLAYER_ONE ? dartsP1 : dartsP2;
        int score = getScore(player);
        if (darts <= 0) return 0.0;
        return ((startScore - score) * 3.0) / darts;
    }

    private void updateAverages() {
        averageP1.setText(formatAverage(averageFor(PLAYER_ONE)));
        averageP2.setText(formatAverage(averageFor(PLAYER_TWO)));
    }

    private String formatAverage(double value) {
        return String.format(Locale.GERMANY, "%.2f", value);
    }

    private void clearMultiplierSelection() {
        doubleSelected = false;
        tripleSelected = false;
        updateMultiplierButtons();
    }

    private void saveSnapshot() {
        // The undo stack is bounded so an accidental long session cannot grow without limit.
        if (undoHistory.size() >= 100) undoHistory.removeLast();
        undoHistory.push(new GameSnapshot(this));
    }

    private void undoLastThrow() {
        if (legDialogOpen || undoHistory.isEmpty()) return;
        GameSnapshot snapshot = undoHistory.pop();
        scoreP1Value = snapshot.score1;
        scoreP2Value = snapshot.score2;
        dartsP1 = snapshot.darts1;
        dartsP2 = snapshot.darts2;
        currentPlayer = snapshot.currentPlayer;
        visitStartScores[0] = snapshot.visitStartScore1;
        visitStartScores[1] = snapshot.visitStartScore2;
        System.arraycopy(snapshot.visit1, 0, visitScores[0], 0, DARTS_PER_TURN);
        System.arraycopy(snapshot.visit2, 0, visitScores[1], 0, DARTS_PER_TURN);
        System.arraycopy(snapshot.bust1, 0, visitBustMarkers[0], 0, DARTS_PER_TURN);
        System.arraycopy(snapshot.bust2, 0, visitBustMarkers[1], 0, DARTS_PER_TURN);
        // Snapshot remembers which dart slots were populated. Text is reconstructed from the score arrays.
        for (int p = 0; p < 2; p++) {
            int[] values = visitScores[p];
            for (int d = 0; d < DARTS_PER_TURN; d++) {
                String savedText = p == 0 ? snapshot.displayedDarts1[d] : snapshot.displayedDarts2[d];
                dartViews[p][d].setText(savedText);
                dartViews[p][d].setBackgroundColor(
                        ContextCompat.getColor(
                                this,
                                visitBustMarkers[p][d] ? R.color.red : R.color.black
                        )
                );
            }
        }
        visitScoreP1.setText(String.valueOf(snapshot.visitScore1));
        visitScoreP2.setText(String.valueOf(snapshot.visitScore2));
        doubleSelected = snapshot.doubleSelected;
        tripleSelected = snapshot.tripleSelected;
        dartsViewP1.setText(String.valueOf(dartsP1));
        dartsViewP2.setText(String.valueOf(dartsP2));
        updateScoreViews();
        updateAverages();
        updateTurnIndicators();
        updateMultiplierButtons();
    }

    private static String nonEmpty(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
}
