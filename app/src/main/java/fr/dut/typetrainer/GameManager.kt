package fr.dut.typetrainer

import fr.dut.typetrainer.game.Game
import fr.dut.typetrainer.game.GameEfficiency

class GameManager {
    private val availableGames = Array(3) {GameEfficiency :: class.java}
    private var currentGame: Game? = null
    fun getGame(): Game {
        if (currentGame == null) {
            currentGame = availableGames[(Math.random() * availableGames.size).toInt()].newInstance()
        }
        return currentGame!!
    }
}