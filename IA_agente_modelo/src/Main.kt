class State(
    val name: String,
    val isDirtyA: Boolean,
    val isDirtyB: Boolean,
    val agentLocation: String,
    val neighbors: MutableMap<String, State> = mutableMapOf()
) {
    override fun toString(): String {
        return "State($name, A_Suja=$isDirtyA, B_Suja=$isDirtyB, Local=$agentLocation)"
    }
}

fun main() {
    val s0 = State("s0", true, true, "A")
    val s1 = State("s1", false, true, "A")
    val s2 = State("s2", false, true, "B")
    val s3 = State("s3", false, false, "B")
    val s4 = State("s4", false, false, "A")
    val s5 = State("s5", true, true, "B")
    val s6 = State("s6", true, false, "B")
    val s7 = State("s7", true, false, "A")

    val allStates = listOf(s0, s1, s2, s3, s4, s5, s6, s7)

    s0.neighbors["aspirar"] = s1
    s0.neighbors["direita"] = s5
    s0.neighbors["esquerda"] = s0

    s1.neighbors["aspirar"] = s1
    s1.neighbors["direita"] = s2
    s1.neighbors["esquerda"] = s1

    s2.neighbors["aspirar"] = s3
    s2.neighbors["direita"] = s2
    s2.neighbors["esquerda"] = s1

    s3.neighbors["aspirar"] = s3
    s3.neighbors["direita"] = s3
    s3.neighbors["esquerda"] = s4

    s4.neighbors["aspirar"] = s4
    s4.neighbors["direita"] = s3
    s4.neighbors["esquerda"] = s4

    s5.neighbors["aspirar"] = s6
    s5.neighbors["direita"] = s5
    s5.neighbors["esquerda"] = s0

    s6.neighbors["aspirar"] = s6
    s6.neighbors["direita"] = s6
    s6.neighbors["esquerda"] = s7

    s7.neighbors["aspirar"] = s4
    s7.neighbors["direita"] = s6
    s7.neighbors["esquerda"] = s7

    println("--- Modelo do Agente Robô Aspirador de Pó ---")
    println("Total de Estados: ${allStates.size}\n")

    allStates.forEach { state ->
        println(state)
        if (state.neighbors.isNotEmpty()) {
            println("  Vizinhos:")
            state.neighbors.forEach { (action, successor) ->
                // Imprime a transição no formato: (ação) -> sucessor
                println("    - Ação '${action}': -> ${successor.name}")
            }
        }
        println()
    }







}