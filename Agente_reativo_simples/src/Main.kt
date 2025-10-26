import java.util.Scanner

/**
 * 4.1 Especificações: Action - Representa as ações possíveis do agente.
 * Usar uma 'enum class' garante segurança de tipos, limitando as ações a um conjunto pré-definido.
 */
enum class Action(val displayName: String) {
    ASPIRAR("Aspirar"),
    DIREITA("Direita"),
    ESQUERDA("Esquerda")
}

/**
 * 4.1 Especificações: Perception - Representa as informações percebidas.
 * Uma 'data class' é ideal aqui, pois o Kotlin gera automaticamente métodos úteis
 * como equals(), hashCode() e toString() para classes que apenas armazenam dados.
 */
data class Perception(val location: String, val isDirty: Boolean)

/**
 * 4.1 Especificações: Environment - Representa o ambiente (mundo do aspirador).
 */
class Environment(var isDirtyA: Boolean, var isDirtyB: Boolean, var agentLocation: String) {
    /**
     * Retorna a percepção atual do agente com base em sua localização.
     */
    fun getPerception(): Perception {
        val isCurrentLocationDirty = if (agentLocation == "A") isDirtyA else isDirtyB
        return Perception(agentLocation, isCurrentLocationDirty)
    }

    /**
     * Atualiza o estado do ambiente com base na ação do agente.
     * A expressão 'when' é a forma idiomática do Kotlin para lidar com múltiplas condições,
     * sendo mais poderosa e legível que um switch-case tradicional.
     */
    fun updateState(action: Action) {
        when (action) {
            Action.ASPIRAR -> {
                if (agentLocation == "A") isDirtyA = false else isDirtyB = false
            }
            Action.DIREITA -> agentLocation = "B"
            Action.ESQUERDA -> agentLocation = "A"
        }
    }
}

/**
 * 4.1 Especificações: Agent - Representa o agente aspirador de pó reativo simples.
 */
class Agent(private val environment: Environment) {

    /**
     * Captura a percepção do ambiente.
     */
    fun perceives(): Perception {
        return environment.getPerception()
    }

    /**
     * Retorna a ação escolhida com base na percepção, implementando a lógica
     * do Agente Reativo Simples definida no pseudocódigo.
     */
    fun act(): Action {
        val perception = perceives()
        // Lógica do pseudocódigo: ASPIRADOR_REATIVO_SIMPLES([posicao, situacao])
        return if (perception.isDirty) {
            Action.ASPIRAR
        } else if (perception.location == "A") {
            Action.DIREITA
        } else { // perception.location == "B"
            Action.ESQUERDA
        }
    }
}

/**
 * 4.1 Especificações: VacuumWorld - Contém a função principal (main) para executar a simulação.
 * Usar 'object' cria uma classe Singleton, um padrão comum em Kotlin para agrupar
 * funções estáticas e pontos de entrada de um programa.
 */
object VacuumWorld {

    /**
     * Função auxiliar para imprimir o estado do ambiente de forma visual.
     */
    private fun printEnvironmentState(env: Environment) {
        val agentA = if (env.agentLocation == "A") "🤖" else " "
        val agentB = if (env.agentLocation == "B") "🤖" else " "
        val dirtA = if (env.isDirtyA) "💩" else "✨"
        val dirtB = if (env.isDirtyB) "💩" else "✨"

        println("[A: $agentA $dirtA]  [B: $agentB $dirtB]")
    }

    /**
     * Ponto de entrada do programa.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)

        // 5. Entrada
        println("--- Configuração Inicial do Ambiente ---")
        print("Posição inicial do Agente (A ou B): ")
        val startLocation = scanner.next().uppercase()

        print("A sala A está suja? (true/false): ")
        val isDirtyA = scanner.nextBoolean()

        print("A sala B está suja? (true/false): ")
        val isDirtyB = scanner.nextBoolean()

        print("Número de passos da simulação: ")
        val steps = scanner.nextInt()
        println("----------------------------------------")

        // Criação das instâncias do ambiente e do agente
        val environment = Environment(isDirtyA, isDirtyB, startLocation)
        val agent = Agent(environment)

        // Loop principal da simulação
        for (i in 1..steps) {
            println("Passo $i")
            // Saída: Situação do ambiente
            printEnvironmentState(environment)

            // Agente percebe o ambiente
            val perception = agent.perceives()
            val dirtStatus = if (perception.isDirty) "💩" else "✨"
            println("Percepção: ${perception.location}, $dirtStatus")

            // Agente decide e executa a ação
            val action = agent.act()
            println("➡️ Ação: ${action.displayName}")

            // Ambiente é atualizado pela ação
            environment.updateState(action)
            println("\n----------------------------------------")
        }
    }
}