package OLD

import enums.Category
import enums.Ring
import enums.Stability

fun Set<Technology>.generateRadar(something: String): Radar {
    return Radar(this, something)
}

data class Technology(
    val name: String,
    val description: String,
    val category: Category,
    val ring: Ring,
    val stability: Stability
): Comparable<Technology> {



    override fun toString(): String {
        return this.name + " " + this.description + " " + this.category + " " + this.ring + " " + this.stability + ";"
    }

    override fun compareTo(other: Technology): Int {
        if (this.category.compareTo(other.category) != 0) {
            return this.category.compareTo(other.category)
        }

        if (this.ring.compareTo(other.ring) != 0) {
            return this.ring.compareTo(other.ring)
        }

        if (this.stability.compareTo(other.stability) != 0) {
            return this.stability.compareTo(other.stability)
        }

        return this.name.compareTo(other.name);
    }
}
