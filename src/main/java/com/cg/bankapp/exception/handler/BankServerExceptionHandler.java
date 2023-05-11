package com.cg.bankapp.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.BankServerException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.NoTransactionsFoundException;

/**
 * This class is a global exception handler for BankServer application. It
 * handles various types of exceptions and returns the appropriate HTTP response
 * code and message.
 */
@ControllerAdvice
public class BankServerExceptionHandler {

	/**
	 * Handles AccountBalanceException and returns HTTP response with error message
	 * and BAD_REQUEST status code.
	 * 
	 * @param e the exception object of AccountBalanceException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = AccountBalanceException.class)
	public ResponseEntity<String> handleAccountBalanceException(AccountBalanceException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles AccountNotFoundException and returns HTTP response with error message
	 * and NOT_FOUND status code.
	 * 
	 * @param e the exception object of AccountNotFoundException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = AccountNotFoundException.class)
	public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles InvalidAmountException and returns HTTP response with error message
	 * and BAD_REQUEST status code.
	 * 
	 * @param e the exception object of InvalidAmountException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = InvalidAmountException.class)
	public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles InvalidFundTransferException and returns HTTP response with error
	 * message and BAD_REQUEST status code.
	 * 
	 * @param e the exception object of InvalidFundTransferException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = InvalidFundTransferException.class)
	public ResponseEntity<String> handleInvalidFundTransferException(InvalidFundTransferException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles NoTransactionsFoundException and returns HTTP response with error
	 * message and NO_CONTENT status code.
	 * 
	 * @param e the exception object of NoTransactionsFoundException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = NoTransactionsFoundException.class)
	public ResponseEntity<String> handleNoTransactionsFoundException(NoTransactionsFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
	}

	/**
	 * Handles MethodArgumentTypeMismatchException and returns HTTP response with
	 * error message and BAD_REQUEST status code.
	 * 
	 * @param e the exception object of MethodArgumentTypeMismatchException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleInvalidClientInput(MethodArgumentTypeMismatchException e) {
		return new ResponseEntity<>("Provided input is of invalid format.", HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles BankServerException and returns HTTP response with error message and
	 * INTERNAL_SERVER_ERROR status code.
	 * 
	 * @param e the exception object of BankServerException
	 * @return the HTTP response object with error message and status code
	 */
	@ExceptionHandler(value = BankServerException.class)
	public ResponseEntity<String> handleBankServerException(BankServerException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}