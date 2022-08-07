package dataClasses

import enums.Stability

fun Set<Technology>.generateRadar(something: String): Radar {
    return Radar(this, something)
}

data class Technology(
    val name: String,
    val description: String,
    val category:  String,
    val ring: String,
    val stability: Stability
){

    override fun toString(): String {
        return this.name + " " + this.description + " " + this.category + " " + this.ring + " " + this.stability + ";"
    }
}
