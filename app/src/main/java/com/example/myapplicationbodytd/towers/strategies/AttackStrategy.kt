import com.example.myapplicationbodytd.enemies.Enemy

interface AttackStrategy {
    fun attack(target: Enemy)
}
