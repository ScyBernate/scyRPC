package ot.tch.rpc.provider;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootStrapApplication{

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
	}

}
