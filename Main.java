public class Main {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Nie znaleiono listy zakupów!");
            System.exit(1);
        }

        String ListName = args[0];
        new List(ListName);
    }
}
