package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/2/2017.
 */
public class PrintStat extends Stat {

    public @ModelElement String string;

   public @ModelElement List<OutputModelObject> args = new ArrayList<>();
    // public @ModelElement
   //List<String> args;

    public PrintStat(String string) {
        this.string = string;
    }
}
