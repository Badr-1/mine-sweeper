package minesweeper

import com.github.ajalt.mordant.rendering.TextColors.*

fun main() {
    var numberOfMines: Int
    numberOfMines@ while (true) {
        println(cyan("How many mines do you want on the field? "))
        numberOfMines = readln().toInt()
        if (numberOfMines in 1..80) { // number of mines can't exceed number of cells
            break@numberOfMines
        } else {
            println(red("Number of mines can't exceed number of cells"))
        }
    }
    val mineField = MineField(numberOfMines)
    mineField.play()
}