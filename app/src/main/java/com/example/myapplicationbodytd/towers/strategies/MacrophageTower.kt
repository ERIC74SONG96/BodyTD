import com.example.myapplicationbodytd.enemies.Enemy
class MacrophageTower : Turret(20, HeavyDamageAttack(), range = 3) {
    override fun attackEnemy(enemy: Enemy) = strategy.attack(enemy)
    override fun upgrade() {}
}