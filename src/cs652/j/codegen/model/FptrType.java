package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class FptrType extends OutputModelObject {

    @ModelElement public TypeSpec returnType;
    @ModelElement public List<TypeSpec> argTypes = new ArrayList<>();
}
