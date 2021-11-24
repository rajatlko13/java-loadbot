package com.rajat.javaloadbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaLoadbotApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(JavaLoadbotApplication.class, args);
		System.out.println("Connecting to Ethereum ...");
		// Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));//RPC SERVER
		// System.out.println("Successfuly connected to Ethereum");

		try {
			// Web3ClientVersion clientVersion = web3.web3ClientVersion().send();
			// System.out.println("Client version: " + clientVersion.getWeb3ClientVersion());

			// EthGasPrice gasPrice = web3.ethGasPrice().send();
			// System.out.println("Default Gas Price: "+gasPrice.getGasPrice());

			// EthGetBalance ethGetBalance = web3
			// 		.ethGetBalance("0x0766fd8a11485bb8c045919ac5a07c51b3d1696b", DefaultBlockParameterName.LATEST)
			// 		.sendAsync().get();

			// System.out.println("Balance: of Account '0x0766fd8a11485bb8c045919ac5a07c51b3d1696b' "
			// + ethGetBalance.getBalance());
			
			// System.out.println("Balance in Ether format: "
			// +Convert.fromWei(web3.ethGetBalance("0x0766fd8a11485bb8c045919ac5a07c51b3d1696b",
			// DefaultBlockParameterName.LATEST).send().getBalance().toString(),Unit.ETHER));

			// new StartLoadbot().test1();

		} catch (Exception ex) {
			throw new RuntimeException("Error whilst sending json-rpc requests", ex);
		}

	}

}
