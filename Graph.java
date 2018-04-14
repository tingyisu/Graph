package assignment4Graph;

public class Graph {
	
	boolean[][] adjacency;
	int nbNodes;
	
	public Graph (int nb){
		this.nbNodes = nb;
		this.adjacency = new boolean [nb][nb];
		for (int i = 0; i < nb; i++){
			for (int j = 0; j < nb; j++){
				this.adjacency[i][j] = false;
			}
		}
	}
	
	public void addEdge (int i, int j){
		// set both edges to true (since is undirected) 
		this.adjacency[i][j] = true;
		this.adjacency[j][i] = true;		
	}
	
	public void removeEdge (int i, int j){
		// set both edges to false (since is undirected) 
		this.adjacency[i][j] = false;
		this.adjacency[j][i] = false; 
	}
	
	public int nbEdges(){
		// find number of edges (non-self loop) & number of edges (self-loop) separately
		int numberNonSelf = 0;
		int numberSelf = 0; 
		for (int i = 0; i < this.adjacency.length; i++) {
			for (int j = 0; j < this.adjacency[i].length; j++) {
				if (adjacency[i][j] == true) {
					if (i == j) {
						numberSelf++;
					} else {
						numberNonSelf++; 
					}
				} 
			}
		}
		// divide number by 2 b/c is an undirected graph so counts the same edge twice 
		numberNonSelf = numberNonSelf/2; 
		return numberSelf + numberNonSelf;
	}
	
	public boolean cycle(int start){
		// variables passed as input for DFS helper method
		boolean[] visited = new boolean[nbNodes];
		int[] nodesVisited = new int[this.nbNodes * this.nbNodes];
		for (int i = 0; i < nodesVisited.length; i++) {
			nodesVisited[i]= -1;
		}		
		
		// Perform DFS on above variables
		this.DFS(visited,-1,start, nodesVisited);  
            
		// Find the start node in the array
		// if it is present in the array, it must be part of a cycle
		// if it is not present in the array, it is not part of a cycle(DFS never went back to the start node)
		for (int i = 0; i< nodesVisited.length; i++) {
			if (nodesVisited[i] == start) {
				return true;
			}
		}
		return false;
	}
	
	// Helper method 1 for cycle 
	// Using an int array, nodesVisited,  to store the nodes that were visited(excluding the start node and the node visited right before a particular node)
	// Since the start node was not stored in the array, if it appears in the array after DFS, start must be in a cycle
	public int[] DFS(boolean[] visited, int u, int start, int[] nodesVisited) {
		//setting the start variable to visited
		visited[start]=true;
		//Creating an array to store the neighbours of start
		int[] neighbours = this.neighboursArray(start);
		for(int i=0; i<neighbours.length; i++) {
			// If these neighbours have not been visited yet, call DFS on them
			if(!visited[neighbours[i]]) {
				this.DFS(visited, start, neighbours[i], nodesVisited);
			// neighbour has been visited and is not the previous node that was visited
			} else if(neighbours[i]!=u && neighbours[i]!=start) {
				this.addNode(nodesVisited, neighbours[i]); // add the neighbour to the nodesVisited array
				this.addNode(nodesVisited, start); // add the start variable to the nodesVisited array
			}
		}	
		return nodesVisited;
	}
	
	// Helper method 2 for cycle
	// adds a node to the array
	public void addNode(int[] visited, int toAdd) {
		for(int i = 0; i < visited.length; i++) {
			if(visited[i] == -1) {
				visited[i] = toAdd;
				break;
			}
		}	
	}
	
	// Helper method 3 for cycle
	// Creates an array of the neighbours of  the node given as input
	// Calls the numberNeighbours method to initialize the array
	public int[] neighboursArray(int node) {
		int [] adjacentNodes = new int[this.numberNeighbours(node)];
		int i = 0;
		// Used the adjacency[][] attribute to check if nodes are neighbours
		while(i<adjacentNodes.length) {
			for (int j = 0; j < this.nbNodes; j++){
				if(this.adjacency[node][j] == true) {
					adjacentNodes[i] = j;
					i++;
				}
			}
		}
		return adjacentNodes;
	}
	
	// Helper method 4 for cycle 
	// Finds the number of neighbours that a node has
	public int numberNeighbours(int node) {
		int neighbours = 0;
		for(int i = 0; i < this.nbNodes; i++) {
			if(this.adjacency[node][i] == true) {
				neighbours++;
			}
		}
		return neighbours;
	}
	
