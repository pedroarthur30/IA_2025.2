
data class Transicao(
    val destino: Estado,
    val custo: Int
)

class Estado(val nome: String) {

    // Lista de adjacência que armazena as transições
    val transicoes: MutableList<Transicao> = mutableListOf()


    fun adicionarTransicao(destino: Estado, custo: Int) {
        val transicao = Transicao(destino, custo)
        this.transicoes.add(transicao)
    }
}

class Mapa {
    // MutableMap para armazenar os estados pelo nome
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
        } else {
            println("Erro: Não foi possível encontrar os estados '$nomeOrigem' ou '$nomeDestino'.")
        }
    }
}

fun main() {

    val mapa = Mapa()


    val nomesCidades = listOf(
        "Arad", "Zerind", "Oradea", "Sibiu", "Timisoara", "Lugoj", "Mehadia",
        "Drobeta", "Craiova", "Rimnicu Vilcea", "Fagaras", "Pitesti",
        "Bucharest", "Giurgiu", "Urziceni", "Neamt", "Iasi", "Vaslui",
        "Hirsova", "Eforie"
    )

    nomesCidades.forEach { nome ->
        mapa.adicionarEstado(Estado(nome))
    }

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

    println("Verificando o mapa...")

    mapa.getEstado("Giurgiu")?.let { estadoSibiu ->
        println("Vizinhos de Sibiu (Estado: ${estadoSibiu.nome}):")

        estadoSibiu.transicoes.forEach { transicao ->
            println("- ${transicao.destino.nome} (Custo: ${transicao.custo})")
        }
    } ?: println("Estado 'Sibiu' não encontrado.")
}