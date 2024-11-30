import java.util.*;
public class Game2048 {
    private static final int BOARD_SIZE = 4;
    private static final int MAX_UNDO_REDO = 5;
    private LinkedList<Cell> cells; // نگهداری خانه‌های غیرخالی
    private int score;
    private Stack<GameState> undoStack;
    private Stack<GameState> redoStack;


    private static class Cell {
        int row;
        int col;
        int value;

        Cell(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }
    private static class GameState {
        LinkedList<Cell> cells;
        int score;

        GameState(LinkedList<Cell> cells, int score) {
            this.cells = new LinkedList<>();
            for (Cell cell : cells) {
                this.cells.add(new Cell(cell.row, cell.col, cell.value)); // کپی عمیق
            }
            this.score = score;
        }
    }

    public Game2048() {
        cells = new LinkedList<>();
        score = 0;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }
    private void saveState() {
        if (undoStack.size() == MAX_UNDO_REDO) {
            undoStack.removeFirst(); // حذف قدیمی‌ترین وضعیت برای حفظ اندازه پشته لیترالی خودمم نمیدونم چیجوری ولی خب
        }
        // کپی عمیق از وضعیت فعلی
        undoStack.push(new GameState(cells, score));
        redoStack.clear(); // ریست کردن Redo
    }

    public boolean undo() {
        if (undoStack.isEmpty()) {
            System.out.println("No more Undo available!");
            return false;
        }
        // ذخیره وضعیت فعلی
        redoStack.push(new GameState(cells, score));
        // وضعیت قبلی
        GameState previousState = undoStack.pop();
        cells = previousState.cells;
        score = previousState.score;
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            System.out.println("No more Redo available!");
            return false;
        }
        // ذخیره وضعیت فعلی در Undo
        undoStack.push(new GameState(cells, score));
        // بازیابی وضعیت بعدی
        GameState nextState = redoStack.pop();
        cells = nextState.cells;
        score = nextState.score;
        return true;
    }
    public void addCell(int row, int col, int value) {
        cells.add(new Cell(row, col, value));
    }
    private Cell getCell(int row, int col) {
        for (Cell cell : cells) {
            if (cell.row == row && cell.col == col) {
                return cell;
            }
        }
        return null; // خانه خالی است
    }

    private void mergeCells(Cell source, Cell target) {
        if (source != null && target != null && source.value == target.value) {
            target.value *= 2;
            score += target.value;
            cells.remove(source);
        }
    }

