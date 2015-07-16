package edu.usc.imsc.spatial;

import java.util.LinkedList;
import java.util.List;

/**
 * @author linghu
 */
public class VendorHolder {
	/**
	 * @uml.property name="instance"
	 * @uml.associationEnd
	 */
	static VendorHolder instance;
	/**
	 * @uml.property name="vendors"
	 */
	List<Vendor> vendors;

	public VendorHolder() {
		vendors = new LinkedList<Vendor>();
	}

	/**
	 * @return
	 * @uml.property name="vendors"
	 */
	public List<Vendor> getVendors() {
		return vendors;
	}

	/**
	 * @param vendors
	 * @uml.property name="vendors"
	 */
	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}

	/**
	 * @return
	 * @uml.property name="instance"
	 */
	public VendorHolder getInstance() {
		if (instance == null) {
			return new VendorHolder();
		} else {
			return instance;
		}
	}
}
