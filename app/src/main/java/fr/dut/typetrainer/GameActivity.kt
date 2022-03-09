package fr.dut.typetrainer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.dut.typetrainer.type.TypeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameManager = GameManager()
        val game = gameManager.getGame()
        setContentView(game.getLayout())
        CoroutineScope(Dispatchers.IO).launch() {
            val typeManager = TypeManager()
            while (typeManager.isLoading()) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(100)
                }
            }
            runOnUiThread {
                game.init(this@GameActivity, typeManager)
            }
        }
    }
}