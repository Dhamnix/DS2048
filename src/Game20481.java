import java.util.*;

public class Game20481 {
    private static final int BOARD_SIZE = 4;
    private static final int MAX_UNDO_REDO = 5; // حداکثر تعداد Undo/Redo
    private LinkedList<Cell> cells; // نگهداری خانه‌های غیرخالی
    private int score; // امتیاز کل
    private Stack<GameState> undoStack; // پشته برای Undo
    private Stack<GameState> redoStack; // پشته برای Redo


    // کلاس داخلی برای نمایش خانه‌ها
    private static class Cell {
        int row;    // ردیف
        int col;    // ستون
        int value;  // مقدار عددی

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

    public Game20481() {
        cells = new LinkedList<>();
        score = 0; // مقدار اولیه امتیاز
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    private void saveState() {
        if (undoStack.size() == MAX_UNDO_REDO) {
            undoStack.remove(0); // حذف قدیمی‌ترین وضعیت
        }
        undoStack.push(new GameState(cells, score));
        redoStack.clear(); // ریست کردن Redo پس از یک حرکت جدید
    }

    // انجام Undo
    public boolean undo() {
        if (undoStack.isEmpty()) {
            System.out.println("No more Undo available!");
            return false;
        }
        redoStack.push(new GameState(cells, score)); // وضعیت فعلی را برای Redo ذخیره کن
        GameState previousState = undoStack.pop();
        cells = previousState.cells;
        score = previousState.score;
        return true;
    }

    // انجام Redo
    public boolean redo() {
        if (redoStack.isEmpty()) {
            System.out.println("No more Redo available!");
            return false;
        }
        undoStack.push(new GameState(cells, score)); // وضعیت فعلی را برای Undo ذخیره کن
        GameState nextState = redoStack.pop();
        cells = nextState.cells;
        score = nextState.score;
        return true;
    }

    // افزودن یک خانه جدید به صفحه
    public void addCell(int row, int col, int value) {
        cells.add(new Cell(row, col, value));
    }

    // گرفتن خانه در مختصات مشخص
    private Cell getCell(int row, int col) {
        for (Cell cell : cells) {
            if (cell.row == row && cell.col == col) {
                return cell;
            }
        }
        return null; // خانه خالی است
    }

    // ادغام خانه‌ها و به‌روزرسانی امتیاز
    private void mergeCells(Cell source, Cell target) {
        if (source != null && target != null && source.value == target.value) {
            target.value *= 2; // مقدار دو برابر می‌شود
            score += target.value; // اضافه کردن به امتیاز کل
            cells.remove(source); // خانه منبع حذف می‌شود
        }
    }

    // حرکت به سمت بالا
    public void moveUp() {
        saveState(); // ذخیره وضعیت قبل از حرکت
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
                    cell.row = newRow; // موقعیت جدید
                }
            }
        }
    }

    // حرکت به سمت پایین
    public void moveDown() {
        saveState(); // ذخیره وضعیت قبل از حرکت
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
                    cell.row = newRow; // موقعیت جدید
                }
            }
        }
    }

    // حرکت به سمت چپ
    public void moveLeft() {
        saveState(); // ذخیره وضعیت قبل از حرکت
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
                    cell.col = newCol; // موقعیت جدید
                }
            }
        }
    }

    // حرکت به سمت راست
    public void moveRight() {
        saveState(); // ذخیره وضعیت قبل از حرکت
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
                    cell.col = newCol; // موقعیت جدید
                }
            }
        }
    }

    // افزودن یک خانه جدید پس از هر حرکت
    public void addNewTail() {
        saveState(); // ذخیره وضعیت قبل از افزودن خانه جدید
        Random random = new Random();

        // یافتن خانه‌های خالی
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (getCell(row, col) == null) { // خانه خالی است
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        // اگر خانه خالی وجود نداشت، نیازی به افزودن نیست
        if (emptyCells.isEmpty()) {
            return;
        }

        // انتخاب تصادفی یک خانه خالی
        int[] selectedCell = emptyCells.get(random.nextInt(emptyCells.size()));

        // انتخاب تصادفی مقدار 2 یا 4
        int value = random.nextDouble() < 0.7 ? 2 : 4;

        // افزودن خانه جدید به لیست
        addCell(selectedCell[0], selectedCell[1], value);
    }

    // چاپ صفحه و امتیاز
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
                return true; // کاربر برنده شده است
            }
        }
        return false; // هنوز به 2048 نرسیده‌ایم
    }

    // بازی را شروع کن
    public static void main(String[] args) {
        Game2048 game = new Game2048();
        game.addCell(0, 0, 2);
        game.addCell(0, 1, 4);
        game.addCell(0, 2, 2);
        game.addCell(0, 3, 8);
        game.display();

        // انجام حرکت‌ها
        game.moveLeft();
        game.addNewTail();
        game.display();

        // انجام Undo و Redo
        game.undo();
        game.display();
        game.redo();
        game.display();
    }
}
