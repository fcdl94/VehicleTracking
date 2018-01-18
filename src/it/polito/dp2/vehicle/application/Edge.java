package it.polito.dp2.vehicle.application;

import it.polito.dp2.vehicle.model.NodeRef;

public class Edge {

	private NodeApp from, to;
	private NodeRef nrFrom, nrTo;
	
	public Edge(NodeApp from, NodeApp to, NodeRef fromNR, NodeRef toNR) {
		this.from = from;
		this.to = to;
		this.nrFrom = fromNR;
		this.nrTo = toNR;
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

	public NodeRef getnrFrom() {
		return nrFrom;
	}

	public void setnrFrom(NodeRef nrFrom) {
		this.nrFrom = nrFrom;
	}

	public NodeRef getnrTo() {
		return nrTo;
	}

	public void setnrTo(NodeRef nrTo) {
		this.nrTo = nrTo;
	}
		
}
