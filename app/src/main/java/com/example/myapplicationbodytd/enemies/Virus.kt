import com.example.myapplicationbodytd.enemies.Enemy
class Virus(
    health: Int = 50,
    speed: Float = 3.0f,
    damage: Int = 5,
    reward: Int = 10
) : Enemy(health, speed, damage, reward) {
    override fun attack() {
        println("Virus attaque rapidement !")
    }

    fun moveFast() {
        println("Virus se déplace à vitesse élevée.")
    }
}
