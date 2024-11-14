import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ProblemaP2{  
    //Main donde se arma y crea el grafo 
    public static void main(String[] args) {
        Scanner scann = new Scanner(System.in); 
        int numCases = Integer.parseInt(scann.nextLine()); 
        for (int i = 0 ; i < numCases ; i++){ 
            String[] lineCase = scann.nextLine().split(" "); 
            int numFilas = Integer.parseInt(lineCase[0]); 
            int dist = Integer.parseInt(lineCase[1]); 

            HashMap <Integer,List<Integer>> mapaCoor = new HashMap<>();
            List<Integer> nodos = new ArrayList<>();
            HashMap <Integer,List<String>> mapa = new HashMap<>(); 
            
            for (int j = 0 ; j < numFilas ; j++){  
                List<Integer> info = new ArrayList<>();
                String [] datos = scann.nextLine().split(" "); 
                    
                int identificador = Integer.parseInt(datos[0]); 
                int coodrX = Integer.parseInt(datos[1]); 
                int coordY = Integer.parseInt(datos[2]); 
                int tipo = Integer.parseInt(datos[3]);  
                
                //agregamos los identificadors a la lista de nodos 
                if(!nodos.contains(identificador)){ 
                        nodos.add(identificador);
                }
                
                //Vamos agregando los nodos al mapa con la informacion  
                if(!mapaCoor.containsKey(identificador)){ 
                    info.add(coodrX); 
                    info.add(coordY); 
                    info.add(tipo); 
                    mapaCoor.put(identificador, info);
                }  

                //Ahora proseguimos con las letras 
                List<String> peptidos = new ArrayList<>();  
                for (int k = 4 ; k < datos.length; k ++){ 
                    peptidos.add(datos[k]); 
                } 
                mapa.put(identificador, peptidos);
            }  

            int [][] grafo = grafo(mapaCoor, mapa, dist, nodos); 
            System.out.println(Arrays.deepToString(grafo));
            int maxF = flujoMaximo(grafo, 0, grafo.length - 1);

            // Bloqueo de cada célula calculadora
            int maxReduccion = 0;
            int nodoABloquear = 1;
            
            for (int nodo : nodos) {
                if (mapaCoor.get(nodo).get(2) == 2) {  // Verifica si es célula calculadora (Tipo 2)
                    int[][] mCopy = copiarMatriz(grafo); // Crea una copia de la matriz
                    bloquearNodo(mCopy, nodo); // Bloquea el nodo en la matriz copia

                    int flujoConBloqueo = flujoMaximo(mCopy, 0, mCopy.length - 1);
                    int reduccion = maxF - flujoConBloqueo;

                    if (reduccion > maxReduccion) {
                        maxReduccion = reduccion;
                        nodoABloquear = nodo;
                    }
                }
            }  
            System.out.println(nodoABloquear + " " + maxF + " " + (maxF - maxReduccion));
        }
        scann.close(); 
    }
     

    //creacion de matriz de adja
    public static int[][] grafo(HashMap<Integer, List<Integer>> mapaCoor, HashMap<Integer, List<String>> mapa, int dist, List<Integer> nodos) {
    int numFilas = nodos.size();
    int[][] matriz = new int[numFilas + 2][numFilas + 2]; // +2 para supersource y supersink

    int supersource = 0;
    int supersink = numFilas + 1;


    for (int i = 0; i < nodos.size(); i++) {
        for (int j= 0; j < nodos.size(); j++) {
            if (i == j) continue;

            int id1 = nodos.get(i);
            int id2 = nodos.get(j);
            List<Integer> coords1 = mapaCoor.get(id1);
            List<Integer> coords2 = mapaCoor.get(id2);
            if (coords1 != null && coords2 != null && distancia(coords1.get(0), coords1.get(1), coords2.get(0), coords2.get(1), dist)) {
                int cap = capacidad(id1, id2, mapa);
                if (cap > 0) {
                    int tipo1 = coords1.get(2);
                    int tipo2 = coords2.get(2);
                    if (tipo1 == 1) {
                        matriz[supersource][id1] = Integer.MAX_VALUE;
                        if (tipo1 == 1 && tipo2 == 2) {
                            matriz[id1][id2] += cap;
                        }
                    } else if (tipo1 == 2 && tipo2 == 2) {
                        matriz[id1][id2] += cap;
                    } else if (tipo1 == 2 && tipo2 == 3) {
                        matriz[id1][id2] += cap;
                    }
                    if (tipo2 == 3) {
                        matriz[id2][supersink] = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }
    return matriz;
    }


    //Formula para contar la cantidad de peptidos (mensajes) entre nodos 
    public static int capacidad(int n1, int n2, HashMap<Integer, List<String>> m) {  
        int peptidos = 0; 
        List<String> cadena1 = m.get(n1);
        List<String> cadena2 = m.get(n2);
        if (cadena1 != null && cadena2 != null) {
            peptidos = (int) cadena1.stream().filter(cadena2::contains).count();
        }
        return peptidos;
    }

    //Formula para calcular la distacia entre dos nodos
    public static boolean distancia(int x1, int y1, int x2, int y2, int d) { 
        int dx = x2 - x1;
        int dy = y2 - y1;
        int distSq = dx * dx + dy * dy;
        return distSq <= d * d;
    }

    // Algoritmo de flujo máximo (Edmonds-Karp, basado en BFS)
    public static int flujoMaximo(int[][] capacidad, int source, int sink) {
        int n = capacidad.length;
        int[][] f = new int[n][n];
        int maxF = 0;

        while (true) {
            int[] parent = new int[n];
            Arrays.fill(parent, -1);
            parent[source] = source;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);

            while (!queue.isEmpty() && parent[sink] == -1) {
                int u = queue.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && capacidad[u][v] - f[u][v] > 0) {
                        parent[v] = u;
                        queue.add(v);
                    }
                }
            }

            if (parent[sink] == -1) {
                break;
            }

            int incrementF = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                incrementF = Math.min(incrementF, capacidad[u][v] - f[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                f[u][v] += incrementF;
                f[v][u] -= incrementF;
            }

            maxF += incrementF;
        }
        return maxF;
    }

    // Crea la matriz de copia
    public static int[][] copiarMatriz(int[][] original) {
        int[][] copia = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copia[i], 0, original[i].length);
        }
        return copia;
    }

    public static void bloquearNodo(int[][] matriz, int nodo) {
        for (int i = 0; i < matriz.length; i++) {
            matriz[nodo][i] = 0; // Elimina todas las conexiones desde el nodo
            matriz[i][nodo] = 0; // Elimina todas las conexiones hacia el nodo
        }
    }
}
