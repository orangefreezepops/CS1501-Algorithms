import java.util.*;
import java.io.*;

public class AptTracker{

  private static AptPQ pq;

  public static void printMenu(){
    System.out.println("\nUse the number pad to select one of the following:\n" +
      "\n    1. Add an Apartment\n    2. Update an Apartment\n    3. Remove an Apartment\n" +
      "    4. Retrieve Lowest Rent Apartment\n    5. Retrieve Highest Square Footage Apartment" +
      "\n    6. Retrieve Lowest Rent Apartment by City\n    7. Retrieve Highest Square Footage Apartment by City\n" +
      "    0. Quit the program");
  }

  public static void run(){
    Scanner input = new Scanner(System.in);
    String option = input.nextLine();

    switch(option){
      case "1": //add new aparment
        System.out.print("\nWhat is the address of the apartment? ");
        String address = input.nextLine();
        System.out.print("\nWhat is the apartment number? ");
        String unitNum = input.nextLine();
        System.out.print("\nWhat city is the apartment in? ");
        String city = input.nextLine();
        System.out.print("\nWhat is the the ZIP code? ");
        int zip = Integer.parseInt(input.nextLine());
        System.out.print("\nWhat is the monthly rent of the apartment? ");
        double cost = Double.parseDouble(input.nextLine());
        System.out.print("\nWhat is the square footage of the apartment? ");
        int sqFeet = Integer.parseInt(input.nextLine());
        Apartment newApt = new Apartment(address, unitNum, city, zip, cost, sqFeet);
        System.out.println("\nAwesome, we're adding the apartment to your queue!");
        pq.add(newApt);
        break;
      case "2": //update an apartments price
        find(0);
        break;
      case "3": //remove an apartment form the PQ
        find(1);
        break;
      case "4": //retrieve the lowest rent apartment
        System.out.println(pq.getMinPrice());
        break;
      case "5": //retrieve highest sq footage apartment
        System.out.println(pq.getMaxSqFeet());
        break;
      case "6": //retrieve lowest rent by city
        System.out.println("What city would you like to retrieve from?");
        String cityMin = input.nextLine();
        System.out.println(pq.getMinPrice(cityMin));
        break;
      case "7": //retrieve highest square footage by city
        System.out.println("What city would you like to retrieve from?");
        String cityMax = input.nextLine();
        System.out.println(pq.getMaxSqFeet(cityMax));
        break;
      case "0": //quit
        System.out.println("Thank you, goodbye!");
        System.exit(0);
      default: //bad input try again
        System.out.println("That isn't a recognized command. Here's the menu again...");
        printMenu();
        break;
    }
  }

  public static void find(int reason){
    Scanner input = new Scanner(System.in);
    System.out.print("\nWhat is the address of the apartment? ");
    String address = input.nextLine();
    System.out.print("\nWhat is the apartment number? ");
    String unitNum = input.nextLine();
    System.out.print("\nWhat is the the ZIP code? ");
    int zip = Integer.parseInt(input.nextLine());

    Apartment found = new Apartment(address, unitNum, null, zip, -1, -1); //apartment object to edit or remove

    if (reason == 0){ //edit apartment
      System.out.print("Awesome, would you like to update the price? y/n ");
      String update = input.nextLine();
      if (update.equals("y")){
        System.out.print("What would you like the new price to be? ");
        double newPrice = Double.parseDouble(input.nextLine());
        found.setCost(newPrice);
        if (pq.edit(found)){
          System.out.println("\nThe price has been updated!");
        } else {
          System.out.println("\nSorry there was no apartment with that address to update");
        }
      }
      if (update.equals("n")){
        System.out.println("\nalrighty then, nevermind.");
      } else {
        System.out.println("\nunknown command, defaulting to n");
      }
    } else if (reason == 1){ //removal
      if (pq.remove(found)){
        System.out.println("\nThe apartment has been removed!");
      } else {
        System.out.println("\nSorry there was no apartment with that address to delete");
      }
    }
  }


  public static void main(String[] args) throws IOException {
    pq = new AptPQ(); //priority queue

    File aptFile = new File("apartments.txt"); //beginning apartment file

    //read the file and add all of the data tot he PQ
    Scanner scanner = new Scanner(aptFile);
    scanner.nextLine(); //read the first line bc it's a throwaway
    String line = null;
    //parse the string up until each colon
    //and add the values to their corresponding apartment properties
    while(scanner.hasNextLine()){ //read the full line
      line = scanner.nextLine();  //current line

      //parse the string up until each colon
      //and add the values to their corresponding apartment properties
      Scanner lineScan = new Scanner(line); //scan the current line
      lineScan.useDelimiter(":");           //use delimiter colon

      String address = lineScan.next();                   //1st scan address
      //System.out.println(address);
      String aptNum = lineScan.next();                    //2nd scan apartment #
      //System.out.println(aptNum);
      String city = lineScan.next();                      //3rd scan is City
      //System.out.println(city);
      int zip = Integer.parseInt(lineScan.next());        //4th scan is zipCode
      //System.out.println(zip);
      double price = Double.parseDouble(lineScan.next()); //5th is price
      //System.out.println(price);
      int sqFt = Integer.parseInt(lineScan.next());       //6th is square footage
      //System.out.println(sqFt);

      Apartment newApt = new Apartment(address, aptNum, city, zip, price, sqFt);
      pq.add(newApt);
    }


    Scanner input = new Scanner(System.in);
    System.out.println("\nHello! welcome to the apartment tracker application! what would you like to do?\n");
    System.out.println("Menu - m\nQuit - <any other key>\n");
    String response = input.nextLine();
    while (!response.equals("q")){
      if (response.equals("m")){
        printMenu();
        run();
      }
    }

    System.out.println("Thank you, goodbye!");
    System.exit(0);
  }

}
