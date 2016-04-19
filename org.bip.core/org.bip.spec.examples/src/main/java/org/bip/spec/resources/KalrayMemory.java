package org.bip.spec.resources;

import org.bip.annotations.ComponentType;
import org.bip.annotations.Data;
import org.bip.annotations.Guard;
import org.bip.annotations.Port;
import org.bip.annotations.Ports;
import org.bip.annotations.Transition;
import org.bip.api.PortType;
import org.bip.api.ResourceProxy;
import org.bip.api.DataOut.AccessType;
import org.bip.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ports({ @Port(name = "create", type = PortType.enforceable),
		@Port(name = "read", type = PortType.enforceable),
		@Port(name = "deleteData", type = PortType.enforceable)})
@ComponentType(initial = "0", name = "org.bip.spec.resources.KalrayMemory")
public class KalrayMemory implements ResourceProxy {

	Logger logger = LoggerFactory.getLogger(KalrayMemory.class);

	private String data = "";
	private int counter;
	private String id;

	private MemoryManager rManager;
	private DataMemoryManager dManager;

	public KalrayMemory(String id) {
		this.id = id;
	}

	public KalrayMemory(String id, MemoryManager manager) {
		this.id = id;
		this.rManager = manager;
	}
	
	public KalrayMemory(String id, MemoryManager manager, DataMemoryManager dManager) {
		this.id = id;
		this.rManager = manager;
		this.dManager = dManager;
	}
	
	@Override
	public String resourceID() {
		return id;
	}

	@Transition(name = "create", source = "0", target = "1", guard = "idOK")
	public void createData(@Data(name = "data") String storedDataName,
			@Data(name = "count") int readCount) {
		this.data = storedDataName;
		this.counter = readCount;
		System.err.println("Data " + storedDataName + " created in memory "
				+ id);
		rManager.notifyCreation(storedDataName, readCount);
		// Q: how to understand the type of usage? how can I know, that m is asked because of D?
		// doubling the data? m_x and md_x?
		//or dynamically change the links? since we know there is a transition between D and m_x, we know there cannot be a transition between m and m_x ->
		// not true if we allocate parts of resources.
	}

	@Transition(name = "read", source = "1", target = "1", guard = "dataIdOK")
	public void readData() {
		counter--;
	}

	@Transition(name = "deleteData", source = "1", target = "0", guard = "noUse")
	public void deleteData() {
		this.data = "";
		rManager.notifyDeletion(data);
	}

	@Guard(name = "noUse")
	public boolean dataIsNotUsed() {
		//System.err.println("noUse in " + this.id + " for data " + this.data + " " + (counter == 0));
		return counter == 0;
	}

	@Guard(name = "idOK")
	public boolean interactionAllowed(@Data(name = "id") String givenId) {
		//System.out.println("In memory " + this.id + " checking for allowance " + givenId);
		return this.id == givenId || ("d" + this.id == givenId );
	}
	
	@Guard(name = "dataIdOK")
	public boolean dinteractionAllowed(@Data(name = "dataID") String givenId) {
		return this.id.equals(givenId) || (givenId.equals("d" + this.id));
	}

	@Data(name = "dataName", accessTypePort = AccessType.allowed, ports = {"deleteData"})
	public String generatedDataName() {
		return data;
	}
	
}