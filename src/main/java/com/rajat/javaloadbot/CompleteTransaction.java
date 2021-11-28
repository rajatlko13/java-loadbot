package com.rajat.javaloadbot;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

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
public class CompleteTransaction {
    
	static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));//RPC SERVER
	
	@Async
	public CompletableFuture<Integer> sendTransactionFunc(int i, int j, Credentials credentials) {
		try {
            System.out.println("i= "+i+"  j= "+j+"   Thread= "+Thread.currentThread().getName());
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

		} catch (Exception e) {
			System.out.println("Error in sendTransaction(): "+e);
		}
		return CompletableFuture.completedFuture(1);
	}

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

}
