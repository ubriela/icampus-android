/**
 * 
 */
package edu.usc.imsc.trams.app;

/**
 * @author   Ling
 */
public enum RouteName {

	ROUTEA(937),
	ROUTEB(938),
	ROUTEC(691),
	PARKINGCENTER(919),
	UNKNOWN(-1);
	
	private int routeid;

	private RouteName(int routeid) {
		this.routeid = routeid;
	}
	
	public static String getRouteNameById(int routeid) {
		switch (routeid) {
		case 937: return "Route A";
		case 938: return "Route B";
		case 691: return "Route C";
		case 919: return "Parking Center";
		default: return "Unknown route";
		}
	}
	
	public static RouteName getRouteEnumById(int routeid) {
		switch (routeid) {
		case 937: return ROUTEA;
		case 938: return ROUTEB;
		case 691: return ROUTEC;
		case 919: return PARKINGCENTER;
		default: return UNKNOWN;
		}
	}
}
