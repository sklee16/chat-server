public class TicTacToeGame {

    private static final char PLAYERX = 'X';     // Helper constant for X player- player who starts the match
    private static final char PLAYERO = 'O';     // Helper constant for O player- opponent
    private static final char SPACE = ' ';       // Helper constant for spaces
    private char[][] board = new char[3][3];
    private String opponent;
    private boolean value;
    private boolean playerIndex;
    /*
    Sample TicTacToe Board
      0 | 1 | 2
     -----------
      3 | 4 | 5
     -----------
      6 | 7 | 8
     */

    // TODO 4: Implement necessary methods to manage the games of Tic Tac Toe


    public void TicTacToeGame(String otherPlayer, boolean isX) {
        value = isX;
        if (isX) {
            playerIndex = !value;
            startGame();
        }
        opponent=otherPlayer;
    }

    public void startGame() {
        setBoard();
    }

    public void setBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = SPACE;
            }
        }
    }

    public char[][] getBoard() {
        return board;
    }// end of getbox

    public synchronized int takeTurn(int index) {
        if (index >= 0 && index <= 8) {
            if(value && playerIndex){
                return -1;
            } else if(!value && !playerIndex){
                return -1;
            } else if(value && !playerIndex){
                if(!getSpace(index)){
                    playerIndex = true;
                    return 1;
                }
            } else if(!value && playerIndex){
                if(!getSpace(index)) {
                    playerIndex = false;
                    return 1;
                }
            }
        }
        return 0;
    }

    public void processturn () {
        playerIndex = !value;
    }

    public char getWinner() {
        char winner = 'S';
        for (int i = 0; i < board.length; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == PLAYERO) {
                    winner = PLAYERO;
                    return winner;
                } else if (board[i][0] == PLAYERX) {
                    winner = PLAYERX;
                    return winner;
                }
            } else if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == PLAYERO) {
                    winner = PLAYERO;
                    return winner;
                } else if (board[0][i] == PLAYERX) {
                    winner = PLAYERX;
                    return winner;
                }
            } else if (i == 0 && (board[i][i] == board[i + 1][i + 1] && board[i + 1][i + 1] == board[i + 2][i + 2])) {
                if (board[i][i] == PLAYERO) {
                    winner = PLAYERO;
                    return winner;
                } else if (board[i][i] == PLAYERX) {
                    winner = PLAYERX;
                    return winner;
                }
            } else if (i == 0 && (board[board.length - 1 - i][i] == board[board.length - 2 - i][i + 1]
                    && board[board.length - 2 - i][i + 1] == board[board.length - 3 - i][i + 2])) {
                if (board[board.length - 1 - i][i] == PLAYERO) {
                    winner = PLAYERO;
                    return winner;
                } else if (board[board.length - 1 - i][i] == PLAYERX) {
                    winner = PLAYERX;
                    return winner;
                }
            } else if (!checkSpace()) {
                winner = 'T';
            }
        }

        return winner;
    }

    public char[][] synchronizeboard(char[][] board) {//char[][] board){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                this.board[i][j] = board[i][j];
            }
        }
        return this.board;
    }

    private boolean checkSpace() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == SPACE) { //SPACE
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized boolean getSpace(int index) {
        char temp;
        boolean nomove = true;

        if (value) {
            temp = PLAYERX;
        } else {
            temp = PLAYERO;
        }
        for (int boardrow = 0; boardrow < board.length; boardrow++) {
            if (index % 3 == 0 && index / 3 == boardrow) {
                if (board[boardrow][0] != PLAYERO && board[boardrow][0] != PLAYERX) {
                    board[boardrow][0] = temp;
                    nomove = false;
                }
            } else if ((index - 1) % 3 == 0 && (index - 1) / 3 == boardrow) {
                if (board[boardrow][1] != PLAYERX && board[boardrow][1] != PLAYERO) {
                    board[boardrow][1] = temp;
                    nomove = false;
                }
            } else if ((index - 2) % 3 == 0 && (index - 2) / 3 == boardrow) {
                if (board[boardrow][2] != PLAYERO && board[boardrow][2] != PLAYERX) {
                    board[boardrow][2] = temp;
                    nomove = false;
                }
            }
        }
        return nomove;
    }

    public String getOpponent() {
        return opponent;
    }

    @Override
    public String toString() {
        return getWinner() + " wins";
    }
}