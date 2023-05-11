package com.cg.bankapp.util;

/**
 * This class contains the constant values used for file printing on console.
 */
public final class ConsoleFileConstants {
	private static final String CONSOLE_RESOURCES_ROOT_DIRECTORY = "src/main/resources/console/";
	public static final String PRINT_MENU_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-menu.txt";
	public static final String PRINT_TITLE_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-title.txt";
	public static final String PRINT_TXN_DETAILS_HEADER_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY
			+ "bsa-txn_details_header.txt";
	public static final String PRINT_TXN_DETAILS_FOOTER_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY
			+ "bsa-txn_details_footer.txt";
	public static final String PRINT_EXIT_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-exit.txt";
	public static final String PRINT_ERROR_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-error.txt";
	public static final String PRINT_UNEXPECTED_ERROR_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY
			+ "bsa-unexpected_error.txt";
	public static final String PRINT_SHOWBALANCE_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-show_balance.txt";
	public static final String PRINT_DEPOSIT_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-deposit_txn.txt";
	public static final String PRINT_WITHDRAW_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY + "bsa-withdraw_txn.txt";
	public static final String PRINT_FUNDTRANSFER_SUCCESS_FILEPATH = CONSOLE_RESOURCES_ROOT_DIRECTORY
			+ "bsa-fund_transfer_success.txt";

	private ConsoleFileConstants() {
	}
}
