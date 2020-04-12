package fr.slixe.exchange.structure;

public class Address {

	private String address;
	private String paymentId;

	public Address() {}

	public Address(String address)
	{
		this.address = address;
	}

	public Address(String address, String paymentId)
	{
		this.address = address;
		this.paymentId = paymentId;
	}
	
	public String getAddress()
	{
		return address;
	}

	public String getPaymentId()
	{
		return paymentId;
	}
}
