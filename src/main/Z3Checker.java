package main;

import java.util.HashMap;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class Z3Checker {

	public static String solve(String smt) throws Z3Exception {
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		String result="";
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();
		BoolExpr[] expr = ctx.parseSMTLIB2String(smt, null, null, null, null);
		s.add(expr);
		if (s.check() == Status.SATISFIABLE) {
			result=s.getModel().toString();
		}
		return result;
	}
	
}
