
package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;
import org.antlr.symtab.TypedSymbol;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class ComputeTypes extends JBaseListener {
	protected StringBuilder buf = new StringBuilder();
	public Scope currentScope;
	public GlobalScope globalScope;
	public static final Type JINT_TYPE = new JPrimitiveType("int");
	public static final Type JFLOAT_TYPE = new JPrimitiveType("float");
	public static final Type JSTRING_TYPE = new JPrimitiveType("string");
	public static final Type JVOID_TYPE = new JPrimitiveType("void");

	public ComputeTypes(GlobalScope globals) {
		this.currentScope = globals;
		globalScope = globals;

	}

	public String getRefOutput() {
		return buf.toString();
	}

	@Override
	public void enterFile(JParser.FileContext ctx) { pushScope(ctx.scope);}

	@Override
	public void exitFile(JParser.FileContext ctx) {	popScope();	}

	@Override
	public void enterMain(JParser.MainContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitMain(JParser.MainContext ctx) {
		popScope();
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx) {	pushScope(ctx.scope); }

	@Override
	public void exitBlock(JParser.BlockContext ctx) {
		popScope();
	}

	@Override
	public void exitIdRef(JParser.IdRefContext ctx) {
		Symbol symbol= currentScope.resolve(ctx.ID().getText());
		if(symbol instanceof TypedSymbol ){
			ctx.type = ((TypedSymbol) symbol).getType();
		}
		buf.append(ctx.getText() + " is "+  ctx.type.getName() +"\n");
	}

	@Override
	public void exitIntRef(JParser.IntRefContext ctx) {
		buf.append(ctx.getText() + " is int\n");
	}

	@Override
	public void exitFloatRef(JParser.FloatRefContext ctx) {
		buf.append(ctx.getText() + " is float\n");
	}

	@Override
	public void exitCtorCall(JParser.CtorCallContext ctx) {
		Symbol symbol= currentScope.resolve(ctx.ID().getText());
		buf.append(ctx.getText() + " is "+  symbol.getName() +"\n");
	}

	@Override
	public void exitQMethodCall(JParser.QMethodCallContext ctx) {
		TypedSymbol symbol;
		if(ctx.expression().getText().equals("this")) {
			JArg jVar = (JArg) currentScope.resolve(ctx.expression().getText());
			symbol = (TypedSymbol) ((JClass) jVar.getType()).resolve(ctx.ID().getText());
		}
		else {
			symbol = (TypedSymbol) ((JClass)ctx.expression().type).resolve(ctx.ID().getText());
		}
		ctx.type = symbol.getType();
		buf.append(ctx.getText()+" is "+symbol.getType().getName()+"\n");
	}

	@Override
	public void exitMethodCall(JParser.MethodCallContext ctx) {
		Symbol symbol= currentScope.resolve(ctx.ID().getText());
		if(symbol instanceof JMethod ){
			ctx.type = ((JMethod) symbol).getType();
		}
		buf.append(ctx.getText() + " is "+  ctx.type.getName() +"\n");
	}

	@Override
	public void exitFieldRef(JParser.FieldRefContext ctx) {
		TypedSymbol symbol;
		if(ctx.expression().getText().equals("this")) {
			JArg jVar = (JArg) currentScope.resolve(ctx.expression().getText());
			symbol = (TypedSymbol) ((JClass) jVar.getType()).resolve(ctx.ID().getText());
		}else {
			symbol = (TypedSymbol) ((JClass) ctx.expression().type).resolve(ctx.ID().getText());
		}
		ctx.type=symbol.getType();
		buf.append(ctx.getText() + " is "+  ctx.type.getName() +"\n");
	}

	@Override
	public void exitThisRef(JParser.ThisRefContext ctx) {
		Symbol symbol= currentScope.resolve(ctx.getText());
		buf.append(ctx.getText() + " is "+  symbol.getScope().getEnclosingScope().getName() +"\n");
	}
	private void pushScope(Scope s) {
		currentScope = s;
	}

	private void popScope() {
		currentScope = currentScope.getEnclosingScope();
	}
}


