import com.example.myapplicationbodytd.enemies.Enemy

class HeavyDamageAttack : AttackStrategy {
    override fun attack(target: Enemy) {
        target.takeDamage(10)
    }
}
