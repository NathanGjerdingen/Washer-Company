import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;

// Test Test Test

/**
 * 
 * This class implements the user interface for the Library project. The
 * commands are encoded as integers using a number of static final variables. A
 * number of utility methods exist to make it easier to parse the input.
 *
 */
public class UserInterface {
	private static UserInterface userInterface;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static WasherCompany washerCompany;
	private static final int EXIT = 0;
	private static final int ADD_CUSTOMER = 1;
	private static final int ADD_WASHER = 2;
	private static final int ADD_INVENTORY = 3;
	private static final int PURCHASE = 4;
	private static final int LIST_CUSTOMERS = 5;
	private static final int LIST_WASHERS = 6;
	private static final int DISPLAY_TOTAL = 7;
	private static final int SAVE = 8;
	private static final int HELP = 9;

	/**
	 * Made private for singleton pattern. Conditionally looks for any saved data.
	 * Otherwise, it gets a singleton Library object.
	 */
	private UserInterface() {
		if (yesOrNo("Look for saved data and  use it?")) {
			retrieve();
		} else {
			washerCompany = WasherCompany.instance();
		}
	}

	/**
	 * Supports the singleton pattern
	 * 
	 * @return the singleton object
	 */
	public static UserInterface instance() {
		if (userInterface == null) {
			return userInterface = new UserInterface();
		} else {
			return userInterface;
		}
	}

	/**
	 * Gets a token after prompting
	 * 
	 * @param prompt
	 *            - whatever the user wants as prompt
	 * @return - the token from the keyboard
	 * 
	 */
	public String getToken(String prompt) {
		do {
			try {
				System.out.println(prompt);
				String line = reader.readLine();
				StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
				if (tokenizer.hasMoreTokens()) {
					return tokenizer.nextToken();
				}
			} catch (IOException ioe) {
				System.exit(0);
			}
		} while (true);
	}

