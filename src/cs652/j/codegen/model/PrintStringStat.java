package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/2/2017.
 */
public class PrintStringStat extends Stat {

    public @ModelElement String string;

    public PrintStringStat(String string) {
        this.string = string;
    }
}
