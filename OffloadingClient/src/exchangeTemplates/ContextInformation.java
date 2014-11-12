package exchangeTemplates;

import java.net.InetAddress;

public class ContextInformation {
	private InetAddress nodeAddress;
	private NodeType nodeType;
	private double availableFrom;
	private double availableTo;
	private double availableProcessingPower;
	public InetAddress getNodeAddress() {
		return nodeAddress;
	}
	public void setNodeAddress(InetAddress nodeAddress) {
		this.nodeAddress = nodeAddress;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public double getAvailableFrom() {
		return availableFrom;
	}
	public void setAvailableFrom(double availableFrom) {
		this.availableFrom = availableFrom;
	}
	public double getAvailableTo() {
		return availableTo;
	}
	public void setAvailableTo(double availableTo) {
		this.availableTo = availableTo;
	}
	public double getAvailableProcessingPower() {
		return availableProcessingPower;
	}
	public void setAvailableProcessingPower(double availableProcessingPower) {
		this.availableProcessingPower = availableProcessingPower;
	}
	
}
