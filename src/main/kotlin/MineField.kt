package minesweeper

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import kotlin.collections.ArrayDeque
import kotlin.random.Random

const val MARKED_SYMBOL = "🚩"
const val MINE_SYMBOL = "💣 "
const val EXPLORED = "⬜"
const val UNEXPLORED_SYMBOL = "⬛"
const val LEVEL_COUNT = 9
val GAME_RANGE = 0 until (LEVEL_COUNT * LEVEL_COUNT)
val terminal = Terminal()

class MineField(private val numberOfMines: Int) {

    private var numberOfUnExplored: Int = LEVEL_COUNT * LEVEL_COUNT
    private var numberOfMarkers: Int = numberOfMines + 1
    private var numberOfMarkedMines: Int = 0
    private var mineField = MutableList(LEVEL_COUNT) {
        MutableList(LEVEL_COUNT) { Cell() }
    }

    init {
        putMinesDown()
        addWarnings()
    }

    private fun addWarnings() {
        mineField.forEachIndexed { upperIndex, row ->
            row.forEachIndexed { lowerIndex, cell ->
                val index = upperIndex * LEVEL_COUNT + lowerIndex
                if (cell.isMine) {
                    // update 9x9 with one
                    //             00 01 02 03 04 05 06 07 08
                    //             09 10 11 12 13 14 15 16 17
                    //             18 19 20 21 22 23 24 25 26
                    //             27 28 29 30 31 32 33 34 35
                    //             36 37 38 39 40 41 42 43 44
                    //             45 46 47 48 49 50 51 52 53
                    //             54 55 56 57 58 59 60 61 62
                    //             63 64 65 66 67 68 69 70 71
                    //             72 73 74 75 76 77 78 79 80
                    // formula for getting index of 8 surrounding elements
                    // get the top element via top = index - 9
                    // get the bottom element via bottom = index + 9
                    val bottom = index + LEVEL_COUNT
                    val top = index - LEVEL_COUNT

                    // right
                    if ((index + 1) % LEVEL_COUNT != 0) { // checks if the element isn't the last from the right
                        addMark(index + 1)
                    }
                    // left
                    if (index % LEVEL_COUNT != 0) { // checks if the element isn't the last from the left
                        addMark(index - 1)
                    }
                    // top
                    if (top in GAME_RANGE) {
                        addMark(top)
                        // right
                        if ((top + 1) % LEVEL_COUNT != 0) { // checks if the element isn't the last from the right
                            addMark(top + 1)
                        }
                        // left
                        if (top % LEVEL_COUNT != 0) { // checks if the element isn't the last from the left
                            addMark(top - 1)
                        }
                    }
                    if (bottom in GAME_RANGE) {
                        addMark(bottom)
                        // right
                        if ((bottom + 1) % LEVEL_COUNT != 0) { // checks if the element isn't the last from the right
                            addMark(bottom + 1)
                        }
                        // left
                        if (bottom % LEVEL_COUNT != 0) { // checks if the element isn't the last from the left
                            addMark(bottom - 1)
                        }
                    }
                }
            }
        }
    }

    private fun addMark(index: Int) {
        if (!mineField.cell(index).isMine) {
            mineField.cell(index).symbol =
                if (mineField.cell(index).symbol == UNEXPLORED_SYMBOL) "1" else (mineField.cell(index).symbol.toInt() + 1).toString()
        }
    }

    private fun MutableList<MutableList<Cell>>.cell(index: Int): Cell {
        val row = index % LEVEL_COUNT
        val column = index / LEVEL_COUNT
        return this[column][row]
    }

    private fun putMinesDown() {
        var numberOfMines = this.numberOfMines
        while (numberOfMines != 0) {
            val index = Random.nextInt(0, LEVEL_COUNT * LEVEL_COUNT)
            if (!mineField.cell(index).isMine) {
                mineField.cell(index).isMine = true
                numberOfMines--
            }
        }
    }

    private fun Cell.display(showAllMines: Boolean) = if (this.isExplored) {
        // possible values: unexplored | explored | near mine
        // change unexplored area to explored
        // keep everything else the same
        if (this.symbol == UNEXPLORED_SYMBOL) EXPLORED else this.symbol

    } else { // unexplored area: mine | mark
        when {
            this.isMine && showAllMines -> MINE_SYMBOL
            this.isMarked -> MARKED_SYMBOL
            else -> UNEXPLORED_SYMBOL
        }
    }


