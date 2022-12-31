
package minesweeper


fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    val mineField = MineField(numberOfMines)
    mineField.play()
}