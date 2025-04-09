import com.example.myapplicationbodytd.enemies.Enemy

class CoughTower(private val pushBackForce: Int = 1) : Turret(10, PushBackAttack(pushBackForce), range = 3) {
    override fun attackEnemy(enemy: Enemy) = strategy.attack(enemy)
    override fun upgrade() {}
}
