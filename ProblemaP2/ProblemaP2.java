import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


public class ProblemaP2{  
    //Main donde se arma y cre el grafo 
    public static void main(String[] args) {
        Scanner scann = new Scanner(System.in); 
            int numCases = Integer.parseInt(scann.nextLine()); 
            for (int i = 0 ; i < numCases ; i++){ 
                String[] lineCase = scann.nextLine().split(" "); 
                int numFilas = Integer.parseInt(lineCase[0]); 
                int dist = Integer.parseInt(lineCase[1]); 
                //List<Integer> nodos = new ArrayList<>(); 
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
            int [][] m = matrizOrigen(mapaCoor, mapa, dist, nodos); 
            System.out.println(Arrays.deepToString(m));
                }  
            }  
            scann.close(); 
        }
     

    //creacion de matriz hacer funcion 
    public static int [][] matrizOrigen (HashMap<Integer,List<Integer>> mapaCoor, HashMap<Integer,List<String>>mapa , int dist, List<Integer> nodos){ 
        int numFilas = nodos.size(); 
        int [][] matriz = new int [numFilas+2][numFilas+2]; 
        for(Map.Entry<Integer,List<Integer>> nodo1 : mapaCoor.entrySet()){ 
            for(Map.Entry<Integer,List<Integer>> nodo2 : mapaCoor.entrySet()){ 
                if(nodo1.getValue().get(2) == 1 && nodo2.getValue().get(2)==2){ 
                    if(distancia(nodo1.getValue().get(0), nodo2.getValue().get(1), nodo2.getValue().get(0), nodo2.getValue().get(1), dist)){ 
                        int supersource = 0;
                        matriz[supersource][nodo2.getKey()] += capacidad(nodo1.getKey(), nodo2.getKey(),mapa);
                    }
                }
            }
        } 
        return matriz;
    }


    //Formula para contar la cantidad de peptidos (mensajes) entre nodos 
    public static int capacidad ( int n1, int n2 , HashMap<Integer,List<String>> m){  
        int peptidos = 0; 
        for (Map.Entry<Integer,List<String>> entry1 : m.entrySet()){ 
            for (Map.Entry<Integer,List<String>> entry2 : m.entrySet()){ 
                if (entry1.getKey() == n1 && entry2.getKey() == n2){ 
                    List<String> cadena1 = entry1.getValue(); 
                    List<String> cadena2 = entry2.getValue(); 
                    peptidos =  (int) cadena1.stream().filter(cadena2::contains).count();
                }
            }
        }
        return peptidos;
    } 









    //Formula para calcular la distacia entre dos nodos
    public static boolean distancia (int x1, int y1, int x2, int y2, int d){ 
        boolean resp = false; 
        int first_rest = x2-x1;
        int second_rest = y2-y1;
        int new_distance = (int)Math.sqrt(Math.pow(first_rest, 2) + Math.pow(second_rest, 2)); 
        if (new_distance == d){ 
            resp = true;
        }   
        return resp;
    }
}