import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class List {

    private Scanner scanner;
    Map<String, java.util.List<Product>> shoppingList = new LinkedHashMap<>();

    public List(String ListName) {
        File file = new File(ListName);
        boolean exist = file.exists();
        if (!exist) {
            System.out.println("Nie znaleiono listy zakupów!");
            System.exit(1);
        }
        LoadShoppingList(file, ListName);
    }

    private void LoadShoppingList(File file, String ListName) {
        try {
            this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Wczytywanie listy zakupów nie powiodło się!");
            System.exit(2);
        }

        String category = null;
        String productLine;
        java.util.List<Product> products = new ArrayList<>();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (Character.isWhitespace(line.charAt(0))) {
                // produkt
                if (category == null) {
                    System.out.println("Wczytywanie listy zakupów nie powiodło się!");
                    System.exit(2);
                }
                productLine = line.trim();
                String product = null;
                double quantity = 0;
                String unit = null;
                try {
                    String[] productParts = productLine.split(" - ");
                    if(productParts.length != 3)
                    {
                        System.out.println("Wczytywanie listy zakupów nie powiodło się!");
                        System.exit(2);
                    }
                    product = productParts[0];
                    quantity = Double.parseDouble(productParts[1]);
                    unit = productParts[2];
                } catch (NumberFormatException e) {
                    System.out.println("Wczytywanie listy zakupów nie powiodło się!");
                    System.exit(2);
                }
                Product newProduct = new Product(product, category, quantity, unit);
                products.add(newProduct);
            } else {
                // kategoria
                if (category != null) {
                    shoppingList.put(category, new ArrayList<>(products));
                    products.clear();
                }
                category = line;
            }
        }
        if (category != null) {
            shoppingList.put(category, new ArrayList<>(products));
        }
        System.out.println("Lista zakupów wczytana poprawnie.");
        SelectOption(ListName);
    }

    public void SelectOption(String ListName) {
        this.scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Dodaj produkt do listy zakupów");
            System.out.println("2. Wyświetl wszystkie produkty z listy zakupów");
            System.out.println("3. Wyświetl produkty z danej kategorii");
            System.out.println("4. Usuń wszystkie produkty z listy zakupów");
            System.out.println("5. Usuń wszystkie produkty z danej kategorii");
            System.out.println("6. Usuń produkt z listy zakupów");
            System.out.println("7. Zapisz listę zakupów na dysku");
            System.out.println("8. Wyjdź");

            System.out.print("Wybierz opcję: ");
            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("BŁĄD! Wybierz opcję od 1 do 8.");
                continue;
            }

            switch (option) {
                case 1 -> addProduct(scanner);
                case 2 -> displayAllProducts();
                case 3 -> displayProductsByCategory(scanner);
                case 4 -> removeAllProducts();
                case 5 -> removeProductsByCategory(scanner);
                case 6 -> removeProduct(scanner);
                case 7 -> saveShoppingList(ListName);
                case 8 -> exit(ListName);
                default -> System.out.println("BŁĄD! Wybierz opcję od 1 do 8.");
            }
        }
    }

    private String getCategory(Scanner scanner) {
        System.out.println("Dostępne kategorie:");
        shoppingList.keySet().forEach(category -> System.out.println(">" + category));
        System.out.println("Wybierz kategorię:");
        return scanner.nextLine();
    }

    private void addProduct(Scanner scanner) {
        String category = displayProductsByCategory(scanner);
        java.util.List<Product> products = shoppingList.get(category);
        System.out.print("Wybierz produkt (FORMAT: produkt - liczebność - jednostak liczebności): ");
        String productLine = scanner.nextLine();

        String product = null;
        double quantity = 0;
        String unit = null;

        try {
            String[] productParts = productLine.split(" - ");
            if(productParts.length != 3)
            {
                System.out.println("Błędny format produktu!");
                System.exit(2);
            }
            product = productParts[0];
            quantity = Double.parseDouble(productParts[1]);
            unit = productParts[2];
        } catch (NumberFormatException e) {
            System.out.println("Niepoprawna liczebność produktu!");
            System.exit(2);
        }

        int exist = 0;
        for (java.util.List<Product> listValues : shoppingList.values()) {
            for(Product selectedProduct : listValues)
            {
                if(selectedProduct.name.equals(product))
                {
                    exist++;
                    System.out.println("Produkt " + product + " już istnieje.");
                    while(true)
                    {
                        System.out.println("Podaj nową liczebnośc produktu: ");
                        String newQuantity = scanner.nextLine();
                        try {
                            quantity = Double.parseDouble(newQuantity);
                        } catch (NumberFormatException e) {
                            System.out.println("Niepoprawna liczebność produktu!");
                            continue;
                        }
                        selectedProduct.quantity = quantity;
                        break;
                    }
                }
            }
        }

        if(exist > 0)
        {
            return;
        }

        if (products == null) {
            Product newProduct = new Product(product, category, quantity, unit);
            java.util.List<Product> newProducts = new ArrayList<>();
            newProducts.add(newProduct);
            shoppingList.put(category, new ArrayList<>(newProducts));
            System.out.println("Produkt dodany pomyślnie.");
            return;
        }
        products.add(new Product(product, category, quantity, unit));
        System.out.println("Produkt dodany pomyślnie.");
    }

    private void displayAllProducts() {
        System.out.println("Lista zakupów:");
        for (java.util.List<Product> products : shoppingList.values()) {
            int category_counter = 0;
            for (Product product : products) {
                if (category_counter == 0) {
                    System.out.println(product.category + ":");
                    category_counter++;
                }
                System.out.println(">" + product.name + " - " + product.quantity + " - " + product.unit);
            }
        }
    }

    private String displayProductsByCategory(Scanner scanner) {
        String category = getCategory(scanner);
        java.util.List<Product> products = shoppingList.get(category);
        if (products != null) {
            System.out.println("Dostępne produkty w kategorii " + category + ":");
            products.forEach(product -> System.out.println(">" + product.name + " - " + product.quantity + " - " + product.unit));
        } else {
            System.out.println("Brak kategorii o nazwie " + category + ".");
        }
        return category;
    }

    private void removeAllProducts() {
        shoppingList.clear();
        System.out.println("Lista wyczyszczona pomyślnie.");
    }

    private void removeProductsByCategory(Scanner scanner) {
        String category = getCategory(scanner);
        java.util.List<Product> products = shoppingList.remove(category);
        if (products != null) {
            System.out.println("Wszystkie produkty z kategorii " + category + " usunięte pomyślnie.");
        } else {
            System.out.println("Brak kategorii o nazwie " + category + ".");
        }
    }

    private void removeProduct(Scanner scanner) {
        String category = getCategory(scanner);
        java.util.List<Product> products = shoppingList.get(category);
        if (products != null) {
            System.out.println("Dostępne produkty w kategorii " + category + ":");
            products.forEach(product -> System.out.println(">" + product.name + " - " + product.quantity + " - " + product.unit));
            System.out.print("Wybierz produkt do usunięcia: ");
            String productName = scanner.nextLine();
            boolean removed = products.removeIf(product -> product.name.equals(productName));
            if (removed) {
                System.out.println("Produkt " + productName + " usunięty pomyślnie.");
            } else {
                System.out.println("Nie istnieje produkt o podanej nazwie.");
            }
        } else {
            System.out.println("Brak kategorii o nazwie " + category + ".");
        }
    }

    private void saveShoppingList(String ListName) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(ListName);
            for (java.util.List<Product> products : shoppingList.values()) {
                int category_counter = 0;
                for (Product product : products) {
                    if (category_counter == 0) {
                        writer.println(product.category);
                        category_counter++;
                    }
                    writer.println("\t" + product.name + " - " + product.quantity + " - " + product.unit);
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Lista zakupów zapisana pomyślnie.");
    }

    private void exit(String ListName) {
        saveShoppingList(ListName);
        System.exit(0);
    }
}
