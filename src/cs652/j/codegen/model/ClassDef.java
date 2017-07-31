package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/1/2017.
 */
public class ClassDef extends OutputModelObject {

    public @ModelElement String ClassName;
    public @ModelElement List<String> funcName = new ArrayList<>();
    public @ModelElement List<String> className = new ArrayList<>();

    public @ModelElement List<VarDef> fields = new ArrayList<>();
    public @ModelElement List<MethodDecl> methods = new ArrayList<>();
    public @ModelElement List<vtable> vtable = new ArrayList<>();
    public @ModelElement ClassBody classBody;

    //public @ModelElement MethodDecl methods;


}