	public int shortestPath(int start, int end){
		// count the number of neighbours of start and end
		// not including their self loops
		int neighboursStart = 0;
		int neighboursEnd = 0; 
		for (int i = 0; i < nbNodes; i++) {
			if (this.adjacency[start][i] == true && start != i) {
				neighboursStart++;
			}
			if (this.adjacency[end][i] == true && start != i) {
				neighboursEnd++; 
			}
		}
		// calculate shortest path for self-loops, cycles, noncycles, and no path 
		if (start == end) {
			// if is a self-loop 
			if (this.adjacency[start][end]) {
				return 1;
			// if is a cycle, find the shortest cycle 
			} else if (!this.adjacency[start][end] && this.cycle(start)){
				int length = this.cyclePathLength(start); 
				if (length == 2) {
					length++; 
				}
				return length; 
			} else {
				// if no self loop or cycle exists 
				return nbNodes + 1; 
			}
		// if start and end are connected
		} else if (this.adjacency[start][end] == true){
			return 1;
		// if either start or end is disconnected, no such path exists
		} else if (neighboursStart == 0 || neighboursEnd == 0) {
			return nbNodes + 1;
		// a path exists and need to use BFS to compute the path 
		} else {
			int length = 0; 
			// store the dequeued BFS path (BFS gives the shortest path) 
			int[][] path = this.BFS(start, end);
			// find index of last node stored in path
			int index = -1; 
			for (int i = 0; i < path.length; i++) {
				if (path[i][1] == end) {
					index = i; 
					break; 
				}
			} 
			// backtrack back to the starting node 
			if (index != -1) {
				// follow the end node back to start to find the length of the path 
				int currentNode = path[index][0]; 
				for (int i = index; i >= 0; i--) {
					if (path[i][1] == currentNode) {
						length++; 
						currentNode = path[i][0];
					}
				}
			// if no path exists 
			} else {
				return nbNodes + 1;
			}
			return length; 
		}
	}
	
	// helper method 1 for shortestPath
	// determines whether or not the queue array is empty
	public boolean isEmpty(int[][] array) {
		// just need to check if the currentnNode is empty or not 
		for (int i = 0; i < array.length; i++) {
			if (array[i][1] != -1) {
				return false;
			}
		}
		return true; 
	}
	
	// helper method 2 for shortestPath
	// returns a popped list
	public int[][] cyclePathDescending(int start){
		// the first time a node is visited is the shortest path to that node from the source node. 
		// store all popped nodes
		int[][] popped = new int[nbEdges() * nbEdges()][2]; 
		for (int i = 0; i < popped.length; i++) {
			for (int j = 0; j < popped[i].length; j++) {
				popped[i][j] = -1; 
			}
		}
		// create a stack using int[][]
		// since each node sum of all deg(v) is max 2 * nbEdges 
		int[][] stack = new int[2 * nbEdges()][2]; 
		for (int i = 0; i < stack.length; i++) {
			for (int j = 0; j < stack[i].length; j++) {
				stack[i][j] = -1; 
			}
		}
		boolean[] visited = new boolean[nbNodes]; 
		// implement DFS
		// father of starting node is not important
		stack[0][0] = -1;
		stack[0][1] = start; 
		// i represents index of popped array
		int i = 0; 
		// j represents index of stack 
		int j = 0; 
		int numberOfOccurrences = 0; 
		while (!this.isEmpty(stack)) {
			// store the father and currentNode
			int father = stack[j][0];
			int currentNode = stack[j][1]; 
			// once have reached starting node 
			if (currentNode == start) {
				numberOfOccurrences++;
			} 
			// if start occurs twice then there is a cycle
			if (numberOfOccurrences == 2) {
				stack[j][0] = -1;	
				stack[j][1] = -1;
				popped[i][0] = father;
				popped[i][1] = currentNode;  	
				return popped; 
			}
			// pop 
			stack[j][0] = -1;
			stack[j][1] = -1;
			popped[i][0] = father;
			popped[i][1] = currentNode; 
			i++; 
			// if have not reached starting node yet 		
			// continue DFS
			if (!visited[currentNode]){
				// set to visited 
				visited[currentNode] = true; 
				// push all neighbours
				for (int k= nbNodes - 1; k >= 0 ; k--) {
					if (this.adjacency[currentNode][k] == true && k!=father) {
						stack[j][0] = currentNode;
						stack[j][1] = k; 
						j++;  
					}
				}
			} 
			j--; 
		}	
		return popped;	
	}
	
	// helper method2 for shortestPath
	// returns a popped list 
	public int[][] cyclePathAscending(int start){ 
		// store all popped nodes
		int[][] popped = new int[nbEdges() * nbEdges()][2]; 
		for (int i = 0; i < popped.length; i++) {
			for (int j = 0; j < popped[i].length; j++) {
				popped[i][j] = -1; 
			}
		}
		// create a stack using int[][]
		// since each node sum of all deg(v) is max 2 * nbEdges 
		int[][] stack = new int[2 * nbEdges()][2]; 
		for (int i = 0; i < stack.length; i++) {
			for (int j = 0; j < stack[i].length; j++) {
				stack[i][j] = -1; 
			}
		}
		boolean[] visited = new boolean[nbNodes]; 
		// implement DFS
		// father of starting node is not important
		stack[0][0] = -1;
		stack[0][1] = start; 
		// i represents index of popped array
		int i = 0; 
		// j represents index of stack 
		int j = 0; 
		int numberOfOccurrences = 0; 
		while (!this.isEmpty(stack)) {
			// store the father and currentNode
			int father = stack[j][0];
			int currentNode = stack[j][1]; 
			// once have reached start 
			if (currentNode == start) {
				numberOfOccurrences++;
				
			} 
			// if start occurs twice, there is a cycle
			if (numberOfOccurrences == 2) {
				stack[j][0] = -1;	
				stack[j][1] = -1;
				popped[i][0] = father;
				popped[i][1] = currentNode;  
				return popped; 
			}
			// pop 
			stack[j][0] = -1;
			stack[j][1] = -1;
			popped[i][0] = father;
			popped[i][1] = currentNode; 
			i++;
			// if have not reached starting node twice
			// continue DFS
			if (!visited[currentNode]){
				// set to visited 
				visited[currentNode] = true; 
				// push all neighbours (not including its father) 
				for (int k = 0; k < nbNodes; k++) {
					if (this.adjacency[currentNode][k] == true && k!=father) {
						stack[j][0] = currentNode;
						stack[j][1] = k; 
						j++;   
					}
				}
			}
			j--; 
		}	
		return popped; 
	}
	
