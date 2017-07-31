package cs652.j.codegen;


import cs652.j.codegen.model.*;
import cs652.j.parser.JBaseVisitor;
import cs652.j.parser.JParser;
import cs652.j.semantics.*;

import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;

public class CodeGenerator extends JBaseVisitor<OutputModelObject> {
	public static STGroup templates;
	public String fileName;

	public Scope currentScope;
	public JClass currentClass;
	public String myclass;


	public CodeGenerator(String fileName) {
		this.fileName = fileName;
		templates = new STGroupFile("cs652/j/templates/C.stg");
	}

	public void pushScope(Scope scope){
		currentScope = scope;
	}
	public void popScope(){
		currentScope = currentScope.getEnclosingScope();
	}
	public CFile generate(ParserRuleContext tree) {
		CFile file = (CFile)visit(tree);
		return file;
	}
	@Override
	public OutputModelObject visitFile(JParser.FileContext ctx) {

		pushScope(ctx.scope);
		CFile file = new CFile(fileName);
		file.main = (MainMethod)visit(ctx.main());
		for(JParser.ClassDeclarationContext classDeclarationContext : ctx.classDeclaration()){
			OutputModelObject outputModelObject = visit(classDeclarationContext);
			file.classes.add(outputModelObject);
		}
		return file;
	}
	@Override
	public OutputModelObject visitClassDeclaration(JParser.ClassDeclarationContext ctx) {

		pushScope(ctx.scope);

		currentClass = (JClass) currentScope;
		ClassDef classDef = new ClassDef();
		String className = ctx.name.getText();
		myclass = className;
		classDef.ClassName = className;
		for (FieldSymbol f : ctx.scope.getFields()){

			String type = f.getType().getName();
			VarDef varDef;
			if (f.getType() instanceof JPrimitiveType ){
				varDef = new VarDef(type,f.getName());
			}
			else {
				varDef = new VarDef(type+" * ",f.getName());

			}
			classDef.fields.add(varDef);
		}
		for (MethodSymbol m : ctx.scope.getMethods()){
			classDef.funcName.add(m.getName());
			classDef.className.add(m.getScope().getName() + "_" + m.getName());
		}
		for(JParser.ClassBodyDeclarationContext classBodyDeclarationContext : ctx.classBody().classBodyDeclaration())
		{
			OutputModelObject outputModelObject = visit(classBodyDeclarationContext);
			if (outputModelObject instanceof MethodDecl) {
				classDef.methods.add((MethodDecl) outputModelObject);
			}
		}
		int i =0;
		List<vtable> vTable = new ArrayList<>();
		Set<MethodSymbol> methodSymbols = currentClass.getMethods();
		for(MethodSymbol methodSymbol : methodSymbols) classDef.vtable.add(null);
		for(MethodSymbol methodSymbol : methodSymbols) {
			vtable vtable = new vtable();
			vtable.className = new ClassName(className);
			vtable.functionName = new FuncName(methodSymbol.getName());
			classDef.vtable.set(i,vtable);
			vtable.slotNumber = i++;
			vTable.add(vtable);

		}
		popScope();
		return classDef;

	}

