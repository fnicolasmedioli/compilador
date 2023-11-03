package compiler;

public class Source {

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Falta al parametro indicando el nombre de archivo");
            return;
        }

        String sourceFileName = args[0];

        Compiler compiler;

        try {
            compiler = new Compiler(sourceFileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        compiler.compile();
    }
}
