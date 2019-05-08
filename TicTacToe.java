import java.util.Scanner;


public class TicTacToe
{

   public static Scanner in = new Scanner(System.in); // the input Scanner

   public static void main(String[] args) {

      System.out.print("What board size you want ? ");
      int boardSize = in.nextInt();

      Board ticTacToeBoard = new Board(boardSize);

      //Player X and O will use same tictactoe board
      Player playerX = new Player('X', ticTacToeBoard);
      Player playerO = new Player('O', ticTacToeBoard);

      Player activePlayer = playerX;

      // Do the game as long its not draw or one of the player not wining yet
      do {

         ticTacToeBoard.printBoard();
         // user move section
         System.out.print("Player "+activePlayer.getSymbol()+", enter your move (row[1-3] column[1-3]): ");
         int row = in.nextInt() - 1;  // array index starts at 0 instead of 1
         int col = in.nextInt() - 1;

         try {
            activePlayer.makeMove(row, col);
         }
         catch(CellNotExistException e) {
            System.out.println("This move at (" + (row + 1) + "," + (col + 1)
                  + ") is not valid. Cell is not exist. Try again...");
            continue;
         }
         catch(CellNotEmptyException e) {
            System.out.println("This move at (" + (row + 1) + "," + (col + 1)
                  + ") is not valid. Cell is not empty. Try again...");
            continue;
         }

         if(ticTacToeBoard.isDraw()) {
            ticTacToeBoard.printBoard();
            System.out.println("It's a Draw! Bye!");
         }

         if(activePlayer.isWin()) {
            ticTacToeBoard.printBoard();
            System.out.println(activePlayer.getSymbol()+" won! Bye!");
         }

         // change active player
         activePlayer = activePlayer.getSymbol() == playerX.getSymbol() ? playerO : playerX;



      } while (!ticTacToeBoard.isDraw() && !playerX.isWin() && !playerO.isWin());
   }
}

class Player {

   private char symbol;
   private Board board;
   private int recentX;
   private int recentY;

   public Player(char symbol, Board board) {
      this.symbol = symbol;
      this.board = board;
   }

   /**
    * @return the symbol
    */
   public char getSymbol() {
      return symbol;
   }

   public void makeMove(int x, int y) throws CellNotEmptyException, CellNotExistException {
      // im storing user recent move so user win condition can be checked with only user recent move
      this.recentX = x;
      this.recentY = y;


      this.board.playerDrawCell(this, x, y);
   }

   public boolean isWin() {
      // try catch block handling index offset out of limit error
      try {
         return this.board.isWin(this);
      }
      catch (Exception e) {
         return false;
      }
   }

   public int getRecentX() {
      return recentX;
   }

   public int getRecentY() {
      return recentY;
   }
}

class Board {

   // Name-constants to represent the seeds and cell contents
   public static final char EMPTY = ' ';
   
   private int gridSize;
   private char[][] board;

   public Board(int gridSize) {
      this.board = new char[gridSize][gridSize];
      this.gridSize = gridSize;
      this.initGame();
   }

   private void initGame() {
      for (int row = 0; row < this.gridSize; ++row) {
         for (int col = 0; col < this.gridSize; ++col) {
            board[row][col] = EMPTY;  // all cells empty
         }
      }
   }

   public void playerDrawCell(Player player, int x, int y) throws CellNotEmptyException, CellNotExistException {

      if(this.cellIsNotExist(x, y)){
         throw new CellNotExistException();
      }

      if(this.cellIsNotEmpty(x, y)) {
         throw new CellNotEmptyException();
      }

      this.board[x][y] = player.getSymbol();

   }

   private boolean cellIsNotEmpty(int x, int y) {
      return this.board[x][y] != EMPTY;
   }

   private boolean cellIsNotExist(int x, int y) {
      if((x < 0 || x > this.gridSize-1) || (y < 0 || y > this.gridSize-1)) {
         return true;
      }

      return false;
   }

   public boolean isDraw() {
      for (int row = 0; row < this.gridSize; ++row) {
         for (int col = 0; col < this.gridSize; ++col) {
            if (board[row][col] == EMPTY) {
               return false;  // an empty cell found, not draw, exit
            }
         }
      }
      return true;  // no empty cell, it's a draw
   }

   public boolean isWin(Player player) {
      if(this.hasDiagonal1Aligned(player) || 
         this.hasDiagonal2Aligned(player) || 
         this.hasHorizontalAligned(player) || 
         this.hasVerticalAligned(player)) {
            return true;
      }

      return false;
   }

   public boolean hasHorizontalAligned(Player player) {

      int matchCount = 0;
      for (int i = 0; i < this.gridSize; ++i) {
         if(this.board[player.getRecentX()][i] == player.getSymbol()){
            matchCount++;
         }
      }

      if(matchCount == gridSize){
         return true;
      }

      return false;
   }

   public boolean hasVerticalAligned(Player player) {
      int matchCount = 0;
      for (int i = 0; i < this.gridSize; ++i) {
         if(this.board[i][player.getRecentY()] == player.getSymbol()){
            matchCount++;
         }
      }

      if(matchCount == gridSize){
         return true;
      }

      return false;
   }

   public boolean hasDiagonal1Aligned(Player player) {
      int matchCount = 0;
      for (int i = 0; i < this.gridSize; ++i) {
         if(this.board[i][i] == player.getSymbol()){
            matchCount++;
         }
      }

      if(matchCount == gridSize){
         return true;
      }

      return false;
   } 

   public boolean hasDiagonal2Aligned(Player player) {
      int matchCount = 0;
      for (int i = 0; i < this.gridSize; ++i) {
         if(this.board[i][(this.gridSize-1)-i] == player.getSymbol()){
            matchCount++;
         }
      }

      if(matchCount == gridSize){
         return true;
      }

      return false;
   } 

   /** Print the game board */
   public void printBoard() {
      for (int row = 0; row < this.gridSize; ++row) {
         for (int col = 0; col < this.gridSize; ++col) {
            this.printCell(this.board[row][col]); // print each of the cells
            if (col != this.gridSize - 1) {
               System.out.print("|");   // print vertical partition
            }
         }
         System.out.println();
         if (row != this.gridSize - 1) {
            System.out.println(this.buildDynamicPrintRow()); // print horizontal partition
         }
      }
      System.out.println();
   }
   
   private String buildDynamicPrintRow() {
      String string = "";
      for (int i = 0; i < gridSize; i++) {
         string = string+"----";
      }

      return string;
   }

   /** Print a cell with the specified "content" */
   public void printCell(char content) {
      if(content == EMPTY){
         System.out.print("   ");
         return;
      }

      System.out.print(" "+content+" ");

   }
}

class CellNotExistException extends Exception {
   public String toString() {
      return "Selected cell is not exist";
   }
}

class CellNotEmptyException extends Exception {
   public String toString() {
      return "Selected cell is not empty";
   }
}