	/**
	 * Queries for a yes or no and returns true for yes and false for no
	 * 
	 * @param prompt
	 *            The string to be prepended to the yes/no prompt
	 * @return true for yes and false for no
	 * 
	 */
	private boolean yesOrNo(String prompt) {
		String more = getToken(prompt + " (Y|y)[es] or anything else for no");
		if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
			return false;
		}
		return true;
	}

	/**
	 * Converts the string to a number
	 * 
	 * @param prompt
	 *            the string for prompting
	 * @return the integer corresponding to the string
	 * 
	 */
	public int getNumber(String prompt) {
		do {
			try {
				String item = getToken(prompt);
				Integer number = Integer.valueOf(item);
				return number.intValue();
			} catch (NumberFormatException nfe) {
				System.out.println("Please input a number ");
			}
		} while (true);
	}

	/**
	 * Prompts for a date and gets a date object
	 * 
	 * @param prompt
	 *            the prompt
	 * @return the data as a Calendar object
	 */
	public Calendar getDate(String prompt) {
		do {
			try {
				Calendar date = new GregorianCalendar();
				String item = getToken(prompt);
				DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
				date.setTime(dateFormat.parse(item));
				return date;
			} catch (Exception fe) {
				System.out.println("Please input a date as mm/dd/yy");
			}
		} while (true);
	}

	/**
	 * Prompts for a command from the keyboard
	 * 
	 * @return a valid command
	 * 
	 */
	public int getCommand() {
		do {
			try {
				int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
				if (value >= EXIT && value <= HELP) {
					return value;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Enter a number");
			}
		} while (true);
	}

	/**
	 * Displays the help screen
	 * 
	 */
	public void help() {
		System.out.println("Enter a number between 0 and 12 as explained below:");
		System.out.println(EXIT + " to Exit\n");
		System.out.println(ADD_CUSTOMER + " to add a member");
		System.out.println(ADD_WASHER + " to  add books");
		System.out.println(ADD_INVENTORY + " to  issue books to a  member");
		System.out.println(PURCHASE + " to  return books ");
		System.out.println(LIST_CUSTOMERS + " to  renew books ");
		System.out.println(LIST_WASHERS + " to  remove books");
		System.out.println(DISPLAY_TOTAL + " to  place a hold on a book");
		System.out.println(SAVE + " to  save data");
		System.out.println(HELP + " for help");
	}

	/**
	 * Method to be called for adding a member. Prompts the user for the appropriate
	 * values and uses the appropriate Library method for adding the member.
	 * 
	 */
	public void addCustomer() {
		String name = getToken("Enter customer name");
		String phone = getToken("Enter phone number");
		Customer result;
		result = washerCompany.addCustomer(name, phone);
		if (result == null) {
			System.out.println("Could not add customer");
		}
		System.out.println(result);
	}

	/**
	 * Method to be called for adding a book. Prompts the user for the appropriate
	 * values and uses the appropriate Library method for adding the book.
	 * 
	 */
	public void addWashers() {
		Washer result;
		do {
			String brand = getToken("Enter brand");
			String model = getToken("Enter model");
			double price = getNumber("Enter price");
			result = washerCompany.addWasher(brand, model, price);
			if (result != null) {
				System.out.println(result);
			} else {
				System.out.println("Washer could not be added");
			}
		} while (yesOrNo("Add more washers?"));
	}

	/**
	 * Add comment
	 */
	public void addInventory() {
		int result;
		do {
			String brand = getToken("Enter washer brand");
			String model = getToken("Enter washer model");
			int quantity = getNumber("Enter quantity");
			result = washerCompany.addInventory(brand, model, quantity);
			switch (result) {
			case WasherCompany.WASHER_NOT_FOUND:
				System.out.println("No such washer in inventory");
				break;
			case WasherCompany.OPERATION_FAILED:
				System.out.println("Washer quantity could not be updated.");
				break;
			case WasherCompany.OPERATION_COMPLETED:
				System.out.println("Washer quantity was updated.");
				break;
			default:
				System.out.println("An error has occurred.");
			}
			if (!yesOrNo("Add more quantity for other washers?")) {
				break;
			}
		} while (true);
	}

	/**
	 * Method to be called for purchasing washers. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for issuing books.
	 * 
	 */
	public void purchase() {
		Washer result;
		String customerId = getToken("Enter member id");
		if (washerCompany.searchCustomer(customerId) == null) {
			System.out.println("No such customer");
			return;
		}
		do {
			String brand = getToken("Enter washer brand");
			String model = getToken("Enter washer model");
			int quantity = getNumber("Enter quantity");
			result = washerCompany.purchaseWasher(customerId, brand, model, quantity);
			if (result != null) {
				System.out.println("Washer successfully purchased.");
			} else {
				System.out.println("Purchase could not be completed.");
			}
		} while (yesOrNo("Buy more washers?"));
	}

	public void displayCustomerList() {
		Iterator result = washerCompany.listCustomers();
		if (result == null) {
			System.out.println("No customers to print");
		} else {
			while (result.hasNext()) {
				CustomerList customerList = (CustomerList) result.next();
				System.out.println(customerList.getCustomer());
			}
		}
	}

	public void displayWasherList() {
		Iterator result = washerCompany.listWashers();
		if (result == null) {
			System.out.println("No washer to print");
		} else {
			while (result.hasNext()) {
				WasherList washerList = (WasherList) result.next();
				System.out.println(washerList.getWasher());
			}
		}
	}

	public void displayTotalSales() {
		washerCompany.displayTotal();
	}

	/**
	 * Method to be called for saving the Library object. Uses the appropriate
	 * Library method for saving.
	 * 
	 */
	private void save() {
		if (washerCompany.save()) {
			System.out.println(" The library has been successfully saved in the file LibraryData \n");
		} else {
			System.out.println(" There has been an error in saving \n");
		}
	}

	/**
	 * Method to be called for retrieving saved data. Uses the appropriate Library
	 * method for retrieval.
	 * 
	 */
	private void retrieve() {
		try {
			if (washerCompany == null) {
				washerCompany = WasherCompany.retrieve();
				if (washerCompany != null) {
					System.out.println(" The library has been successfully retrieved from the file LibraryData \n");
				} else {
					System.out.println("File doesnt exist; creating new library");
					washerCompany = WasherCompany.instance();
				}
			}
		} catch (Exception cnfe) {
			cnfe.printStackTrace();
		}
	}

	/**
	 * Orchestrates the whole process. Calls the appropriate method for the
	 * different functionalities.
	 * 
	 */
	public void process() {
		int command;
		help();
		while ((command = getCommand()) != EXIT) {
			switch (command) {
			case ADD_CUSTOMER:
				addCustomer();
				break;
			case ADD_WASHER:
				addWashers();
				break;
			case ADD_INVENTORY:
				addInventory();
				break;
			case PURCHASE:
				purchase();
				break;
			case LIST_WASHERS:
				displayWasherList();
				break;
			case LIST_CUSTOMERS:
				displayCustomerList();
				break;
			case DISPLAY_TOTAL:
				displayTotalSales();
				break;
			case SAVE:
				save();
				break;
			case HELP:
				help();
				break;
			}
		}
	}

	/**
	 * The method to start the application. Simply calls process().
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		UserInterface.instance().process();
	}
}
