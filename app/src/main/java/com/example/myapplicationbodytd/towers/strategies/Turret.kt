import com.example.myapplicationbodytd.enemies.Enemy

abstract class Turret(
    val cost: Int,
    val strategy: AttackStrategy,
    val range: Int = 3
) {
    abstract fun attackEnemy(enemy: Enemy)
    abstract fun upgrade()
}

