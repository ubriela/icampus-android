package edu.usc.imsc.spatial;

/**
 * @author linghu
 */
public class Vendor extends AbstractPointPlace implements Comparable<Vendor> {
	/**
	 * @uml.property name="category"
	 */
	private String category;

	/**
	 * @return
	 * @uml.property name="category"
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 * @uml.property name="category"
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	public Vendor() {
	}

	public Vendor(int id, String name, String category, double latitude,
			double longitude) {
		this(id, name, category);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Vendor(int id, String name, String category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public int compareTo(Vendor arg0) {
		return (this.name.compareTo(arg0.name));
	}

}
