import java.util.Queue
import java.util.LinkedList


data class Transicao(
    val destino: Estado,
    val custo: Int
)

class Estado(val nome: String) {
    val transicoes: MutableList<Transicao> = mutableListOf()

    fun adicionarTransicao(destino: Estado, custo: Int) {
        val transicao = Transicao(destino, custo)
        this.transicoes.add(transicao)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Estado) return false
        return this.nome == other.nome
    }

    override fun hashCode(): Int {
        return nome.hashCode()
    }
}

class Mapa {
    private val estados: MutableMap<String, Estado> = mutableMapOf()

    fun adicionarEstado(estado: Estado) {
        this.estados[estado.nome] = estado
    }

    fun getEstado(nome: String): Estado? {
        return this.estados[nome]
    }

    fun adicionarConexao(nomeOrigem: String, nomeDestino: String, custo: Int) {
        val origem = getEstado(nomeOrigem)
        val destino = getEstado(nomeDestino)

        if (origem != null && destino != null) {
            origem.adicionarTransicao(destino, custo)
            destino.adicionarTransicao(origem, custo)
        }
    }
}


data class No(
    val estado: Estado,
    val pai: No?,
    val transicao: Transicao?,
    val custoDoCaminho: Int
)

/**
 * Implementa o algoritmo de BUSCA-EM-LARGURA (BFS)
 * conforme o pseudocódigo.
 *
 * A Borda é implementada como uma Fila FIFO (First-In, First-Out).
 * Usamos a 'LinkedList' que implementa a interface 'Queue'.
 */
fun buscaEmLargura(mapa: Mapa, nomeInicio: String, nomeObjetivo: String): No? {

    val estadoInicial = mapa.getEstado(nomeInicio) ?: return null
    val estadoObjetivo = mapa.getEstado(nomeObjetivo) ?: return null

    val noInicial = No(
        estado = estadoInicial,
        pai = null,
        transicao = null,
        custoDoCaminho = 0
    )

    if (noInicial.estado == estadoObjetivo) {
        return noInicial
    }

    val borda: Queue<No> = LinkedList<No>()
    borda.add(noInicial)

    val explorados = mutableSetOf<Estado>()

    explorados.add(estadoInicial)

    while (borda.isNotEmpty()) {

        val noAtual = borda.poll()

        for (transicao in noAtual.estado.transicoes) {

            val filhoEstado = transicao.destino

            if (filhoEstado !in explorados) {

                val filhoCusto = noAtual.custoDoCaminho + transicao.custo
                val noFilho = No(
                    estado = filhoEstado,
                    pai = noAtual,
                    transicao = transicao,
                    custoDoCaminho = filhoCusto
                )

                if (filhoEstado == estadoObjetivo) {
                    return noFilho
                }

                explorados.add(filhoEstado)
                borda.add(noFilho)
            }
        }
    }
    return null
}

fun imprimirSolucao(noObjetivo: No, tipoBusca: String) {
    val acoes = mutableListOf<String>()
    val estados = mutableListOf<String>()
    var noAtual: No? = noObjetivo

    while (noAtual != null) {
        estados.add(noAtual.estado.nome)
        noAtual.transicao?.let { trans ->
            val nomePai = noAtual!!.pai!!.estado.nome
            val nomeFilho = trans.destino.nome
            acoes.add("Ir de $nomePai para $nomeFilho (custo ${trans.custo})")
        }
        noAtual = noAtual.pai
    }

    acoes.reverse()
    estados.reverse()

    println("--- Solução Encontrada ($tipoBusca) ---")
    println("Custo Total do Caminho: ${noObjetivo.custoDoCaminho}")
    println("Número de Passos: ${acoes.size}")

    println("\nSequência de Estados (Caminho):")
    println(estados.joinToString(separator = " -> "))

    println("\nSequência de Ações:")
    if (acoes.isEmpty()) {
        println("O estado inicial já é o objetivo.")
    } else {
        acoes.forEach { acao ->
            println("- $acao")
        }
    }
}

/**
 * Função auxiliar para criar e popular o mapa da Romênia.
 */
fun criarMapaDaRomania(): Mapa {
    val mapa = Mapa()
    val nomesCidades = listOf(
        "Arad", "Zerind", "Oradea", "Sibiu", "Timisoara", "Lugoj", "Mehadia",
        "Drobeta", "Craiova", "Rimnicu Vilcea", "Fagaras", "Pitesti",
        "Bucharest", "Giurgiu", "Urziceni", "Neamt", "Iasi", "Vaslui",
        "Hirsova", "Eforie"
    )
    nomesCidades.forEach { nome -> mapa.adicionarEstado(Estado(nome)) }

    // Adiciona as conexões (estradas)
    mapa.adicionarConexao("Arad", "Zerind", 75)
    mapa.adicionarConexao("Arad", "Sibiu", 140)
    mapa.adicionarConexao("Arad", "Timisoara", 118)
    mapa.adicionarConexao("Zerind", "Oradea", 71)
    mapa.adicionarConexao("Oradea", "Sibiu", 151)
    mapa.adicionarConexao("Sibiu", "Fagaras", 99)
    mapa.adicionarConexao("Sibiu", "Rimnicu Vilcea", 80)
    mapa.adicionarConexao("Timisoara", "Lugoj", 111)
    mapa.adicionarConexao("Lugoj", "Mehadia", 70)
    mapa.adicionarConexao("Mehadia", "Drobeta", 75)
    mapa.adicionarConexao("Drobeta", "Craiova", 120)
    mapa.adicionarConexao("Craiova", "Rimnicu Vilcea", 146)
    mapa.adicionarConexao("Craiova", "Pitesti", 138)
    mapa.adicionarConexao("Rimnicu Vilcea", "Pitesti", 97)
    mapa.adicionarConexao("Fagaras", "Bucharest", 211)
    mapa.adicionarConexao("Pitesti", "Bucharest", 101)
    mapa.adicionarConexao("Bucharest", "Giurgiu", 90)
    mapa.adicionarConexao("Bucharest", "Urziceni", 85)
    mapa.adicionarConexao("Urziceni", "Hirsova", 98)
    mapa.adicionarConexao("Urziceni", "Vaslui", 142)
    mapa.adicionarConexao("Hirsova", "Eforie", 86)
    mapa.adicionarConexao("Vaslui", "Iasi", 92)
    mapa.adicionarConexao("Iasi", "Neamt", 87)

    return mapa
}


fun main() {
    val mapa = criarMapaDaRomania()
    val estadoInicial = "Arad"
    val estadoObjetivo = "Bucharest"

    println("Buscando o caminho de $estadoInicial para $estadoObjetivo usando BFS...")

    val noSolucao = buscaEmLargura(mapa, estadoInicial, estadoObjetivo)

    if (noSolucao != null) {
        imprimirSolucao(noSolucao, "Busca em Largura (BFS)")
    } else {
        println("Falha: Não foi possível encontrar um caminho de $estadoInicial para $estadoObjetivo.")
    }
}