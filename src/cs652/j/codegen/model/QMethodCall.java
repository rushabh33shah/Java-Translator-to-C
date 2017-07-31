package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class QMethodCall extends Expr{

    @ModelElement public Expr receiver;
    @ModelElement public FptrType fptrType;
    @ModelElement public List<OutputModelObject> args = new ArrayList<>();
    @ModelElement public String className;
    @ModelElement public ObjectTypeSpec receiverType;
    public String methodName;

}
