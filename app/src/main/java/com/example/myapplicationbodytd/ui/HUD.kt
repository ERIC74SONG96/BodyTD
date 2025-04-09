class HUD {
    fun updateDisplay(money: Int, wave: Int, time: Float) {
        displayInfo(money, wave, time)
    }

    private fun displayInfo(money: Int, wave: Int, time: Float) {
        println("Money: $$money | Wave: $wave | Time: ${"%.2f".format(time)}s")
    }
}
