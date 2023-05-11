package com.cg.bankapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.cg.bankapp.dto.Account;
import com.cg.bankapp.dto.Customer;
import com.cg.bankapp.dto.InterAccountTransactionRequest;
import com.cg.bankapp.dto.SingleAccountTransactionRequest;
import com.cg.bankapp.dto.Transaction;
import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.NoTransactionsFoundException;
import com.cg.bankapp.exception.TransactionFailedException;
import com.cg.bankapp.service.BankService;
import com.cg.bankapp.utils.AccountType;
import com.cg.bankapp.utils.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BankController.class)
class BankControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BankService bankService;

	@Test
	void testHomeRoute() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/v1/").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals("Bank Server REST API is running.", responseContent);
	}

	@Test
	void testHandleCreateAccount_SaveValidCustomer() throws Exception {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Test Customer");

		Account expectedAccount = new Account();
		expectedAccount.setAccountNo(1L);
		expectedAccount.setAccountType(AccountType.SAVINGS);
		expectedAccount.setCustomer(customer);
		expectedAccount.setAccountBalance(0.0);

		when(bankService.createAccount(any(Customer.class))).thenReturn(expectedAccount);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc.perform(post("/api/v1/account/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(customer))).andExpect(status().isCreated()).andReturn();

		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).createAccount(any(Customer.class));
	}

	@Test
	void testHandleCreateAccount_SaveValidCustomerFails() throws Exception {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Test Customer");

		when(bankService.createAccount(any(Customer.class))).thenThrow(TransactionFailedException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(customer)))
				.andExpect(status().isInternalServerError()).andReturn();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).createAccount(any(Customer.class));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	void testHandleShowBalance_InvalidAccountNo_ShouldThrowException(Long accountNo) throws Exception {
		when(bankService.showBalance(accountNo)).thenThrow(AccountNotFoundException.class);

		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + accountNo + "/balance").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).showBalance(accountNo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "abcd", "123456789012345678901234567890" })
	@NullSource
	void testHandleShowBalance_AccountNoInputInvalid_ShouldThrowException(String accountNo) throws Exception {
		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + accountNo + "/balance").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isBadRequest()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		assertEquals("Provided input is of invalid format.", responseContent);
	}

	@Test
	void testHandleShowBalance_ValidAccountNo_ShouldReturnValue() throws Exception {
		Long accountNo = 1L;
		Double balance = 100.0;

		when(bankService.showBalance(accountNo)).thenReturn(balance);

		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + accountNo + "/balance").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals("Account balance for Account No. " + accountNo + ": Rs. " + balance, responseContent);
		verify(bankService, times(1)).showBalance(accountNo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "abcd", "123456789012345678901234567890" })
	@NullSource
	void testHandleDeposit_AccountNoInputInvalid_ShouldThrowException(String accountNo) throws Exception {
		SingleAccountTransactionRequest depositTransactionRequest = new SingleAccountTransactionRequest();
		depositTransactionRequest.setAmount(10.0);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(depositTransactionRequest)))
				.andExpect(status().isBadRequest()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		assertEquals("Provided input is of invalid format.", responseContent);
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	void testHandleDeposit_InvalidAccountNo_ShouldThrowException(Long accountNo) throws Exception {
		SingleAccountTransactionRequest depositTransactionRequest = new SingleAccountTransactionRequest();
		depositTransactionRequest.setAmount(10.0);

		when(bankService.deposit(accountNo, 10.0)).thenThrow(AccountNotFoundException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(depositTransactionRequest)))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).deposit(accountNo, 10.0);
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	void testHandleDeposit_NegativeAmount_ShouldThrowException(Double amount) throws Exception {
		Long accountNo = 1L;
		SingleAccountTransactionRequest depositTransactionRequest = new SingleAccountTransactionRequest();
		depositTransactionRequest.setAmount(amount);

		when(bankService.deposit(accountNo, amount)).thenThrow(InvalidAmountException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(depositTransactionRequest)))
				.andExpect(status().isBadRequest()).andReturn();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test
	void testHandleWithdraw_ValidAmountWithLessBalance_ShouldThrowException() throws Exception {
		SingleAccountTransactionRequest withdrawTransactionRequest = new SingleAccountTransactionRequest();
		withdrawTransactionRequest.setAmount(10.0);

		when(bankService.withdraw(1L, 10.0)).thenThrow(AccountBalanceException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1 + "/transaction/withdraw").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(withdrawTransactionRequest)))
				.andExpect(status().isBadRequest()).andReturn();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test
	void testHandleDeposit_ValidAmount_ShouldReturnValue() throws Exception {
		Long accountNo = 1L;
		Double amount = 100.0;

		Transaction expectedTransaction = new Transaction();
		expectedTransaction.setTransactionId(1L);
		expectedTransaction.setFromAccount(null);
		expectedTransaction.setToAccount(accountNo);
		expectedTransaction.setTransactionAmount(amount);
		expectedTransaction.setTransactionType(TransactionType.CREDIT);
		expectedTransaction.setDatetime(new Date());

		SingleAccountTransactionRequest depositTransactionRequest = new SingleAccountTransactionRequest();
		depositTransactionRequest.setAmount(amount);

		when(bankService.deposit(accountNo, amount)).thenReturn(expectedTransaction);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(depositTransactionRequest)))
				.andExpect(status().isCreated()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		Transaction actualTransaction = objectMapper.readValue(responseContent, Transaction.class);
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		assertEquals(expectedTransaction, actualTransaction);
		verify(bankService, times(1)).deposit(accountNo, amount);
	}

	@Test
	void testHandleDeposit_AmountDepositActionFailed() throws Exception {
		Long accountNo = 1L;
		Double amount = 100.0;

		SingleAccountTransactionRequest depositTransactionRequest = new SingleAccountTransactionRequest();
		depositTransactionRequest.setAmount(amount);

		when(bankService.deposit(accountNo, amount)).thenThrow(TransactionFailedException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(depositTransactionRequest)))
				.andExpect(status().isInternalServerError()).andReturn();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).deposit(accountNo, amount);

	}

	@ParameterizedTest
	@ValueSource(strings = { "abcd", "123456789012345678901234567890" })
	@NullSource
	void testHandleWithdraw_AccountNoInputInvalid_ShouldThrowException(String accountNo) throws Exception {
		SingleAccountTransactionRequest withdrawTransactionRequest = new SingleAccountTransactionRequest();
		withdrawTransactionRequest.setAmount(10.0);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(withdrawTransactionRequest)))
				.andExpect(status().isBadRequest()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		assertEquals("Provided input is of invalid format.", responseContent);
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	void testHandleWithdraw_InvalidAccountNo_ShouldThrowException(Long accountNo) throws Exception {
		SingleAccountTransactionRequest withdrawTransactionRequest = new SingleAccountTransactionRequest();
		withdrawTransactionRequest.setAmount(10.0);

		when(bankService.withdraw(accountNo, 10.0)).thenThrow(AccountNotFoundException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(withdrawTransactionRequest)))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).withdraw(accountNo, 10.0);
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	void testHandleWithdraw_NegativeAmount_ShouldThrowException(Double amount) throws Exception {
		Long accountNo = 1L;
		SingleAccountTransactionRequest withdrawTransactionRequest = new SingleAccountTransactionRequest();
		withdrawTransactionRequest.setAmount(amount);

		when(bankService.withdraw(accountNo, amount)).thenThrow(InvalidAmountException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(withdrawTransactionRequest)))
				.andExpect(status().isBadRequest()).andReturn();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test
	void testHandleWithdraw_ValidAmount_ShouldReturnValue() throws Exception {
		Long accountNo = 1L;
		Double amount = 100.0;

		Transaction expectedTransaction = new Transaction();
		expectedTransaction.setTransactionId(1L);
		expectedTransaction.setFromAccount(accountNo);
		expectedTransaction.setToAccount(null);
		expectedTransaction.setTransactionAmount(amount);
		expectedTransaction.setTransactionType(TransactionType.DEBIT);
		expectedTransaction.setDatetime(new Date());

		SingleAccountTransactionRequest withdrawTransactionRequest = new SingleAccountTransactionRequest();
		withdrawTransactionRequest.setAmount(amount);

		when(bankService.withdraw(accountNo, amount)).thenReturn(expectedTransaction);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(withdrawTransactionRequest)))
				.andExpect(status().isCreated()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		Transaction actualTransaction = objectMapper.readValue(responseContent, Transaction.class);
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		assertEquals(expectedTransaction, actualTransaction);
		verify(bankService, times(1)).withdraw(accountNo, amount);
	}

	@Test
	void testHandleWithdraw_AmountDepositActionFailed() throws Exception {
		Long accountNo = 1L;
		Double amount = 100.0;

		SingleAccountTransactionRequest withdrawTransactionRequest = new SingleAccountTransactionRequest();
		withdrawTransactionRequest.setAmount(amount);

		when(bankService.withdraw(accountNo, amount)).thenThrow(TransactionFailedException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(withdrawTransactionRequest)))
				.andExpect(status().isInternalServerError()).andReturn();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).withdraw(accountNo, amount);

	}

	@ParameterizedTest
	@ValueSource(strings = { "abcd", "123456789012345678901234567890" })
	@NullSource
	void testHandleFundTransfer_FromAccountNoInputInvalid_ShouldThrowException(String accountNo) throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(2L);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		assertEquals("Provided input is of invalid format.", responseContent);
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	void testHandleFundTransfer_InvalidFromAccountNo_ShouldThrowException(Long accountNo) throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(2L);

		when(bankService.fundTransfer(accountNo, 2L, 10.0)).thenThrow(AccountNotFoundException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + accountNo + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).fundTransfer(accountNo, 2L, 10.0);
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	void testHandleFundTransfer_InvalidToAccountNo_ShouldThrowException(Long accountNo) throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(accountNo);

		when(bankService.fundTransfer(1L, accountNo, 10.0)).thenThrow(AccountNotFoundException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1L + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).fundTransfer(1L, accountNo, 10.0);
	}

	@Test
	void testHandleFundTransfer_SameAccount_ShouldThrowException() throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(1L);

		when(bankService.fundTransfer(1L, 1L, 10.0)).thenThrow(InvalidFundTransferException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1L + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest()).andReturn();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).fundTransfer(1L, 1L, 10.0);
	}

	@Test
	void testHandleFundTransfer_FromValidAccountNoWithLowBalance_ShouldThrowException() throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(2L);

		when(bankService.fundTransfer(1L, 2L, 10.0)).thenThrow(AccountBalanceException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1L + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest()).andReturn();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).fundTransfer(1L, 2L, 10.0);
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	void testHandleFundTransfer_NegativeAmount_ShouldThrowException(Double amount) throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(amount);
		request.setToAccountNo(2L);

		when(bankService.fundTransfer(1L, 2L, amount)).thenThrow(InvalidAmountException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1L + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest()).andReturn();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).fundTransfer(1L, 2L, amount);
	}

	@Test
	void testHandleFundTransfer_ValidCase_ShouldReturnValue() throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(2L);

		Transaction expectedTransaction = new Transaction();
		expectedTransaction.setTransactionId(1L);
		expectedTransaction.setDatetime(new Date());
		expectedTransaction.setFromAccount(1L);
		expectedTransaction.setToAccount(2L);
		expectedTransaction.setTransactionAmount(10.0);
		expectedTransaction.setTransactionType(TransactionType.TRANSFER);

		when(bankService.fundTransfer(1L, 2L, 10.0)).thenReturn(expectedTransaction);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1L + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isCreated()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		Transaction actualTransaction = objectMapper.readValue(responseContent, Transaction.class);
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		assertEquals(expectedTransaction, actualTransaction);
		verify(bankService, times(1)).fundTransfer(1L, 2L, 10.0);
	}

	@Test
	void testHandleFundTransfer_SaveActionFails() throws Exception {
		InterAccountTransactionRequest request = new InterAccountTransactionRequest();
		request.setAmount(10.0);
		request.setToAccountNo(2L);

		when(bankService.fundTransfer(1L, 2L, 10.0)).thenThrow(TransactionFailedException.class);

		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult result = mockMvc
				.perform(post("/api/v1/account/" + 1L + "/transaction/fund-transfer")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isInternalServerError()).andReturn();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).fundTransfer(1L, 2L, 10.0);

	}

	@ParameterizedTest
	@ValueSource(strings = { "abcd", "123456789012345678901234567890" })
	@NullSource
	void testHandleGetAllTransactions_AccountNoInputInvalid_ShouldThrowException(String accountNo) throws Exception {
		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + accountNo + "/transactions").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isBadRequest()).andReturn();

		String responseContent = result.getResponse().getContentAsString();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		assertEquals("Provided input is of invalid format.", responseContent);
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	void testHandleGetAllTransactions_InvalidAccountNo_ShouldThrowException(Long accountNo) throws Exception {
		
		when(bankService.getAllTransactionDetails(accountNo)).thenThrow(AccountNotFoundException.class);

		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + accountNo + "/transactions").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).getAllTransactionDetails(accountNo);
	}

	@Test
	void testHandleGetAllTransactions_ValidAccountNoButNoTransactions_ShouldThrowException()
			throws Exception {
		when(bankService.getAllTransactionDetails(1L)).thenThrow(NoTransactionsFoundException.class);
		
		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + 1L + "/transactions").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isNoContent()).andReturn();

		assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).getAllTransactionDetails(1L);
	}

	@Test
	void testGetAllTransactions_ValidAccountNo_ShouldReturn10Txns() throws Exception {
		List<Transaction> dummyTransactions = new ArrayList<>();

		for (long i = 0; i < 10; i++) {
			Transaction transaction = new Transaction();
			transaction.setTransactionId(i);
			transaction.setTransactionType(TransactionType.CREDIT);
			transaction.setToAccount(1L);
			transaction.setTransactionAmount(10d);
			dummyTransactions.add(transaction);
		}

		when(bankService.getAllTransactionDetails(1L)).thenReturn(dummyTransactions);

		MvcResult result = mockMvc
				.perform(get("/api/v1/account/" + 1L + "/transactions").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andReturn();

		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		verify(bankService, times(1)).getAllTransactionDetails(1L);

	}

}