    private fun printField(showAllMines: Boolean = false) {

        val header = mutableListOf<String>()
        for (i in 1..LEVEL_COUNT) {
            header.add(i.toString())
        }
        terminal.println(
            table {
                header { row("➖", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣") }
                body {
                    row(
                        "1️⃣",
                        mineField[0][0].display(showAllMines),
                        mineField[0][1].display(showAllMines),
                        mineField[0][2].display(showAllMines),
                        mineField[0][3].display(showAllMines),
                        mineField[0][4].display(showAllMines),
                        mineField[0][5].display(showAllMines),
                        mineField[0][6].display(showAllMines),
                        mineField[0][7].display(showAllMines),
                        mineField[0][8].display(showAllMines),
                    )
                    row(
                        "2️⃣",
                        mineField[1][0].display(showAllMines),
                        mineField[1][1].display(showAllMines),
                        mineField[1][2].display(showAllMines),
                        mineField[1][3].display(showAllMines),
                        mineField[1][4].display(showAllMines),
                        mineField[1][5].display(showAllMines),
                        mineField[1][6].display(showAllMines),
                        mineField[1][7].display(showAllMines),
                        mineField[1][8].display(showAllMines),
                    )

                    row(
                        "3️⃣",
                        mineField[2][0].display(showAllMines),
                        mineField[2][1].display(showAllMines),
                        mineField[2][2].display(showAllMines),
                        mineField[2][3].display(showAllMines),
                        mineField[2][4].display(showAllMines),
                        mineField[2][5].display(showAllMines),
                        mineField[2][6].display(showAllMines),
                        mineField[2][7].display(showAllMines),
                        mineField[2][8].display(showAllMines),
                    )

                    row(
                        "4️⃣",
                        mineField[3][0].display(showAllMines),
                        mineField[3][1].display(showAllMines),
                        mineField[3][2].display(showAllMines),
                        mineField[3][3].display(showAllMines),
                        mineField[3][4].display(showAllMines),
                        mineField[3][5].display(showAllMines),
                        mineField[3][6].display(showAllMines),
                        mineField[3][7].display(showAllMines),
                        mineField[3][8].display(showAllMines),
                    )

                    row(
                        "5️⃣",
                        mineField[4][0].display(showAllMines),
                        mineField[4][1].display(showAllMines),
                        mineField[4][2].display(showAllMines),
                        mineField[4][3].display(showAllMines),
                        mineField[4][4].display(showAllMines),
                        mineField[4][5].display(showAllMines),
                        mineField[4][6].display(showAllMines),
                        mineField[4][7].display(showAllMines),
                        mineField[4][8].display(showAllMines),
                    )

                    row(
                        "6️⃣",
                        mineField[5][0].display(showAllMines),
                        mineField[5][1].display(showAllMines),
                        mineField[5][2].display(showAllMines),
                        mineField[5][3].display(showAllMines),
                        mineField[5][4].display(showAllMines),
                        mineField[5][5].display(showAllMines),
                        mineField[5][6].display(showAllMines),
                        mineField[5][7].display(showAllMines),
                        mineField[5][8].display(showAllMines),
                    )

                    row(
                        "7️⃣",
                        mineField[6][0].display(showAllMines),
                        mineField[6][1].display(showAllMines),
                        mineField[6][2].display(showAllMines),
                        mineField[6][3].display(showAllMines),
                        mineField[6][4].display(showAllMines),
                        mineField[6][5].display(showAllMines),
                        mineField[6][6].display(showAllMines),
                        mineField[6][7].display(showAllMines),
                        mineField[6][8].display(showAllMines),
                    )

                    row(
                        "8️⃣",
                        mineField[7][0].display(showAllMines),
                        mineField[7][1].display(showAllMines),
                        mineField[7][2].display(showAllMines),
                        mineField[7][3].display(showAllMines),
                        mineField[7][4].display(showAllMines),
                        mineField[7][5].display(showAllMines),
                        mineField[7][6].display(showAllMines),
                        mineField[7][7].display(showAllMines),
                        mineField[7][8].display(showAllMines),
                    )
                    row(
                        "9️⃣",
                        mineField[8][0].display(showAllMines),
                        mineField[8][1].display(showAllMines),
                        mineField[8][2].display(showAllMines),
                        mineField[8][3].display(showAllMines),
                        mineField[8][4].display(showAllMines),
                        mineField[8][5].display(showAllMines),
                        mineField[8][6].display(showAllMines),
                        mineField[8][7].display(showAllMines),
                        mineField[8][8].display(showAllMines),
                    )
                }
            }
        )

    }

    fun play() {
        printField()
        gameLoop@ while (true) {
            var input: String
            var x: Int
            var y: Int
            var action: String
            action@ while (true) {
                println(cyan("Set/unset mines marks or claim a cell as free (flags remaining $numberOfMarkers 🚩): "))
                input = readln()
                if (input.matches(Regex("([1-9] [1-9] (mine|free))"))) {

                    x = input.split(' ').first().toInt() - 1
                    y = input.split(' ')[1].toInt() - 1
                    action = input.split(' ').last()

                    break@action
                } else {
                    println(red("invalid input: x y mine/free, x and y are integers between 1 and 9"))
                }
            }
            // chose a cell with a number
            when (action) {
                "free" -> {
                    // if stepped on a mine
                    if (mineField[y][x].isMine) {
                        printField(true)
                        println(red("You stepped on a mine and failed!"))
                        break@gameLoop
                    } else {
                        val toBeExplored = ArrayDeque<Int>()
                        val start = y * LEVEL_COUNT + x
                        toBeExplored.addLast(start)
                        while (toBeExplored.isNotEmpty()) {
                            val index = toBeExplored.removeFirst()
                            if (mineField.cell(index).isExplored) {
                                continue
                            }
                            // mark as explored
                            mineField.cell(index).isExplored = true
                            numberOfUnExplored--
                            if (mineField.cell(index).isMarked) {
                                numberOfMarkers++
                                mineField.cell(index).isMarked = false
                            }
                            if (mineField.cell(index).symbol[0].isDigit()) { // if near mine explore it only
                                continue
                            }
                            // else add its neighbours


                            val bottom = index + LEVEL_COUNT
                            val top = index - LEVEL_COUNT
                            // right
                            if ((index + 1) % LEVEL_COUNT != 0) { // checks if the element isn't the last from the right
                                toBeExplored.addToQueue(index + 1)
                            }
                            // left
                            if (index % LEVEL_COUNT != 0) { // checks if the element isn't the last from the left
                                toBeExplored.addToQueue(index - 1)
                            }
                            // top
                            if (top in GAME_RANGE) {
                                toBeExplored.addToQueue(top)
                                // right
                                if ((top + 1) % LEVEL_COUNT != 0) { // checks if the element isn't the last from the right
                                    toBeExplored.addToQueue(top + 1)
                                }
                                // left
                                if (top % LEVEL_COUNT != 0) { // checks if the element isn't the last from the left
                                    toBeExplored.addToQueue(top - 1)
                                }
                            }
                            if (bottom in GAME_RANGE) {
                                toBeExplored.addToQueue(bottom)
                                // right
                                if ((bottom + 1) % LEVEL_COUNT != 0) { // checks if the element isn't the last from the right
                                    toBeExplored.addToQueue(bottom + 1)
                                }
                                // left
                                if (bottom % LEVEL_COUNT != 0) { // checks if the element isn't the last from the left
                                    toBeExplored.addToQueue(bottom - 1)
                                }
                            }
                        }
                        printField()
                        if (numberOfUnExplored == numberOfMines) {
                            println(green("Congratulations! You found all the mines!"))
                            break@gameLoop
                        }
                    }
                }

                "mine" -> {
                    if (!mineField[y][x].isExplored) {

                        if (!mineField[y][x].isMarked) {
                            if (numberOfMarkers != 0) {
                                mineField[y][x].isMarked = true
                                numberOfMarkers--
                                if (mineField[y][x].isMine) {
                                    numberOfMarkedMines++
                                }
                            } else {
                                println(red("You don't have any flags"))
                            }
                        } else {
                            mineField[y][x].isMarked = false
                            numberOfMarkers++
                            if (mineField[y][x].isMine) {
                                numberOfMarkedMines--
                            }
                        }
                        printField()
                        if (numberOfMarkedMines == numberOfMines && numberOfMarkers == 1) {
                            println(green("Congratulations! You found all the mines!"))
                            break@gameLoop
                        }
                    }

                }

            }
        }
    }

    private fun ArrayDeque<Int>.addToQueue(index: Int) {
        val cell = mineField.cell(index)
        if (!cell.isExplored && !cell.isMine) {
            this.addLast(index)
        }
    }

    class Cell(
        var symbol: String = UNEXPLORED_SYMBOL,
        var isMine: Boolean = false,
        var isExplored: Boolean = false,
        var isMarked: Boolean = false
    )
}