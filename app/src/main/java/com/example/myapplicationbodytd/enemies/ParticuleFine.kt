import com.example.myapplicationbodytd.enemies.Enemy

class FineParticle(
    health: Int = 30,
    speed: Float = 1.0f,
    damage: Int = 2,
    reward: Int = 5
) : Enemy(health, speed, damage, reward) {
    override fun attack() {
        println("Particule fine attaque faiblement.")
    }
}
