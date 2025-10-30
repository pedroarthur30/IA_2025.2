import java.util.PriorityQueue

// --- PARTE 1: CLASSES DO LABORATÓRIO ANTERIOR ---

/**
 * Define a classe Transição (Ação).
 * Armazena o estado destino e o custo da aresta.
 */
data class Transicao(
    val destino: Estado,
    val custo: Int
)

/**
 * Define a classe Estado (Cidade).
 * Armazena o nome do estado e sua lista de adjacência (transições).
 */
class Estado(val nome: String) {
    val transicoes: MutableList<Transicao> = mutableListOf()

    fun adicionarTransicao(destino: Estado, custo: Int) {
        val transicao = Transicao(destino, custo)
        this.transicoes.add(transicao)
    }

    // Usado pelo Set de 'explorados' para comparar estados
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Estado) return false
        return this.nome == other.nome
    }

    // Usado pelo Set de 'explorados'
    override fun hashCode(): Int {
        return nome.hashCode()
    }
}

/**
 * Define a classe Mapa (Grafo).
 * Armazena um conjunto de estados (cidades) em um Map.
 */
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
            destino.adicionarTransicao(origem, custo) // Grafo não-direcionado
        }
    }
}

// --- PARTE 2: IMPLEMENTAÇÃO DA BUSCA ---

/**
 * 4.1 Classe No (Nó da Árvore de Busca)
 * Representa um nó da árvore de busca.
 *
 * Implementa 'Comparable' para que a Fila de Prioridade (Borda)
 * possa ordenar os nós pelo 'custoDoCaminho' (menor custo primeiro).
 */
data class No(
    val estado: Estado,
    val pai: No?,
    val transicao: Transicao?, // A transição (ação) que levou a este estado
    val custoDoCaminho: Int  // Custo acumulado g(n)
) : Comparable<No> {

    /**
     * Compara este nó com outro, baseado APENAS no custoDoCaminho.
     * Essencial para a Fila de Prioridade (Busca de Custo Uniforme).
     */
    override fun compareTo(other: No): Int {
        return this.custoDoCaminho.compareTo(other.custoDoCaminho)
    }
}


/**
 * Implementa o algoritmo de Busca de Custo Uniforme (UCS).
 * A Borda é uma Fila de Prioridade e o conjunto de Explorados
 * armazena Estados.
 */
fun buscaCustoUniforme(mapa: Mapa, nomeInicio: String, nomeObjetivo: String): No? {
    // 1. Obter estados inicial e objetivo do mapa
    val estadoInicial = mapa.getEstado(nomeInicio) ?: return null
    val estadoObjetivo = mapa.getEstado(nomeObjetivo) ?: return null

    // 2. Criar o nó inicial (raiz da árvore de busca)
    val noInicial = No(
        estado = estadoInicial,
        pai = null,
        transicao = null,
        custoDoCaminho = 0
    )

    // 4.2 Borda (Frontier)
    // Implementada como Fila de Prioridade, que ordena pelo 'custoDoCaminho'
    val borda = PriorityQueue<No>()
    borda.add(noInicial)

    // 4.3 Explorados
    // Armazena os ESTADOS que já foram expandidos (para evitar ciclos)
    val explorados = mutableSetOf<Estado>()

    // 3. Loop de busca
    while (borda.isNotEmpty()) {

        // Remove o nó com o *menor custo* da borda
        val noAtual = borda.poll()

        // --- Verificação de Ciclo/Repetição ---
        // Se já expandimos este ESTADO antes, pulamos
        // (pois a Fila de Prioridade garante que a primeira
        // vez que expandimos um estado é pelo caminho de menor custo)
        if (noAtual.estado in explorados) {
            continue
        }

        // --- Teste de Objetivo ---
        if (noAtual.estado == estadoObjetivo) {
            return noAtual // Encontrou a solução!
        }

        // --- Expansão ---
        // Marca o estado como expandido
        explorados.add(noAtual.estado)

        // Para cada transição (ação) saindo do estado atual...
        for (transicao in noAtual.estado.transicoes) {
            val filhoEstado = transicao.destino

            // Só adiciona o nó filho na borda se seu estado
            // ainda não foi expandido.
            if (filhoEstado !in explorados) {
                // O custo do filho é o custo do pai + custo da transição
                val filhoCusto = noAtual.custoDoCaminho + transicao.custo

                val noFilho = No(
                    estado = filhoEstado,
                    pai = noAtual,
                    transicao = transicao,
                    custoDoCaminho = filhoCusto
                )
                // Adiciona o novo nó na borda
                borda.add(noFilho)
            }
        }
    }

    return null // Falha: não encontrou solução
}

/**
 * Função auxiliar para imprimir a solução (caminho e custo)
 * a partir do nó objetivo encontrado.
 */
fun imprimirSolucao(noObjetivo: No) {
    val acoes = mutableListOf<String>()
    val estados = mutableListOf<String>()
    var noAtual: No? = noObjetivo

    // 1. Monta o caminho de trás para frente (Objetivo -> Inicial)
    while (noAtual != null) {
        // Adiciona o nome do estado
        estados.add(noAtual.estado.nome)

        // Adiciona a ação (transição) que levou a este nó
        noAtual.transicao?.let { trans ->
            // O 'pai' não será nulo se a 'transicao' não for nula
            val nomePai = noAtual!!.pai!!.estado.nome
            val nomeFilho = trans.destino.nome
            acoes.add("Ir de $nomePai para $nomeFilho (custo ${trans.custo})")
        }

        // Sobe para o nó pai
        noAtual = noAtual.pai
    }

    // 2. Inverte as listas para ter a ordem correta (Inicial -> Objetivo)
    acoes.reverse()
    estados.reverse()

    // 3. Imprime a Saída
    println("--- Solução Encontrada (Busca de Custo Uniforme) ---")
    println("Custo Total do Caminho: ${noObjetivo.custoDoCaminho}")

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


// --- PONTO DE ENTRADA PRINCIPAL ---

fun main() {
    // 1. Configuração do Problema
    val mapa = criarMapaDaRomania()
    val estadoInicial = "Arad"
    val estadoObjetivo = "Bucharest"

    println("Buscando o caminho de menor custo de $estadoInicial para $estadoObjetivo...")

    // 2. Execução da Busca
    val noSolucao = buscaCustoUniforme(mapa, estadoInicial, estadoObjetivo)

    // 3. Apresentação da Saída
    if (noSolucao != null) {
        imprimirSolucao(noSolucao)
    } else {
        println("Falha: Não foi possível encontrar um caminho de $estadoInicial para $estadoObjetivo.")
    }
}