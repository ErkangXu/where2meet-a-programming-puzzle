package erkang;

//import java.io.File;
//import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GetMeetingLocation {
	public static HashSet<ArrayList<String>> mapInput = new HashSet<ArrayList<String>>();
	public static Map<String,HashSet<String>> equalNodeMap = new HashMap<String,HashSet<String>>(); // A data Structure to store the interconnected nodes. 
	// If a and b are double connected, and b c are double connected. a b c are in the same group
	
	public static void main(String[] args) {
	
		/*// Automated test
		Scanner s = null;
		try {
			s = new Scanner(new File("test.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		
		Scanner s = new Scanner(System.in);
		//System.out.println("Please provide input!");
		s.nextLine();
		
		while(s.hasNext()){
			String temp = s.nextLine();
			if(temp.equals("Avoid:")) break;
			String[] tempStringArray = temp.split("\\s+");
			ArrayList<String> tempArrayList = new ArrayList<String>();
			tempArrayList.add(tempStringArray[0]);
			tempArrayList.add(tempStringArray[1]);
			mapInput.add(tempArrayList);
		}

		String avoidListString = s.nextLine();
		List<String> avoidNodeList = new ArrayList<String>();
		if(!avoidListString.equals("")){
			String[] AvoidListArray = avoidListString.split("\\s+");
			avoidNodeList = Arrays.asList(AvoidListArray);
		}
		
		/*// Avoid list test
		System.out.println("Avoid nodes are:");
		for (String entry : avoidNodeList) {
		    System.out.println(entry);
		}*/
		
		for(Iterator<ArrayList<String>> ite = mapInput.iterator(); ite.hasNext();){
			ArrayList<String> tempList = ite.next();
			String upStreamNodeName = tempList.get(0); // up mean the the node is up Stream in the current edge
			String downStreamNodeName = tempList.get(1);
			if(upStreamNodeName.equals(downStreamNodeName)){
				ite.remove(); // In the last paragraph of the problem description, it says 100 nodes(10000 edges), so a node might point to itself
			}
			else if(avoidNodeList.contains(upStreamNodeName)||avoidNodeList.contains(downStreamNodeName)){
				ite.remove(); // Because the void nodes can not be passed, I need to remove all the edges contain them
			}
		}
				
		s.nextLine();
		List<String> peggyStartingNodeList = new ArrayList<String>();
		String peggyStartingNodeString = s.nextLine();
		String[] peggyStartingNodeArray = peggyStartingNodeString.split("\\s+");
		for(int i=0; i<peggyStartingNodeArray.length; i++){
			peggyStartingNodeList.add(peggyStartingNodeArray[i]);
		}
		peggyStartingNodeList.removeAll(avoidNodeList);
		
		/*// Peggy starting points test:
		System.out.println("Peggy starting nodes are:");
		for (String entry : peggyStartingNodeList) {
		    System.out.println(entry);
		}*/
		
		s.nextLine();
		List<String> samStartingNodeList = new ArrayList<String>();
		String samStartingNodeString = s.nextLine();
		String[] samStartingNodeArray = samStartingNodeString.split("\\s+");
		for(int i=0; i<samStartingNodeArray.length; i++){
			samStartingNodeList.add(samStartingNodeArray[i]);
		}
		samStartingNodeList.removeAll(avoidNodeList);
		
		 /* //Sam starting points test
		System.out.println("Sam starting nodes are:");
		for (String entry : samStartingNodeList) {
		    System.out.println(entry);
		}*/
		
		simplifyMap(); // This is a recursive method that simplify the map to the full most
		
/*      // Test of MapInput
			System.out.println("Here is the content of mapInput");
			for(ArrayList<String> element: mapInput){
				System.out.println(element.get(0));
				System.out.println(element.get(1)+".");
			}*/
		
		/*// Test of equalNodeMap
			System.out.println("Here is the content of equalNodeMap");
			for(String key: equalNodeMap.keySet()){
				System.out.print("The equal node set of node "+key+" is:");
				HashSet<String> tempSet = equalNodeMap.get(key);
				for(String i: tempSet){
					System.out.print(i+",");
				}
				System.out.println();
			}*/
				
		Map<String, Node> validNodeMap = new HashMap<String, Node>();// Build a map structure to store the reduced connection data
		// put the connection data into the map structure
		for(ArrayList<String> segment: mapInput){
			Node upStreamNode;
			Node downStreamNode;
			String upStreamNodeName = segment.get(0);
			String downStreamNodeName = segment.get(1);
			if (validNodeMap.containsKey(upStreamNodeName)) {
				upStreamNode = validNodeMap.get(upStreamNodeName);
			}
			else {
				upStreamNode = new Node(upStreamNodeName);
				validNodeMap.put(upStreamNodeName, upStreamNode);
			}
			if (validNodeMap.containsKey(downStreamNodeName)) {
				downStreamNode = validNodeMap.get(downStreamNodeName);
			}
			else {
				downStreamNode = new Node(downStreamNodeName);
				validNodeMap.put(downStreamNodeName, downStreamNode);
			}
			upStreamNode.addDownStreamNeighbor(downStreamNode);
		}

		Set<String> peggyStartingNodesOnSimpleMap = new HashSet<String>();
		for(String nodeName: peggyStartingNodeList){
			boolean flag = false;
			for(String key: equalNodeMap.keySet()){
				HashSet<String> tempSet = equalNodeMap.get(key);
				if(tempSet.contains(nodeName)){
					peggyStartingNodesOnSimpleMap.add(key); // replace the original starting point to the representative of that set
					flag = true;
					break; // Each equal node set are mutually exclusive to each other, so once fit in one set I can break the loop
				}
			}
			if(!flag){
				peggyStartingNodesOnSimpleMap.add(nodeName);
			}	
		}
		
		Set<String> samStartingNodesOnSimpleMap = new HashSet<String>();
		for(String nodeName: samStartingNodeList){
			boolean flag = false;
			for(String key: equalNodeMap.keySet()){
				HashSet<String> tempSet = equalNodeMap.get(key);
				if(tempSet.contains(nodeName)){
					samStartingNodesOnSimpleMap.add(key);
					flag = true;
				}
			}
			if(!flag){
				// If it's not a representative or in any equal set, add its original name
				samStartingNodesOnSimpleMap.add(nodeName);
			}	
		}
		/*// Test of actual Peggy starting points
		System.out.println("Peggy's Starting points on simplified map");
		for(String peggyS: peggyStartingNodesOnSimpleMap){
			System.out.print(peggyS+",");
		}
		System.out.println();
		
		// Test of actual Sam starting points
		System.out.println("Sam's Starting points on simplified map");
		for(String samS: samStartingNodesOnSimpleMap){
			System.out.print(samS+",");
		}
		System.out.println();*/
		
		// The representative node of a independent group of fully double connected nodes will not appear on validNodeMap after the 
		// filtering process. For example, a and b only connected to each other, whatever their representative is, it will not show on
		// validNodeMap. but I have to add those singular representative on the map manually
		
		//System.out.print("The singular nodes are:");
		for(String key: equalNodeMap.keySet()){
			if(!validNodeMap.containsKey(key)){
				//System.out.print(key+",");
				validNodeMap.put(key, new Node(key));
			}
		}
		//System.out.println();
	
		LinkedList<ArrayList<String>> activeRouteList = new LinkedList<ArrayList<String>>(); // Breadth first search. I need to keep order thus use LinkedList
		
		for(String startingNodeName : peggyStartingNodesOnSimpleMap){
				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(startingNodeName); // At first each route has only one point
				activeRouteList.add(tempList);
		}
		
		List<HashSet<String>> setOfLoops = new ArrayList<HashSet<String>>(); // There might have loops in the simplified map
		Set<String> meetingNodeSet = new HashSet<String>();
		
		while(!activeRouteList.isEmpty()){
			ArrayList<String> route = activeRouteList.removeFirst();
			String endNodeName = route.get(route.size()-1);
			Node endNode = validNodeMap.get(endNodeName);
			if(samStartingNodesOnSimpleMap.contains(endNodeName)){
				meetingNodeSet.addAll(route);
			}
			// Even you found the destination, peggy should go on, because she might find a loop
			if(endNode.neighborList != null){
				for(Node neighborNode: endNode.neighborList){
					String neighborNodeName = neighborNode.nodeName;
					int indexOfFirstOccurrence = route.indexOf(neighborNodeName);
					if(indexOfFirstOccurrence != -1){
						// Passed by this node before!
						List<String> loopNodeList = route.subList(indexOfFirstOccurrence, route.size());
						ArrayList<Integer> indexList = new ArrayList<Integer>(); // A list to contain the indices of sets that have intersection with current loop
						for(int i=0; i<setOfLoops.size(); i++){
							HashSet<String> tempSet = setOfLoops.get(i);
							if(tempSet.removeAll(loopNodeList)){
								// This means two set have intersection, I can merge them
								indexList.add(i);
								continue; // Should find all the sets that have intersection. Loop on
							}
						}
						if(indexList.size()!=0){
							if(indexList.size()==1){
								// This means, it found only set to merge in
								setOfLoops.get(indexList.get(0)).addAll(loopNodeList);
							}
							else{
								// This loop has intersection with multiple sets, I can combine them all
								HashSet<String> tempSet = new HashSet<String>();
								for(Integer i:indexList){
									tempSet.addAll(setOfLoops.get(i));
								}
								for(Integer i:indexList){
									setOfLoops.remove(i);
								}
								setOfLoops.add(0, tempSet);
							}
							
						}
						else{
							// This loop cannot be merged, I have to add a new set
							HashSet<String> loopNodeSet = new HashSet<String>(loopNodeList);
							setOfLoops.add(loopNodeSet);
						}
					}
					else{
						ArrayList<String> newRoute = new ArrayList<String>(route); // Have to use a copy of the route, otherwise it will affect other path
						newRoute.add(neighborNodeName);
						activeRouteList.add(newRoute);
					}
					
				}
			}
			
		}
		
			/*//Test of the loop set
			System.out.println("The following is the nodes in the loop!");
			for(HashSet<String> set: setOfLoops){
				for(String string: set){
					System.out.print(string+",");
				}
				System.out.println();
			}*/
			
			Set<String> additionalLocationSet = new HashSet<String>(); // If a meeting node is in a loop set, all the nodes in that loop are valid meeting place
			for(Set<String> loopNodeNameSet:setOfLoops){
				for(String nodeName:meetingNodeSet){
					if (loopNodeNameSet.contains(nodeName)){
						additionalLocationSet.addAll(loopNodeNameSet);
					}
				}
			}
			meetingNodeSet.addAll(additionalLocationSet);
			
			HashSet<String> additionalEqualNodeSet = new HashSet<String>(); // All the nodes being represented can be added back now
			for (String equalNodeSetKey: equalNodeMap.keySet()){
				HashSet<String> equalNodeSet = equalNodeMap.get(equalNodeSetKey);
				for(String primaryMeetingNodeName: meetingNodeSet){
					if (equalNodeSetKey.equals(primaryMeetingNodeName)){
						additionalEqualNodeSet.addAll(equalNodeSet);
					}
				}
				
			}
			/*// Test of additionalEqualNodeSet
			System.out.println("The following are the equalNodes");
			for (String additionalEqualNodeName: additionalEqualNodeSet){
				System.out.println(additionalEqualNodeName);
			}*/
			
			meetingNodeSet.addAll(additionalEqualNodeSet);
			
		List<String> meetingNodeList = new ArrayList<>(meetingNodeSet);
		java.util.Collections.sort(meetingNodeList);
		
		//System.out.println("The Following are meeting points");
		
		for(String locationName :meetingNodeList){
			System.out.println(locationName);
		}
	}
	
	public static void simplifyMap(){
		HashSet<ArrayList<String>> tempSet = new HashSet<ArrayList<String>>(); // A temporal set to receive modification
		for(ArrayList<String> edge:mapInput){
			String upStreamNodeName = edge.get(0); // up mean the the node is up Stream in the current edge
			String downStreamNodeName = edge.get(1);
			ArrayList<String> reversedTempList = new ArrayList<String>();
			reversedTempList.add(downStreamNodeName);
			reversedTempList.add(upStreamNodeName);
			if (tempSet.contains(reversedTempList)){
				// This means two way connection is encountered. There is a revered one way connection in the temSet
				boolean upNodeFlag = false; // Flag to indicate whether node is in a equal node group
				boolean downNodeFlag = false;
				String upNodeKey = new String(); // upNode mean the up node in the first appearance of this two way edge
				String downNodeKey = new String();
				if (equalNodeMap.containsKey(downStreamNodeName)){
					upNodeFlag = true;
					upNodeKey = downStreamNodeName;
				}
				else {
					for(String key: equalNodeMap.keySet()){
						HashSet<String> aSet = equalNodeMap.get(key);
						if (aSet.contains(downStreamNodeName)){
							upNodeKey = key; // Get the key of the set that contains this node
							upNodeFlag = true; // The key of the equal node set is also in that group
							break;
						} 
					}
				}
				if (equalNodeMap.containsKey(upStreamNodeName)){
					downNodeFlag = true;
					downNodeKey = upStreamNodeName;
				}
				else {
					for(String key: equalNodeMap.keySet()){
						HashSet<String> aSet = equalNodeMap.get(key);
						if (aSet.contains(upStreamNodeName)){
							downNodeKey = key; 
							downNodeFlag = true;
							break;
						} 
					}
				}
				if(upNodeFlag&&downNodeFlag){
					if(!upNodeKey.equals(downNodeKey)){
						// This edge will interconnect two previously separate group
						HashSet<String> disappearingSet = equalNodeMap.get(downNodeKey);
						equalNodeMap.get(upNodeKey).addAll(disappearingSet);
						equalNodeMap.get(upNodeKey).add(downNodeKey); // The key also needs to be added 
						equalNodeMap.remove(downNodeKey); // Don't forget to remove the acquired set
					}
				}
				else if(upNodeFlag){
					equalNodeMap.get(upNodeKey).add(upStreamNodeName);
					// Add the other end to the group
				}
				else if(downNodeFlag){
					equalNodeMap.get(downNodeKey).add(downStreamNodeName);
				}
				else{
					// Neither two end of this edge is in any group in the equalNodeMap, I have to build a new entry. 
					// Later, The key of this entry will be the representative of this group in the reduced map
					HashSet<String> tempHashSet = new HashSet<String>();
					tempHashSet.add(upStreamNodeName);
					//tempHashSet.add(downStreamNodeName); // For convenience, I add the representative node to the equal node set too
					equalNodeMap.put(downStreamNodeName, tempHashSet);
				}
				tempSet.remove(reversedTempList);
			}
			else{
				tempSet.add(edge);
			}	
		}
					
		// now I have to further reduce the map, replace each set of equal nodes to their representative
		HashSet<ArrayList<String>> removeSet = new HashSet<ArrayList<String>>(); // Auxiliary sets for modification
		HashSet<ArrayList<String>> addSet = new HashSet<ArrayList<String>>();
		for(ArrayList<String> line: tempSet){
			boolean upNodeFlag = false; // Indicator of whether the node is in equal set and need replacement
			boolean downNodeFlag = false;
			String upNodeKey = line.get(0); // The representative of a node. If it's not in a equal set, it represents itself
			String downNodeKey = line.get(1);
			if(equalNodeMap.containsKey(line.get(0))){
				// The node is a representative of a group
			} 
			else {
				for(String key: equalNodeMap.keySet()){
					HashSet<String> value = equalNodeMap.get(key);
				    if(value.contains(line.get(0))){
							upNodeFlag = true;
							upNodeKey = key; // Replace to its representative
							break;
					}
				}
			}
			if(equalNodeMap.containsKey(line.get(1))){
				
			} 
			else {
				for(String key: equalNodeMap.keySet()){
					HashSet<String> value = equalNodeMap.get(key);
				    if(value.contains(line.get(1))){
							downNodeFlag = true;
							downNodeKey = key;
							break;
					}	
				}
			}
			if(upNodeFlag||downNodeFlag){
				// It means that at least one of the node need to be modified
				removeSet.add(line); // The original input line should be removed
				if(!upNodeKey.equals(downNodeKey)){
					// If the representatives of two nodes are the same, I don't need to put a substitute in the addSet
					ArrayList<String> tempList= new ArrayList<String>();
					tempList.add(upNodeKey);
					tempList.add(downNodeKey);
					addSet.add(tempList);
				}
			}
		}
		
		tempSet.removeAll(removeSet);
		tempSet.addAll(addSet);

		/*// Test of removeSet
		System.out.println("Here is the content of removeSet");
		for(ArrayList<String> element: removeSet){
			System.out.println(element.get(0));
			System.out.println(element.get(1)+".");
		}

		// Test of addSet
		System.out.println("Here is the content of addSet");
		for(ArrayList<String> element: addSet){
			System.out.println(element.get(0));
			System.out.println(element.get(1)+".");
		}*/
		mapInput = tempSet; // Update the mapInput. prepare it for the recursive call
		
		
		
		if(!addSet.isEmpty()){
			// If addSet is empty, it means the mapInput is reduced to its simplest form
			simplifyMap();
		}
	}

}

class Node {
	
	public String nodeName;
	public List<Node> neighborList;
	
	public Node(String name){
		this.nodeName = name;
		this.neighborList = new ArrayList<Node>();
	}
	
	public void addDownStreamNeighbor(Node neighbor) {
		neighborList.add(neighbor);
	}
}
