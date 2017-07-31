package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;

public class DefineScopesAndSymbols extends JBaseListener {
	public Scope currentScope;
	public GlobalScope globalScope;
	public DefineScopesAndSymbols(GlobalScope globals) {
		currentScope = globals;
		globalScope=globals;
		currentScope.define((Symbol) ComputeTypes.JINT_TYPE);
		currentScope.define((Symbol) ComputeTypes.JFLOAT_TYPE);
		currentScope.define((Symbol) ComputeTypes.JSTRING_TYPE);
		currentScope.define((Symbol) ComputeTypes.JVOID_TYPE);
	}

	@Override
	public void enterFile(JParser.FileContext ctx) {
		ctx.scope = globalScope;
	}

	@Override
	public void exitFile(JParser.FileContext ctx) {
		popScope();
	}

	@Override
	public void enterClassDeclaration(JParser.ClassDeclarationContext ctx) {
		JClass jClass = new JClass(ctx.name.getText(),ctx);
		if(ctx.superClass != null){
			jClass.setSuperClass(ctx.superClass.getText());
		}
		jClass.setEnclosingScope(currentScope);
		currentScope.define(jClass);
		//currentScope=jClass;
		ctx.scope = jClass;
		pushScope(jClass);
	}

	@Override
	public void exitClassDeclaration(JParser.ClassDeclarationContext ctx) {
		popScope();
	}

	@Override
	public void enterMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		JMethod jMethod = new JMethod((ctx.ID().getText()),ctx);
		jMethod.setEnclosingScope(currentScope);
		if(ctx.jType()!=null){
			jMethod.setType((Type) globalScope.resolve(ctx.jType().getText()));
		}
		else {
			jMethod.setType(ComputeTypes.JVOID_TYPE);
		}
		currentScope.define(jMethod);
		pushScope(jMethod);
		ctx.scope = jMethod;
		JArg jArg = new JArg("this");
		currentScope = currentScope.getEnclosingScope();
		jArg.setType((Type) globalScope.resolve(currentScope.getName()));
		pushScope(jMethod);
		currentScope.define(jArg);
	}

	@Override
	public void exitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		popScope();
	}

	@Override
	public void enterFormalParameter(JParser.FormalParameterContext ctx) {
		JArg jArg = new JArg(ctx.ID().getText());
		jArg.setType((Type) currentScope.resolve(ctx.jType().getText()));
		currentScope.define(jArg);
	}

	@Override
	public void enterMain(JParser.MainContext ctx) {
		JMethod jMethod = new JMethod("main",ctx);
		jMethod.setEnclosingScope(currentScope);
		currentScope.define(jMethod);
		//currentScope = jMethod;
		ctx.scope = jMethod;
		pushScope(jMethod);
	}

	@Override
	public void exitMain(JParser.MainContext ctx) {
		popScope();
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx){
		LocalScope localScope = new LocalScope(currentScope);
		currentScope.nest(localScope);
		//currentScope = localScope;
		ctx.scope = localScope;
		pushScope(localScope);
	}

	@Override
	public void exitBlock(JParser.BlockContext ctx) {
		popScope();
	}

	@Override
	public void enterFieldDeclaration(JParser.FieldDeclarationContext ctx) {
		JField jField = new JField(ctx.ID().getText());
		jField.setType((Type) currentScope.resolve(ctx.jType().getText()));
		currentScope.define(jField);

	}

	@Override
	public void enterLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {

		JArg jVar = new JArg(ctx.ID().getText());
		jVar.setType((Type) currentScope.resolve(ctx.jType().getText()));
		currentScope.define(jVar);
	}

	private void pushScope(Scope s) {

		currentScope = s;
	}

	private void popScope() {
		currentScope = currentScope.getEnclosingScope();
	}
}
