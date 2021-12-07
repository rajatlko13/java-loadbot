package com.rajat.javaloadbot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

@Service
class SendTransaction {

	@Value("${RPC_SERVER}")
	private String RPC_SERVER;

	@Value("${MNEMONIC}")
	private String MNEMONIC;

	@Value("${CHAIN_ID}")
	private String CHAIN_ID;
    
    static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));//RPC SERVER

	@Autowired
	CompleteTransaction completeTransaction;
    
    public String getRecipientAddress(int i) {
		try {
			String password = "Rajat123";
            String mnemonic = MNEMONIC;

            //Derivation path wanted: // m/44'/60'/0'/i
            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, i};

            // Generate a BIP32 master keypair from the mnemonic phrase
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

            // Derived the key using the derivation path
            Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

            // Load the wallet for the derived key
            Credentials credentials = Credentials.create(derivedKeyPair);
			System.out.println("Recipient "+i+" : "+credentials.getAddress());
			return credentials.getAddress();

		} catch (Exception e) {
			System.out.println("Error in getRecipientAccount(): "+e);
			return null;
		}
	}

	@Async
	public void sendTransactionFunc(int i, Credentials credentials) {
		try {
			// System.out.println("Thread: "+Thread.currentThread().getName());
			// Get the latest nonce of current account
			// EthGetTransactionCount ethGetTransactionCount = web3
			// 		.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
			// BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			BigInteger nonce = BigInteger.valueOf(i);

			// Recipient address
			String recipientAddress = getRecipientAddress(i);
			
			// Value to transfer (in wei)
			String amountToBeSent="1";
			BigInteger value = new BigInteger(amountToBeSent);

			// Gas Parameter
			BigInteger gasLimit = BigInteger.valueOf(21000);
			BigInteger gasPrice = Convert.toWei("20", Unit.WEI).toBigInteger();
			// BigInteger gasPrice = BigInteger.valueOf(10);

			// Prepare the rawTransaction
			// RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit,
			// 		recipientAddress, value);
			
			// BigInteger maxPriorityFeePerGas = Convert.toWei("2", Unit.WEI).toBigInteger();
			// BigInteger maxFeePerGas = Convert.toWei("10", Unit.WEI).toBigInteger();
			long chainId = Long.parseLong(CHAIN_ID);
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(chainId , nonce, gasLimit,
					recipientAddress, value, BigInteger.valueOf(2), BigInteger.valueOf(100));

			// Sign the transaction
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
			String hexValue = Numeric.toHexString(signedMessage);

			// Send transaction
			EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
			String transactionHash = ethSendTransaction.getTransactionHash();
			System.out.println("transactionHash "+i+" : "+ transactionHash);

			// TransactionReceipt transactionReceipt = Transfer.sendFundsEIP1559(
			// 					web3, 
			// 					credentials,
			// 					recipientAddress, //toAddress
			// 					BigDecimal.valueOf(1), //value
			// 					Convert.Unit.WEI, //unit
			// 					BigInteger.valueOf(21000), //gasLimit
			// 					BigInteger.valueOf(3),//maxPriorityFeePerGas
			// 					BigInteger.valueOf(10)//maxFeePerGas
			// 				).send();
			// System.out.println("Transaction Hash: "+ transactionReceipt.getTransactionHash());

			// Wait for transaction to be mined
			// Optional<TransactionReceipt> transactionReceipt = null;
			// do {
			// 	System.out.println("checking if transaction " + transactionHash + " is mined....");
			// 	EthGetTransactionReceipt ethGetTransactionReceiptResp = web3.ethGetTransactionReceipt(transactionHash)
			// 			.send();
			// 	transactionReceipt = ethGetTransactionReceiptResp.getTransactionReceipt();
			// 	Thread.sleep(3000); // Wait for 3 sec
			// } while (!transactionReceipt.isPresent());

			// System.out.println("Transaction " + transactionHash + " was mined in block # "
			// 		+ transactionReceipt.get().getBlockNumber());
			// System.out.println("Balance: "
			// 		+ Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
			// 				.send().getBalance().toString(), Unit.ETHER));

		} catch (Exception e) {
			System.out.println("Error in sendTransaction(): "+e);
		}
	}
	
	public void sendMultipleTransaction(Credentials[] senderCredentials, int i) {
        try {
			List<CompletableFuture<Integer>> futureResultList1 = new ArrayList<CompletableFuture<Integer>>();
			for (int j = 0; j < senderCredentials.length; j++) {
				CompletableFuture<Integer> res1 = completeTransaction.sendTransactionFunc(i, j, senderCredentials[j]);
				futureResultList1.add(res1);
			}

			CompletableFuture[] futureResultArray1 = futureResultList1.toArray(new CompletableFuture[futureResultList1.size()]);
			CompletableFuture<Void> combinedFuture1 = CompletableFuture.allOf(futureResultArray1);
			// sendTransactionFunc(i, senderCredentials[j]);
        } catch (Exception e) {
            System.out.println("Error in startMultipleTransaction(): "+e);
        }
    }
    
	@Async
	public void test2(int i) throws Exception {
		System.out.println("i= "+i+"\tThread: Thread: "+Thread.currentThread().getName());
		// Thread.sleep(2000);
	}

	public void run() {
		System.out.println("Thread: "+Thread.currentThread().getName());
		// Thread.sleep(2000);
	}

}
