package fr.dut.typetrainer.game

import fr.dut.typetrainer.GameActivity
import fr.dut.typetrainer.type.TypeManager

interface Game {
    fun getLayout(): Int
    fun init(activity: GameActivity, typeManager: TypeManager)
}