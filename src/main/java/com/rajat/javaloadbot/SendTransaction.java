package com.rajat.javaloadbot;

import java.math.BigInteger;

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
class SendTransaction implements Runnable {
    
    static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));//RPC SERVER
    
    public String getRecipientAddress(int i) {
		try {
			String password = "Rajat123";
            String mnemonic = "envelope direct allow creek endless detect mountain squeeze mass welcome virtual sample";

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
			BigInteger gasPrice = Convert.toWei("1", Unit.GWEI).toBigInteger();

			// Prepare the rawTransaction
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit,
					recipientAddress, value);

			long chainId = 1212;
			// Sign the transaction
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
			String hexValue = Numeric.toHexString(signedMessage);

			// Send transaction
			EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
			String transactionHash = ethSendTransaction.getTransactionHash();
			System.out.println("transactionHash "+i+" : "+ transactionHash);

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

	@Async
	public void sendMultipleTransaction(Credentials[] senderCredentials) {
        try {
			for (int i = 0; i < 10000; i++) {
				for (int j = 0; j < senderCredentials.length; j++) {
					System.out.println("i="+i);
					sendTransactionFunc(i, senderCredentials[j]);
				}
			}

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
