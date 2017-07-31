package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class MethodCall extends Expr{

    @ModelElement public Expr receiver;
    @ModelElement public FptrType fptrType = new FptrType();
    @ModelElement public List<Expr> args = new ArrayList<>();
    @ModelElement public String className;
    @ModelElement public String methodName;
    @ModelElement public ObjectTypeSpec receiverType;
    public String currentClass;

}
