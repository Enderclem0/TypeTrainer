package fr.dut.typetrainer.game

import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import fr.dut.typetrainer.GameActivity
import fr.dut.typetrainer.R
import fr.dut.typetrainer.type.TypeManager
import me.sargunvohra.lib.pokekotlin.model.Type
import java.util.*

class GameEfficiency : Game {
    private var typeManager: TypeManager? = null
    private var solution: Double? = null
    private var activity: GameActivity? = null
    private val buttonList: MutableList<Button> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.game_efficiency
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun init(activity: GameActivity, typeManager: TypeManager) {
        this.typeManager = typeManager
        this.activity = activity
        val typeAttack = typeManager.getRandomType()
        val typeDefense = typeManager.getTypeCombination()
        solution = typeManager.getEfficiency(typeAttack, typeDefense[0]!!,typeDefense[1])
        setImage(typeAttack, typeDefense[0]!!, typeDefense[1])
        setButton()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setButton() {
        val buttonNext = activity!!.findViewById<Button>(R.id.button_next)
        buttonNext.setOnClickListener {
            activity!!.recreate()
        }
        buttonList.add(activity!!.findViewById(R.id.efficiency1))
        buttonList.add(activity!!.findViewById(R.id.efficiency2))
        buttonList.add(activity!!.findViewById(R.id.efficiency4))
        buttonList.add(activity!!.findViewById(R.id.efficiency05))
        buttonList.add(activity!!.findViewById(R.id.efficiency025))
        buttonList.add(activity!!.findViewById(R.id.efficiency0))
        buttonList.forEach {
            it.setOnClickListener { revealSolution() }
        }
    }

    private fun setImage(typeAttack: Type, typeDef1: Type, typeDef2: Type? = null) {
        val layout = activity?.findViewById<android.widget.LinearLayout>(R.id.typeDefLayout)
        val imageAttack = activity?.findViewById<android.widget.ImageView>(R.id.typeAtq)
        val imageViewDef1 = android.widget.ImageView(activity)
        val drawableAttackId = activity?.resources?.getIdentifier(
            typeAttack.name.lowercase(Locale.ROOT),
            "drawable",
            activity?.packageName
        )
        val drawableDef1Id = activity?.resources?.getIdentifier(
            typeDef1.name.lowercase(Locale.ROOT),
            "drawable",
            activity?.packageName
        )
        imageAttack?.setImageResource(drawableAttackId!!)
        imageViewDef1.setImageResource(drawableDef1Id!!)
        layout?.addView(imageViewDef1)
        if (typeDef2 != null) {
            val imageViewDef2 = android.widget.ImageView(activity)
            val drawableDef2Id = activity?.resources?.getIdentifier(
                typeDef2.name.lowercase(Locale.ROOT),
                "drawable",
                activity?.packageName
            )
            imageViewDef2.setImageResource(drawableDef2Id!!)
            layout?.addView(imageViewDef2)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun revealSolution() {
        for (button in buttonList) {
            val eff = button.text.toString().dropLast(1).toDouble()
            if (eff == solution) {
                activity?.let { button.setBackgroundColor(it.getColor(R.color.bug)) }
            } else {
                activity?.let { button.setBackgroundColor(it.getColor(R.color.fire)) }
            }
        }
        val buttonNext = activity!!.findViewById<Button>(R.id.button_next)
        buttonNext.visibility = android.view.View.VISIBLE
    }
}