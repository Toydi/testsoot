package main;

import java.util.List;
import java.util.Map;

import com.microsoft.z3.Z3Exception;

import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;

public class SmtExpr extends AbstractStmtSwitch{

	List<Unit> units;
	JimpleValueSwitch jvs;
	StringBuilder condition;
	Map<String, String> declareRecord;
	Unit nextUnit;
	
	public SmtExpr(List<Unit> units, Map<String, String> declareRecord) {
		this.units = units;
		jvs = new JimpleValueSwitch(this);
		this.condition = new StringBuilder();
		this.declareRecord = declareRecord;
	}
	
	public String getResult() {
		
		for (int i = 0; i < units.size(); i++) {
			Unit u = units.get(i);
			if (u instanceof IfStmt) {
				nextUnit = units.get(i + 1);
			}
			u.apply(this);
			nextUnit = null;
		}
		
		String smt = condition.toString();
		String result="";
		try {
			result=Z3Checker.solve(smt);
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return smt+"\n"+result;
	}


	public void caseIdentityStmt(IdentityStmt stmt) {
		String left = jvs.trans(stmt.getLeftOp());
		String declareExpr=declareRecord.get(left);
		if(declareExpr!=null){
			condition.append(declareExpr.replace("???", left));
			condition.append("\n");
		}
	}

	public void caseIfStmt(IfStmt stmt) {
		String cond = jvs.trans(stmt.getCondition());
		if (nextUnit instanceof GotoStmt) {
			condition.append(String.format("(assert (not %s))", cond));
		} else {
			condition.append(String.format("(assert %s)", cond));
		}
		condition.append("\n");
	}

}
