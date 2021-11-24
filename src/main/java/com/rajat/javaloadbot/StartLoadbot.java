package com.rajat.javaloadbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

@Service
public class StartLoadbot {

	@Value("${KEYFILE}")
	private String keyFile;

	@Autowired
	SendTransaction sendTransaction;

	static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));//RPC SERVER

	@Async
    public void startLoadbot() {
        try {
			// ExecutorService service = Executors.newFixedThreadPool(8);
            Credentials credentials = getSenderAccount();
            
			// SendTransaction sendTransaction = new SendTransaction();
			for (int i = 76000; i < 96000; i++) {
				System.out.println("i="+i);
				// service.execute(new SendTransaction(i, credentials));
				sendTransaction.sendTransactionFunc(i, credentials);
			}

        } catch (Exception e) {
            System.out.println("Error in sendTransaction(): "+e);
        }
    }

	public Credentials getSenderAccount() {
		try {
			String walletPassword = "Rajat123";
            String walletPath = "/home/rajat/geth-git/test-chain-dir/keystore/" + keyFile;
            // Decrypt and open the wallet into a Credential object
            Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletPath);
			System.out.println("Sender Account address: " + credentials.getAddress());
			System.out.println("Sender Balance: "
					+ Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
							.send().getBalance().toString(), Unit.ETHER));
			return credentials;
		} catch (Exception e) {
			System.out.println("Error in getSenderAccount(): "+e);
			return null;
		}
	}

	@Async
	public void test1() throws Exception {
		// ExecutorService service = Executors.newFixedThreadPool(8);
		for (int i = 0; i < 100; i++) {
			System.out.println("i= "+i+"\tThread: Thread: "+Thread.currentThread().getName());
			sendTransaction.test2(i);
			// service.execute(new SendTransaction());
		}
	}

}