    public void moveUp() {
        saveState();
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 1; row < BOARD_SIZE; row++) {
                Cell cell = getCell(row, col);
                if (cell != null) {
                    int newRow = row;
                    while (newRow > 0 && getCell(newRow - 1, col) == null) {
                        newRow--;
                    }
                    if (newRow > 0) {
                        mergeCells(cell, getCell(newRow - 1, col));
                    }
                    cell.row = newRow;
                }
            }
        }
    }

    public void moveDown() {
        saveState();
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = BOARD_SIZE - 2; row >= 0; row--) {
                Cell cell = getCell(row, col);
                if (cell != null) {
                    int newRow = row;
                    while (newRow < BOARD_SIZE - 1 && getCell(newRow + 1, col) == null) {
                        newRow++;
                    }
                    if (newRow < BOARD_SIZE - 1) {
                        mergeCells(cell, getCell(newRow + 1, col));
                    }
                    cell.row = newRow;
                }
            }
        }
    }

    public void moveLeft() {
        saveState();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 1; col < BOARD_SIZE; col++) {
                Cell cell = getCell(row, col);
                if (cell != null) {
                    int newCol = col;
                    while (newCol > 0 && getCell(row, newCol - 1) == null) {
                        newCol--;
                    }
                    if (newCol > 0) {
                        mergeCells(cell, getCell(row, newCol - 1));
                    }
                    cell.col = newCol;
                }
            }
        }
    }

    public void moveRight() {
        saveState();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = BOARD_SIZE - 2; col >= 0; col--) {
                Cell cell = getCell(row, col);
                if (cell != null) {
                    int newCol = col;
                    while (newCol < BOARD_SIZE - 1 && getCell(row, newCol + 1) == null) {
                        newCol++;
                    }
                    if (newCol < BOARD_SIZE - 1) {
                        mergeCells(cell, getCell(row, newCol + 1));
                    }
                    cell.col = newCol;
                }
            }
        }
    }

    public void addNewTail() {
        Random random = new Random();

        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (getCell(row, col) == null) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (emptyCells.isEmpty()) {
            return;
        }
            // سلکت کردن رندوم
        int[] selectedCell = emptyCells.get(random.nextInt(emptyCells.size()));

        // انتخاب تصادفی مقدار 2 یا 4
        int value = random.nextDouble() < 0.7 ? 2 : 4;

        // افزودن خانه جدید به لیست
        addCell(selectedCell[0], selectedCell[1], value);
    }

    public void printBoard() {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (Cell cell : cells) {
            board[cell.row][cell.col] = cell.value;
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("Score: " + score);
    }
    public void display() {
        printBoard();
    }
    public boolean hasWon() {
        for (Cell cell : cells) {
            if (cell.value == 2048) {
                return true; // برنده شد
            }
        }
        return false; // هنوز به 2048 نرسیده است
    }

    private boolean canMove() {
        // ایا خانه خالی وجود دارد اگه داره ترو ریترن میکنه
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (getCell(row, col) == null) {
                    return true;
                }
            }
        }

        // بررسی امکان مرج خانه‌های مجاور
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Cell current = getCell(row, col);
                if (current != null) {
                    if ((row > 0 && getCell(row - 1, col) != null && getCell(row - 1, col).value == current.value) ||
                            (row < BOARD_SIZE - 1 && getCell(row + 1, col) != null && getCell(row + 1, col).value == current.value) ||
                            (col > 0 && getCell(row, col - 1) != null && getCell(row, col - 1).value == current.value) ||
                            (col < BOARD_SIZE - 1 && getCell(row, col + 1) != null && getCell(row, col + 1).value == current.value)) {
                        return true;
                    }
                }
            }
        }
        return false; // هیچ حرکت ممکنی وجود ندارد
    }

    public boolean isGameOver() {
        return !canMove();
    }
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("Welcome to 2048!");
        System.out.println("Enter 'start' to begin the game.");

        while (true) {
            String command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("start")) {
                initializeGame();
                display();
                break;
            } else {
                System.out.println("Invalid command! Type 'start' to begin the game.");
            }
        }

        while (isRunning) {
            System.out.println("Enter a command (moveUp, moveDown, moveLeft, moveRight, undo, redo, display, quit):");
            String command = scanner.nextLine().trim();

            switch (command.toLowerCase()) {
                case "moveup":
                    moveUp();
                    addNewTail();
                    display();
                    break;

                case "movedown":
                    moveDown();
                    addNewTail();
                    display();
                    break;

                case "moveleft":
                    moveLeft();
                    addNewTail();
                    display();
                    break;

                case "moveright":
                    moveRight();
                    addNewTail();
                    display();
                    break;

                case "undo":
                    if (undo()) {
                        System.out.println("Undo successful.");
                    }
                    display();
                    break;

                case "redo":
                    if (redo()) {
                        System.out.println("Redo successful.");
                    }
                    display();
                    break;

                case "display":
                    display();
                    break;

                case "quit":
                    System.out.println("Thanks for playing 2048! Goodbye.");
                    isRunning = false;
                    break;

                default:
                    System.out.println("Invalid command. Please try again.");
                    break;
            }

            // بررسی وضعیت برنده یا باخت
            if (hasWon()) {
                System.out.println("Congratulations! You've reached 2048! You win!");
                isRunning = false;
            } else if (isGameOver()) {
                System.out.println("No more moves possible. Game over!");
                isRunning = false;
            }
        }
        scanner.close();
    }

    private void initializeGame() {
        cells.clear();
        score = 0;
        undoStack.clear();
        redoStack.clear();

        addNewTail();
        addNewTail();
    }


    public static void main(String[] args) {
        Game2048 game = new Game2048();
        game.startGame();
    }
}
