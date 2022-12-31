package minesweeper

import kotlin.collections.ArrayDeque
import kotlin.random.Random

const val MARKED_SYMBOL = "*"
const val MINE_SYMBOL = "X"
const val EXPLORED = "/"
const val UNEXPLORED_SYMBOL = "."
const val LEVEL_COUNT = 9
val GAME_RANGE = 0 until (LEVEL_COUNT * LEVEL_COUNT)


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
//        printField(true)
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

    private fun printField(showAllMines: Boolean = false) {
        print(" │")
        for (i in 1..LEVEL_COUNT) {
            print("$i".padStart(2,' ').padEnd(3,' '))
        }
        println("│")
        println("—│${"—".repeat(LEVEL_COUNT * 3)}│")
        mineField.forEachIndexed { index, cells ->
            print("${index + 1}|")

            cells.forEach { cell: Cell ->
                val value: String = if (cell.isExplored) {
                    // possible values: unexplored | explored | near mine
                    // change unexplored area to explored
                    // keep everything else the same
                    if (cell.symbol == UNEXPLORED_SYMBOL) EXPLORED else cell.symbol
                } else { // unexplored area: mine | mark
                    when {
                        cell.isMine && showAllMines -> MINE_SYMBOL
                        cell.isMarked -> MARKED_SYMBOL
                        else -> UNEXPLORED_SYMBOL
                    }
                }
                print(value.padStart(2, ' ').padEnd(3, ' '))
            }
            println("|")
        }
        println("—│${"—".repeat(LEVEL_COUNT * 3)}│")
    }

    fun play() {
        printField()
        gameLoop@ while (true) {
            println("Set/unset mines marks or claim a cell as free: ")
            val input = readln().split(' ').map { it }
            val x = input[0].toInt() - 1
            val y = input[1].toInt() - 1
            // chose a cell with a number
            when (input[2]) {
                "free" -> {
                    // if stepped on a mine
                    if (mineField[y][x].isMine) {
                        printField(true)
                        println("You stepped on a mine and failed!")
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
                            println("Congratulations! You found all the mines!")
                            break@gameLoop
                        }
                    }
                }

                "mine" -> {
                    if (!mineField[y][x].isMarked) {
                        mineField[y][x].isMarked = true
                        numberOfMarkers--
                        if (mineField[y][x].isMine) {
                            numberOfMarkedMines++
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
                        println("Congratulations! You found all the mines!")
                        break@gameLoop
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