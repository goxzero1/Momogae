package com.example.momogae.Board;

import com.example.momogae.R;

public enum BoardType {
    QUESTION(R.string.heading_question),
    FREE(R.string.heading_recent),
    SHARE(R.string.heading_sharing)
    ;

    private int titleRes;

    BoardType(int title) {
        this.titleRes = title;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public static BoardType findWithIndex(int index) {
        switch (index) {
            case 1: return BoardType.FREE;
            case 2: return BoardType.SHARE;
            default: return BoardType.QUESTION;
        }
    }
}