	@Override
	public OutputModelObject visitFieldDeclaration(JParser.FieldDeclarationContext ctx) {
		VarDef varDef;
		if(ctx.jType().ID() != null){
			varDef = new VarDef(ctx.jType().getText()+" * ", ctx.ID().getText());
		}
		else {
			varDef = new VarDef(ctx.jType().getText(), ctx.ID().getText());
		}

		return varDef;
	}
	@Override
	public OutputModelObject visitClassBodyDeclaration(JParser.ClassBodyDeclarationContext ctx) {
			if(ctx.methodDeclaration() != null)
			return visit(ctx.methodDeclaration());
		else
			return visit(ctx.fieldDeclaration());
	}
	@Override
	public OutputModelObject visitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		pushScope(ctx.scope);
		MethodDecl methodDecl = new MethodDecl();
		methodDecl.funcName = new FuncName(currentClass.getName()+"_"+ctx.ID().getText());
		try {
			if(ctx.jType().ID()!=null){
				methodDecl.returnType = ctx.jType().ID().getText()+" *";
			}else {
				methodDecl.returnType = ctx.jType().getText();
			}

		}catch (NullPointerException e){
			methodDecl.returnType = "void";
		}
		VarDef varDef = new VarDef(currentClass.getName(),"*this");
		methodDecl.formalParam.add(varDef);
		try{
			for (JParser.FormalParameterContext formalParameterContext : ctx.formalParameters().formalParameterList().formalParameter()){
				OutputModelObject outputModelObject = visit(formalParameterContext);
				methodDecl.formalParam.add((VarDef) outputModelObject);
			}
		}catch (NullPointerException e){
		}
		methodDecl.body = (Block) visit(ctx.methodBody());
		popScope();
		return methodDecl;
	}

	@Override
	public OutputModelObject visitMethodBody(JParser.MethodBodyContext ctx) {
		return visit(ctx.block());
	}

	@Override
	public OutputModelObject visitFormalParameters(JParser.FormalParametersContext ctx) {
		FormalParas formalParas = new FormalParas();
		formalParas.formalParaList = (FormalParaList) visit(ctx.formalParameterList());
		return formalParas;
	}

	@Override
	public OutputModelObject visitFormalParameterList(JParser.FormalParameterListContext ctx) {
		FormalParaList formalParaList = new FormalParaList();
		for(JParser.FormalParameterContext formalParameterContext : ctx.formalParameter()){
			OutputModelObject outputModelObject = visit(formalParameterContext);
			formalParaList.formalParam.add(outputModelObject);
		}

		return formalParaList;
	}

	@Override
	public OutputModelObject visitFormalParameter(JParser.FormalParameterContext ctx) {
		VarDef varDef;
		if(ctx.jType().ID() != null){
			varDef = new VarDef(ctx.jType().getText()+" * ", ctx.ID().getText());
		}
		else {
			varDef = new VarDef(ctx.jType().getText(), ctx.ID().getText());
		}

		return varDef;
	}

	@Override
	public OutputModelObject visitMain(JParser.MainContext ctx) {
		MainMethod mainMethod = new MainMethod();
		mainMethod.body = (Block)visit(ctx.block());

		return mainMethod;
	}
	@Override
	public OutputModelObject visitBlock(JParser.BlockContext ctx) {
		pushScope(ctx.scope);
		Block block = new Block();
		for(JParser.StatementContext statementContext : ctx.statement() ){
			OutputModelObject outputModelObject = visit(statementContext);
			if(outputModelObject instanceof VarDef) {
				block.locals.add(outputModelObject);
			} else {
				block.instrs.add(outputModelObject);
			}
		}
		popScope();
		return block;
	}

	@Override
	public OutputModelObject visitIfStat(JParser.IfStatContext ctx) {
		IfStat ifStat = new IfStat();
		ifStat.condition = (Expr) visit(ctx.parExpression());
		ifStat.stat = (Stat) visit(ctx.statement(0));
		if(ctx.statement(1) != null){
			IfElseStat ifElseStat = new IfElseStat();
			ifElseStat.condition = (Expr) visit(ctx.parExpression());
			ifElseStat.stat = (Stat) visit(ctx.statement(0));
			ifElseStat.elseStat = (Stat) visit(ctx.statement(1));
			return ifElseStat;
		}

		return ifStat;
	}

	@Override
	public OutputModelObject visitParExpression(JParser.ParExpressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public OutputModelObject visitLocalVarStat(JParser.LocalVarStatContext ctx) {
		return visitLocalVariableDeclaration((ctx.localVariableDeclaration()));
	}

	@Override
	public OutputModelObject visitLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {

		VarDef varDef;
		if(ctx.jType().ID() != null){
			varDef = new VarDef(ctx.jType().getText()+" * ", ctx.ID().getText()+";");
		}
		else {
			varDef = new VarDef(ctx.jType().getText(), ctx.ID().getText() + ";");
		}

		return varDef;
	}

	@Override
	public OutputModelObject visitWhileStat(JParser.WhileStatContext ctx) {

		WhileStat whileStat = new WhileStat();
		whileStat.condition = (Expr) visit(ctx.parExpression());
		//whileStat.stat = ctx.statement().getText();
		whileStat.stat = visit(ctx.statement());
		return whileStat;
	}

	@Override
	public OutputModelObject visitBlockStat(JParser.BlockStatContext ctx) {
		System.out.println("block");
		Block block = new Block();
		for (JParser.StatementContext statementContext: ctx.block().statement()){
			block.locals.add(visit(statementContext));
		}

		return block;
	}

	@Override
	public OutputModelObject visitAssignStat(JParser.AssignStatContext ctx) {
		AssignStat assignStat = new AssignStat();//(ctx.getChild(0).getText(),ctx.getChild(2).getText());
		assignStat.left = (Expr) visit(ctx.expression(0));

		if (ctx.expression(0).type instanceof JClass && ctx.expression(1) instanceof JParser.IdRefContext){
			TypeCast typeCast = new TypeCast(new ObjectTypeSpec(ctx.expression(0).type.getName()));
			typeCast.expr = (Expr) visit(ctx.expression(1));
			assignStat.right =typeCast;

		} else {
			assignStat.right =(Expr) visit(ctx.expression(1));
		}

		return assignStat;
	}

	@Override
	public OutputModelObject visitIdRef(JParser.IdRefContext ctx) {
		if(currentScope.resolve(ctx.ID().getText()) instanceof  JField){
			FieldRef fieldRef = new FieldRef(ctx.ID().getText());
			fieldRef.object = new ThisRef();
			return  fieldRef;
		}
		return new VarRef(ctx.ID().getText());
	}

	@Override
	public OutputModelObject visitIntRef(JParser.IntRefContext ctx) {
		IntRef intRef = new IntRef(ctx.getText());
		intRef.IntName=ctx.getText();
		return intRef;
	}

	@Override
	public OutputModelObject visitFloatRef(JParser.FloatRefContext ctx) {
		FloatRef floatRef = new FloatRef(ctx.getText());
		return floatRef;
	}

	@Override
	public OutputModelObject visitPrintStringStat(JParser.PrintStringStatContext ctx) {
		PrintStringStat printStringStat = new PrintStringStat(ctx.STRING().getText());

		return printStringStat;
	}

	@Override
	public OutputModelObject visitPrintStat(JParser.PrintStatContext ctx) {
	PrintStat printStat = new PrintStat(ctx.STRING().getText());

		for(JParser.ExpressionContext expressionContext : ctx.expressionList().expression() ){
			OutputModelObject ar = visit(expressionContext);
			printStat.args.add(ar); //expressionContext.getText();
		}
		return printStat;
	}

	@Override
	public OutputModelObject visitCallStat(JParser.CallStatContext ctx) {

		CallStat callStat = new CallStat();
		callStat.call = visit(ctx.expression());

		return callStat;
	}

	@Override
	public OutputModelObject visitThisRef(JParser.ThisRefContext ctx) {
		ThisRef thisRef = new ThisRef();
		return thisRef;
	}

	@Override
	public OutputModelObject visitCtorCall(JParser.CtorCallContext ctx) {
	CtorCall ctorCall = new CtorCall(ctx.ID().getText());
		return ctorCall;
	}

	@Override
	public OutputModelObject visitReturnStat(JParser.ReturnStatContext ctx) {
		ReturnStat returnStat = new ReturnStat();
		returnStat.expr = (Expr) visit(ctx.expression());
		return returnStat;
	}

	@Override
	public OutputModelObject visitFieldRef(JParser.FieldRefContext ctx) {
		FieldRef fieldRef = new FieldRef(ctx.ID().getText());
		fieldRef.object = (Expr) visit(ctx.expression());
		return fieldRef;
	}

	@Override
	public OutputModelObject visitQMethodCall(JParser.QMethodCallContext ctx) {

		MethodCall methodCall = new MethodCall();
		FieldRef fieldRef = new FieldRef(ctx.ID().getText());
		Expr expr = (Expr) visit(ctx.expression());
		if(expr instanceof VarRef){
			fieldRef.object = (VarRef) visit(ctx.expression());
		} else if (expr instanceof ThisRef){
			fieldRef.object = new ThisRef();
		} else {
			fieldRef.object = (FieldRef) visit(ctx.expression());
		}

		Scope scope = currentScope.getEnclosingScope().getEnclosingScope();
		scope.getName();
		Type type = ctx.expression().type;
		String text = ctx.expression().getText();

		if (text.equals("this")){
			currentScope =currentScope.getEnclosingScope();
			JClass jClass = (JClass) currentScope.resolve(scope.getName());
			String rect = ((JMethod) jClass.resolve(fieldRef.name)).getEnclosingScope().getName();
			methodCall.receiverType = new ObjectTypeSpec(rect);
			methodCall.receiver = fieldRef.object;
			methodCall.methodName = fieldRef.name;
			methodCall.currentClass = scope.getName();
			System.out.println(jClass.getScope()+"==this="+scope.getName()+fieldRef.name);

		} else {
			JClass jClass = (JClass) currentScope.resolve(type.getName());
			String rect = ((JMethod) jClass.resolve(fieldRef.name)).getEnclosingScope().getName();
			methodCall.receiverType = new ObjectTypeSpec(rect);
			methodCall.receiver = fieldRef.object;
			methodCall.methodName = fieldRef.name;
			methodCall.className = rect;
			methodCall.currentClass=type.getName();

		}
		if(ctx.type instanceof JPrimitiveType){
			methodCall.fptrType.returnType = new PrimitiveTypeSpec(ctx.type.getName());
		} else {
			methodCall.fptrType.returnType = new ObjectTypeSpec(ctx.type.getName());
		}

		TypeCast typeCast = new TypeCast(methodCall.receiverType);
		typeCast.expr = methodCall.receiver;
		methodCall.args.add(typeCast);
		methodCall.fptrType.argTypes.add(methodCall.receiverType);

		if(ctx.expressionList() == null) {
			return methodCall;
		}
		argTypePrinting(methodCall);
		argsPrinting(ctx, methodCall);
		return methodCall;
	}

	@Override
	public OutputModelObject visitMethodCall(JParser.MethodCallContext ctx) {
		MethodCall methodCall = new MethodCall();
		String f = ctx.ID().getText();
		methodCall.receiver = new ThisRef();
		JClass c =null;
		Scope scope=currentScope;
		while (scope != null) {
			if(scope instanceof JClass) {
				c = (JClass) scope;
				break;
			}
			else
				scope = scope.getEnclosingScope();
		}
		JMethod m = (JMethod) c.resolve(f);
		methodCall.receiverType = new ObjectTypeSpec(m.getEnclosingScope().getName());
		methodCall.methodName = f;//new FuncName(f);
		methodCall.currentClass = c.getName();

		if(ctx.type instanceof JPrimitiveType){
			methodCall.fptrType.returnType = new PrimitiveTypeSpec(ctx.type.getName());
		} else {
			methodCall.fptrType.returnType = new ObjectTypeSpec(ctx.type.getName());
		}
		TypeCast thisArg = new TypeCast(methodCall.receiverType);
		thisArg.expr = methodCall.receiver;
		methodCall.args.add(thisArg);
		methodCall.fptrType.argTypes.add(methodCall.receiverType);

		if(ctx.expressionList() == null) {
			return methodCall;
		}
		argTypePrinting(methodCall);
		return methodCall;
	}

	private void argTypePrinting(MethodCall methodCall) {

		methodCall.fptrType.argTypes.clear();
		JClass jClass = (JClass) currentScope.resolve(methodCall.receiverType.typeName);
		JMethod jMethod = (JMethod) jClass.getSymbol(methodCall.methodName);
		List<? extends Symbol> parameters = jMethod.getSymbols();
		for(Symbol symbol : parameters) {
			TypeSpec typeSpec;
			if(((JArg)symbol).getType() instanceof JPrimitiveType){
				typeSpec = new PrimitiveTypeSpec(((JArg)symbol).getType().getName());
			} else {
				typeSpec = new ObjectTypeSpec(((JArg)symbol).getType().getName());
			}
			methodCall.fptrType.argTypes.add(typeSpec);
		}
	}

	private void argsPrinting(JParser.QMethodCallContext ctx, MethodCall methodCall) {
		for(JParser.ExpressionContext expressionContext : ctx.expressionList().expression()){
			Expr expr = (Expr)visit(expressionContext);
			TypeSpec typeSpec = null;
			if((expressionContext instanceof JParser.IdRefContext)){
				typeSpec = new ObjectTypeSpec(expressionContext.type.getName());
				TypeCast typeCast= new TypeCast(typeSpec);
				typeCast.expr = expr;
				methodCall.args.add(typeCast);

			}else
				methodCall.args.add(expr);
		}
	}


















}
