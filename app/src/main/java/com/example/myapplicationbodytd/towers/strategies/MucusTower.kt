import com.example.myapplicationbodytd.enemies.Enemy

class MucusTower : Turret(10, SlowEffectAttack(), range = 3) {
    override fun attackEnemy(enemy: Enemy) = strategy.attack(enemy)
    override fun upgrade() {}
}
