package it.polito.dp2.vehicle.application;

public class Edge {

	private NodeApp from, to;
	private String portFrom, portTo;
	
	public Edge(NodeApp from, NodeApp to, String portFrom, String portTo) {
		this.from = from;
		this.to = to;
		this.portFrom = portFrom;
		this.portTo = portTo;
	}

	public NodeApp getFrom() {
		return from;
	}

	public void setFrom(NodeApp from) {
		this.from = from;
	}

	public NodeApp getTo() {
		return to;
	}

	public void setTo(NodeApp to) {
		this.to = to;
	}

	public String getPortFrom() {
		return portFrom;
	}

	public void setPortFrom(String portFrom) {
		this.portFrom = portFrom;
	}

	public String getPortTo() {
		return portTo;
	}

	public void setPortTo(String portTo) {
		this.portTo = portTo;
	}
	
	
	
}
