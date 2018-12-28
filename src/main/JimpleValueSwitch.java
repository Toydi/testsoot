package main;

import soot.Value;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.GtExpr;

public class JimpleValueSwitch extends AbstractJimpleValueSwitch {

	SmtExpr s;
	private String result;

	public JimpleValueSwitch(SmtExpr s) {
		this.s = s;
		result = null;
	}

	public String trans(Value val) {
		val.apply(this);
		String temp = result;
		result = null;
		return temp;
	}

	public void defaultCase(Object obj) {
		result = obj.toString();
	}


	public void caseGtExpr(GtExpr v) {
		result = String.format("(> %s %s)", v.getOp1(),v.getOp2());
	}
	
}
