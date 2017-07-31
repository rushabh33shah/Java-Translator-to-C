package cs652.j.semantics;

import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;


public class JClass extends ClassSymbol {
	public JClass(String name, ParserRuleContext tree) {
		super(name);
		setDefNode(tree);
	}

	/*public JClass(String name) {
		super(name);
	}*/
}
