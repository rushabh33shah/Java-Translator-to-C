package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/2/2017.
 */
public class MethodDecl extends OutputModelObject {

    public @ModelElement FuncName funcName;
    public @ModelElement String returnType;
    //public @ModelElement VarDef formalParam;
    public @ModelElement List<VarDef> formalParam = new ArrayList<>();
    public @ModelElement Block body;

}