	// helper method 3 for shortestPath
	// returns the shortest length between the ascending and descending cycle paths
	public int cyclePathLength(int start) {
		// store the cycle paths 
		int[][] pathA = this.cyclePathAscending(start);
		int[][] pathB =  this.cyclePathDescending(start);
		
		// calculate length of pathA first
		int lengthA = 0; 
		// find the index of the last node stored in pathA
		int indexA = -1; 
		for (int i = pathA.length - 1; i >= 0; i--) {
			if (pathA[i][1] == start) {
				indexA = i; 
				break; 
			}
		} 
		// backtrack back to start
		if (indexA != -1) {
			// follow the start node back to itself to find the length of the path 
			int currentNode = pathA[indexA][0]; 
			for (int i = indexA; i >= 0; i--) {
				if (pathA[i][1] == currentNode) {
					lengthA++; 
					currentNode = pathA[i][0];
				}
			}
		} 
		// calculate length of pathB
		int lengthB = 0; 
		// find index of last node stored in pathB
		int indexB = -1; 
		for (int i = pathB.length - 1; i >= 0; i--) {
			if (pathB[i][1] == start) {
				indexB = i; 
				break; 
			}
		} 
		// backtrack back to start
		if (indexB != -1) {
			// follow the end node back to start to find the length of the path 
			int currentNode = pathB[indexB][0]; 
			for (int i = indexB; i >= 0; i--) {
				if (pathB[i][1] == currentNode) {
					lengthB++; 
					currentNode = pathB[i][0];
				}
			}
		}
		// no cycle exists if both indexA and indexB are -1
		if (indexA==-1 && indexB==-1) {
			return this.nbNodes+1;
		}
		// compare lenthA and lengthB and return the shortest 
		if (lengthA > lengthB) {
			return lengthB;
		} else {
			return lengthA;	
		} 
	}
	
	// helper method 4 for shortestPath
	// performs BFS and returns the dequeued list 
	public int[][] BFS(int start, int end){
		// the first time a node is visited is the shortest path to that node from the source node. 
		// store all dequeued nodes
		int[][] dequeued = new int[2 * nbEdges()][2]; 
		for (int i = 0; i < dequeued.length; i++) {
			for (int j = 0; j < dequeued[i].length; j++) {
				dequeued[i][j] = -1; 
			}
		}
		// create a queue using int[][]
		// since each node only appears in the queue once, max size is nbNodes
		int[][] queue = new int[nbNodes][2]; 
		for (int i = 0; i < queue.length; i++) {
			for (int j = 0; j < queue[i].length; j++) {
				queue[i][j] = -1; 
			}
		}
		boolean[] visited = new boolean[nbNodes]; 
		// implement BFS
		// a self-loop has path 1
		visited[start] = true; 
		// father of starting node is not important
		// enqueue 
		queue[0][0] = -1;
		queue[0][1] = start; 
		// i represents index of dequeued array
		int i = 0; 
		while (!this.isEmpty(queue)) {
			// store the father and currentNode
			// first in first out 
			int father = queue[0][0];
			int currentNode = queue[0][1]; 
			// once have reached end node 
			if (currentNode == end) {
				// dequeue
				queue[0][0] = -1;	
				queue[0][1] = -1;
				dequeued[i][0] = father;
				dequeued[i][1] = currentNode;  
				return dequeued; 
			// if have not reached end node yet 
			} else {
				// dequeue
				queue[0][0] = -1;
				queue[0][1] = -1;
				dequeued[i][0] = father;
				dequeued[i][1] = currentNode; 
				i++; 
				// shift all queued items 
				for (int k = 0; k < queue.length - 1; k++) {
					queue[k] = queue[k + 1];  
				}
				// find the first available index to store its neighbours
				int number = -1; 
				for (int l = 0; l < queue.length; l++) {
					if (queue[l][1] == -1) {
						number = l; 
						break; 
					}
				}
				// enqueue all unvisited neighbours
				for (int j = 0; j < nbNodes; j++) {
					if (this.adjacency[currentNode][j] == true && !visited[j]) {
						visited[j] = true;
						queue[number][0] = currentNode;
						queue[number][1] = j; 
						number++; 
					} 
				}
			}
		}	
		return dequeued;
	}
	
}