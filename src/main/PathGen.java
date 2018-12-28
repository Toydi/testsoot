package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.microsoft.z3.Z3Exception;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;


class Element {
	List<Integer> blocks=new ArrayList<Integer>();
	int nextIndex;

	public Element(List<Integer> b, int nextIndex) {
		this.blocks.addAll(b);
		this.nextIndex = nextIndex;
	}
}

public class PathGen {

	BlockGraph graph;
	
	public PathGen(BlockGraph b){
		this.graph=b;
	}
	
	public List<String> pathConditionAndResult() throws Z3Exception {
		
		List<List<Integer>> allPath = new ArrayList<>();
		Queue<Element> queue = new LinkedList<>();
		Block head = graph.getHeads().get(0);
		for (Block b : head.getSuccs()) {
			List<Integer> blocks = new ArrayList<Integer>();
			blocks.add(head.getIndexInMethod());
			queue.add(new Element(blocks, b.getIndexInMethod()));
		}
		while (!queue.isEmpty()) {
			Element e = queue.poll();
			Block nextBlock = graph.getBlocks().get(e.nextIndex);
			if (graph.getTails().contains(nextBlock)) {
				List<Integer> path = new ArrayList<Integer>();
				path.addAll(e.blocks);
				path.add(e.nextIndex);
				allPath.add(path);
			} 
			else{
				for (Block b : nextBlock.getSuccs()) {
					List<Integer> path = new ArrayList<Integer>();
					path.addAll(e.blocks);
					path.add(e.nextIndex);
					queue.add(new Element(path, b.getIndexInMethod()));
				}
			}
		}

		List<String> res = new ArrayList<>();
		Map<String, String> declareRecord = new HashMap<>();
		for (Local local : graph.getBody().getLocals()) {
			String type = local.getType().toString();
			String name = local.getName();
			String declareFormat="";
			if(type.equals("int")){
				declareFormat="(declare-const ??? Int)";
			}
			declareRecord.put(name, declareFormat);
		}
		for (List<Integer> path : allPath) {
			List<Unit> units = new ArrayList<>();
			for (int i : path) {
				Block block = graph.getBlocks().get(i);
				Iterator<Unit> si = block.iterator();
				while (si.hasNext()) {
					units.add(si.next());
				}
			}
			SmtExpr smtExpr = new SmtExpr(units, declareRecord);
			String cond=smtExpr.getResult();
			res.add(cond);
		}
		return res;
	}
	
	public static void main(String[] args) throws Z3Exception, IOException {
		// TODO Auto-generated method stub
		String sootClass = "example.test";
		Scene.v().setSootClassPath("E:/Java/jdk1.8.0_191/jre/lib/rt.jar;C:/Users/Toydi/Desktop/testSoot/src");
//		C:/Program Files/Java/jdk1.7.0_80/jre/lib/rt.jar;
		SootClass sClass = Scene.v().loadClassAndSupport(sootClass);
		sClass.setApplicationClass();

		String outPath = "C:/Users/Toydi/Desktop/testSoot/src/out";
		new File(outPath).mkdir();
		SootMethod m =sClass.getMethods().get(0);
		Body b = m.retrieveActiveBody();
		BriefBlockGraph graph = new BriefBlockGraph(b);
		CFGToDotGraph cfg = new CFGToDotGraph();
		DotGraph dotGraph = cfg.drawCFG(graph, b);
		dotGraph.plot(outPath + "/graph.out");
				
		PathGen pathGen = new PathGen(graph);
		List<String> results = pathGen.pathConditionAndResult();
				
		for(int i=0;i<results.size();i++){
			String fileName = outPath+"/getMax_"+i;
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
			writer.write(results.get(i));
			System.out.println(results.get(i));
			writer.close();
		}
				
	}

	
}
