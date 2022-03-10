package connectfour

class Player(var name: String, var points: Int = 0)

fun getName(number: Int): String {
    print(if (number == 1) "First" else "Second")
    println(" player's name:")
    return readLine()!!
}

fun getSize(): String {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
    val sizeIn = readLine()!!
    if (sizeIn.isEmpty()) {
        return "6 7"
    }
    val regex = Regex("[0-9]+x[0-9]+")
    var size = ""
    for (ch in sizeIn) {
        if (!Character.isWhitespace(ch)) {
            size += ch
        }
    }
    size = size.lowercase()
    if (!size.matches(regex)) {
        println("Invalid input")
        return getSize()
    }
    val (rows, columns) = size.split("x").map { it.toInt() }
    return if (rows !in 5..9){
        println("Board rows should be from 5 to 9")
        getSize()
    } else if (columns !in 5..9) {
        println("Board columns should be from 5 to 9")
        getSize()
    } else {
        "$rows $columns"
    }
}

fun drawBoard(rows: Int, columns: Int, brd: MutableList<MutableList<Char>>) {
    for (num in 1..columns) {
        print(" $num")
    }
    println()
    for (row in (rows - 1) downTo  0) {
        for (col in 0 until  columns) {
            print("║${brd[col][row]}")
        }
        println("║")
    }
    print("╚")
    for (col in 0 until (columns - 1)) {
        print("═╩")
    }
    println("═╝")
}

fun winCondition(row: Int, col: Int, brd: MutableList<MutableList<Char>>, sym: Char, remain: Int, direction: Int): Boolean {
    if (remain <= 0) return true
    var win = false
    if (brd[col][row] == sym) {
        win = when (direction) {
            0 -> {
                if (remain <= brd[0].size - row) {
                    winCondition(row + 1, col, brd, sym, remain - 1, 0)
                } else false
            }
            1 -> {
                if (remain <= brd.size - col) {
                    winCondition(row, col + 1, brd, sym, remain - 1, 1)
                } else false
            }
            2 -> {
                if (remain <= brd.size - col && remain <= brd[0].size - row) {
                    winCondition(row + 1, col + 1, brd, sym, remain - 1, 2)
                } else false
            }
            3 -> {
                if (remain <= row + 1 && remain <= brd.size - col) {
                    winCondition(row - 1, col + 1, brd, sym, remain - 1, 3)
                } else false
            }
            else -> false
        }
    }
    return win
}

fun game(playerOne: Player, playerTwo: Player, rows: Int, columns: Int, numOfGame: Int, multiGame: Boolean) {
    val fPlayer = playerOne.name
    val sPlayer = playerTwo.name
    var countOfTurns = rows * columns
    var first = true
    var turn: String
    var colTurn: Int
    val boardPosition = MutableList(columns) { MutableList(rows) { ' ' } }
    drawBoard(rows, columns, boardPosition)
    while (true) {
        if (countOfTurns == 0) {
            println("It is a draw")
            if (!multiGame) {
                println("Game over!")
            }
            ++playerOne.points
            ++playerTwo.points
            return
        }
        println("${if (first) fPlayer else sPlayer}'s turn:")
        turn = readLine()!!
        if (turn == "end") {
            println("Game over!")
            return
        }
        if (turn.matches(Regex("[0-9]+"))) {
            if (turn.toInt() in 1..columns) {
                colTurn = turn.toInt() - 1
            } else {
                println("The column number is out of range (1 - $columns)")
                continue
            }
        } else {
            println("Incorrect column number")
            continue
        }
        var rowNum = rows
        do {
            rowNum--
        } while (boardPosition[colTurn][rowNum] == ' ' && rowNum > 0)
        if (boardPosition[colTurn][rowNum] != ' ') rowNum++
        if (rowNum == rows) {
            println("Column ${colTurn + 1} is full")
            continue
        }
        boardPosition[colTurn][rowNum] = if (numOfGame % 2 != 0) {
            if (first) 'o' else '*'
        } else {
            if (!first) 'o' else '*'
        }
        drawBoard(rows, columns, boardPosition)

        val sym = if (numOfGame % 2 != 0) {
            if (first) 'o' else '*'
        } else {
            if (!first) 'o' else '*'
        }

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                if (winCondition(row, col, boardPosition, sym, 4, 0) ||
                    winCondition(row, col, boardPosition, sym, 4, 1) ||
                    winCondition(row, col, boardPosition, sym, 4, 2) ||
                    winCondition(row, col, boardPosition, sym, 4, 3)) {
                    println("Player ${if (first) fPlayer else sPlayer} won")
                    if (first) {
                        playerOne.points += 2
                    } else {
                        playerTwo.points += 2
                    }
                    if (!multiGame) {
                        println("Game over!")
                    }
                    return
                }
            }
        }
        first = !first
        countOfTurns--
    }
}

fun countOfGames(): Int {
    println("Do you want to play single or multiple games?")
    println("For a single game, input 1 or press Enter")
    println("Input a number of games:")
    val count = readLine()!!
    if (count.isEmpty()) {
        return 1
    } else if (count.matches(Regex("[1-9]+"))) {
        if (count.toInt() > 0) {
            return count.toInt()
        }
    }
    println("Invalid input")
    return countOfGames()
}

fun main() {
    println("Connect Four")
    val firstPlayer = Player(getName(1))
    val secondPlayer = Player(getName(2))
    val (rows, columns) = getSize().split(" ").map { it.toInt() }
    val countOfGames: Int = countOfGames()

    println("${firstPlayer.name} VS ${secondPlayer.name}")
    println("$rows X $columns board")
    println(
        if (countOfGames == 1) "Single game"
        else "Total $countOfGames games"
    )
    if (countOfGames == 1) {
        game(firstPlayer, secondPlayer, rows, columns, 1, false)
        return
    }
    for (numOfGame in 1..countOfGames) {
        println("Game #$numOfGame")
        if (numOfGame % 2 == 0) {
            game(secondPlayer, firstPlayer, rows, columns, numOfGame, true)
        } else {
            game(firstPlayer, secondPlayer, rows, columns, numOfGame, true)
        }
        println("Score\n${firstPlayer.name}: ${firstPlayer.points} ${secondPlayer.name}: ${secondPlayer.points}")
    }
    println("Game over!")
}